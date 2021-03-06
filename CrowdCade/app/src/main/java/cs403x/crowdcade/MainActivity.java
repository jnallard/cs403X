package cs403x.crowdcade;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    MapListener mapFindListener = new MapListener(this, false);
    MapListener mapReportListener = new MapListener(this, true);

    /**
     * Used to store the last screen title. For use in .
     */
    private CharSequence mTitle;

    private MainActivity activity = this;

    // Report tab UI elements
    private EditText gameNameText;
    private EditText locationNameText;
    private EditText searchText;
    private EditText addressText;
    private RatingBar conditionRatingBar;
    private Button submitButton;
    private Button searchButton;
    private Button moreInfoButton;
    private ImageButton cameraButton;
    private TabHost tabHost;
    private TextView gameNameLabelFind;
    private TextView locationNameLabelFind;

    private double locationLat;
    private double locationLon;

    private static final int DEFAULT_DISPLAY_COUNT = 10;

    private List<ArcadeEntry> arcadeEntryList;

    private Map<String, ArcadeEntry> markerToEntry = new HashMap<>();
    private Map<Long, Marker> entryToMarker = new HashMap<>();

    private static final int REQUEST_PHOTO = 0;

    private File photoFile;
    private Bitmap currentImage = null;

    private ArcadeEntry selectedArcadeEntry;

    public static Typeface tf;


    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;

    //Use this runnable to determine what happens after the arcade locations are loaded.
    public ResponseRunnable arcadeEntriesLoaded = new ResponseRunnable(activity) {

        @Override
        public void runOnMainThread() {
            arcadeEntryList = ArcadeEntry.fromJSONArray(data);
            mapFindListener.map.clear();
            Log.d("entries", arcadeEntryList.size() + "");

            markerToEntry.clear();
            entryToMarker.clear();

            for (ArcadeEntry entry : arcadeEntryList) {
                LatLng entryMarkerLoc = new LatLng(entry.locationLat, entry.locationLon);
                Marker marker = mapFindListener.map.addMarker(new MarkerOptions().position(entryMarkerLoc).title(entry.locationName));
                markerToEntry.put(marker.getId(), entry);
                entryToMarker.put(entry.getId(), marker);
            }
        }
    };

    //Use this runnable to determine what happens after the term has been searched
    public ResponseRunnable arcadeEntriesSearched = new ResponseRunnable(activity) {

        @Override
        public void runOnMainThread() {
            arcadeEntryList = ArcadeEntry.fromJSONArray(data);
            Log.d("entries", arcadeEntryList.size() + "");
        }
    };

    //Use this runnable to determine what happens after the arcade entry is reported
    public ResponseRunnable arcadeEntryAdded = new ResponseRunnable(activity) {

        @Override
        public void runOnMainThread() {
            arcadeEntryList = ArcadeEntry.fromJSONArray(data);
            Log.d("entries", arcadeEntryList.size() + "");
            makeToast("Entry Saved");
            tabHost.setCurrentTab(0);
        }
    };

    //Use this runnable to determine what happens after the arcade entry is reported
    public ResponseRunnable arcadeEntryVisited = new ResponseRunnable(activity) {

        @Override
        public void runOnMainThread() {
            arcadeEntryList = ArcadeEntry.fromJSONArray(data);
            Log.d("entries", arcadeEntryList.size() + "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_LOCATION);

        }

        activity = this;
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));

        tf = Typeface.createFromAsset(getAssets(), "fonts/PressStart2P-Regular.ttf");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapFindListener);


        SupportMapFragment mapFragmentReport = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_report);

        mapFragmentReport.getMapAsync(mapReportListener);

        setupTabHost();

        //Testing
        //ArcadeEntry testEntry = new ArcadeEntry("test2", "home", "45 street st.", 0, 0, 4.3);
        //testEntry.setId(1);
        //NetworkManager.getInstance().reportArcadeEntry(testEntry, arcadeEntryAdded);
        //NetworkManager.getInstance().reportArcadeEntryVisited(testEntry, arcadeEntryVisited);

        //Initialize UI elements
        gameNameText = (EditText) findViewById(R.id.gameNameText);
        locationNameText = (EditText) findViewById(R.id.locationText);
        searchText = (EditText) findViewById(R.id.gameNameSearchBox);
        conditionRatingBar = (RatingBar) findViewById(R.id.conditionRatingBar);
        gameNameLabelFind = (TextView) findViewById(R.id.gameNameFind);
        locationNameLabelFind = (TextView) findViewById(R.id.locationNameFind);
        addressText = (EditText) findViewById(R.id.addressLabel);

        photoFile = getPhotoFile();

        initializeSubmitButton();
        initializeCameraButton();
        initializeMoreInfoButton();
        initializeSearchButton();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (extras.containsKey("fromNotification")) {
                boolean fromNotification = extras.getBoolean("fromNotification");
                if (fromNotification) {
                    TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
                    tabHost.setCurrentTab(1);
                }
            }
        }
    }


    private void setupTabHost() {
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("find");
        tabSpec.setContent(R.id.tabFind);
        tabSpec.setIndicator("Find");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("report");
        tabSpec.setContent(R.id.tabReport);
        tabSpec.setIndicator("Report");
        tabHost.addTab(tabSpec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tab) {
                //Getting locations happens ASYNC. Modify the runnable to change behavior
                NetworkManager.getInstance().getArcadeEntries(arcadeEntriesLoaded);
            }

        });
    }

    /**
     * Initializes the submit button and its click listener
     */
    private void initializeSubmitButton() {
        submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllInfoAdded = true;

                // Check game name
                String gameName = gameNameText.getText().toString().trim();
                if (gameName.length() <= 0) {
                    isAllInfoAdded = false;
                }

                // Check location name
                String locationName = locationNameText.getText().toString().trim();
                if (locationName.length() <= 0) {
                    isAllInfoAdded = false;
                }

                // Check location name
                String address = addressText.getText().toString().trim();
                if (address.length() <= 0) {
                    isAllInfoAdded = false;
                }

                // Check location coordinates
                LatLng selectedLocation = mapReportListener.getSelectedLocation();
                if (selectedLocation == null) {
                    isAllInfoAdded = false;
                }

                // Check condition
                float conditionValue = conditionRatingBar.getRating();
                if (conditionValue == 0) {
                    isAllInfoAdded = false;
                }

                // Check if it has a photo
                boolean hasPhoto = true;
                if (currentImage == null) {
                    hasPhoto = false;
                }

                if (isAllInfoAdded) {
                    gameNameText.setText("");
                    addressText.setText("");
                    locationNameText.setText("");
                    //mapReportListener.map.clear();
                    mapReportListener.initializeOnClickListener();
                    conditionRatingBar.setRating(0f);
                    cameraButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
                    ArcadeEntry newEntry;

                    if (hasPhoto) {
                        newEntry = new ArcadeEntry(gameName, locationName, address, selectedLocation.latitude, selectedLocation.longitude, conditionValue, currentImage);
                    } else {
                        newEntry = new ArcadeEntry(gameName, locationName, address, selectedLocation.latitude, selectedLocation.longitude, conditionValue);
                    }

                    NetworkManager.getInstance().reportArcadeEntry(newEntry, arcadeEntryAdded);

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO) {
            receiveArcadePhoto();
        }
    }

    /**
     * Initializes the camera button and its click listener
     */
    private void initializeCameraButton() {
        cameraButton = (ImageButton) findViewById(R.id.addPhotoButton);

        cameraButton.setMaxHeight(cameraButton.getHeight());
        cameraButton.setMaxWidth(cameraButton.getWidth());

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager packageManager = getPackageManager();

        boolean canTakePhoto = photoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        cameraButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(photoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
    }

    /**
     * Receives the photo of the arcade cabinet from the camera and sets the camera image button src
     * to the received photo
     */
    private void receiveArcadePhoto()
    {
        if (photoFile != null || photoFile.exists()) {
            Bitmap bitmap = getScaledBitmap(photoFile.getPath(), this);
            if (bitmap != null) {
                currentImage = bitmap;
                cameraButton.setImageBitmap(currentImage);
            }
        }
    }

    public void makeToast(final String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Creates a file for the photo of the arcade machine based on its unique ID
     * @return - The file for the arcade photo
     */
    public File getPhotoFile() {
        File externalFilesDir = getApplicationContext()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, "IMG_" + createUniqueArcadeID());
    }

    /**
     * Initializes the search button in the find game screen
     */
    private void initializeSearchButton()
    {
        searchButton = (Button) findViewById(R.id.searchGameNameButton);
        final MainActivity mainActivity = this;
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String gameToSearch = searchText.getText().toString();
                new SearchDialog(mainActivity, gameToSearch);
            }

        });
    }

    private void initializeMoreInfoButton()
    {
        moreInfoButton = (Button) findViewById(R.id.moreInfoButton);
        moreInfoButton.setEnabled(false);
        final MainActivity main = this;
        moreInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(selectedArcadeEntry != null)
                    new MoreDetailsDialog(main, selectedArcadeEntry);
            }

        });
    }

    /**
     * Creates the unique ID for the arcade entry
     * @return - The ID for the arcade entry
     */
    private String createUniqueArcadeID()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * Used for scaling bitmaps that are received from the camera
     * @param path
     * @param activity
     * @return - The scaled bitmap
     */
    public Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    /**
     * Used for scaling bitmaps that are received from the camera
     * @param path - The path of the image
     * @param destWidth
     * @param destHeight
     * @return - The scaled bitmap
     */
    public Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    public void updateSelectedMarker(String markerId){

        moreInfoButton.setEnabled(true);
        selectedArcadeEntry = markerToEntry.get(markerId);
        if (selectedArcadeEntry != null) {
            gameNameLabelFind.setText(selectedArcadeEntry.getName());
            locationNameLabelFind.setText(selectedArcadeEntry.getLocationName());
        }
    }

    public void selectSearchResults(ArcadeEntry result){
        Marker marker = entryToMarker.get(result.getId());
        updateSelectedMarker(marker.getId());
        mapFindListener.map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    public static void setTypeFace(TextView textView){
        textView.setTypeface(tf);
    }
}
