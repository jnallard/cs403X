package cs403x.crowdcade;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.Toast;
import android.net.Uri;

import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    MapListener mapFindListener = new MapListener(this);
    MapListener mapReportListener = new MapListener(this);

    /**
     * Used to store the last screen title. For use in .
     */
    private CharSequence mTitle;

    private MainActivity activity = this;

    // Report tab UI elements
    private EditText gameNameText;
    private EditText locationNameText;
    private EditText searchText;
    private RatingBar conditionRatingBar;
    private Button submitButton;
    private Button searchButton;
    private Button moreInfoButton;
    private ImageButton cameraButton;
    private TabHost tabHost;

    private double locationLat;
    private double locationLon;

    private static final int DEFAULT_DISPLAY_COUNT = 10;

    private List<ArcadeEntry> arcadeEntryList;

    private static final int REQUEST_PHOTO = 0;

    private File photoFile;
    private Bitmap currentImage = null;

    private ArcadeEntry selectedArcadeEntry;

    //Use this runnable to determine what happens after the arcade locations are loaded.
    public ResponseRunnable arcadeEntriesLoaded = new ResponseRunnable(activity) {

        @Override
        public void runOnMainThread() {
            arcadeEntryList = ArcadeEntry.fromJSONArray(data);
            Log.d("entries", arcadeEntryList.size() + "");

            for (ArcadeEntry entry : arcadeEntryList) {
                LatLng entryMarker = new LatLng(entry.locationLat, entry.locationLon);
                mapFindListener.map.addMarker(new MarkerOptions().position(entryMarker).title(entry.locationName));
            }
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
        activity = this;
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));

        ;
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
                    ArcadeEntry newEntry = null;

                    if (hasPhoto) {
                        newEntry = new ArcadeEntry(gameName, locationName, "", 0, 0, conditionValue, currentImage);
                    } else {
                        newEntry = new ArcadeEntry(gameName, locationName, "", 0, 0, conditionValue);
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
            currentImage = bitmap;
            cameraButton.setImageBitmap(currentImage);
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
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String gameToSearch = searchText.getText().toString();
                List<ArcadeEntry> searchResults = new ArrayList<ArcadeEntry>();

                for(ArcadeEntry entry : arcadeEntryList)
                {
                    if (entry.name == gameToSearch){
                        searchResults.add(entry);
                    }
                }

                searchResults = sortListByDistance(searchResults);
                populateSearchResultsWindow(searchResults);
            }

        });
    }

    private void initializeMoreInfoButton()
    {
        moreInfoButton = (Button) findViewById(R.id.moreInfoButton);
        moreInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }

        });
    }

    /**
     * Creates the unique ID for the arcade entry
     * @return - The ID for the arcade entry
     */
    private int createUniqueArcadeID()
    {
        return 1234;
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

    /**
     * Sorts the given list of arcade entries by distance to the users current location
     * @param listToSort - The list of ArcadeEntry to sort
     * @return - The sorted list of ArcadeEntry
     */
    private List<ArcadeEntry> sortListByDistance(List<ArcadeEntry> listToSort)
    {
        List<ArcadeEntry> sortedList = new ArrayList<ArcadeEntry>();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        for(ArcadeEntry entry : listToSort)
        {

        }

        return sortedList;
    }

    /**
     * Creates the search results window with the given list of ArcadeEntry displayed
     * @param searchResults - The list of ArcadeEntry to display
     */
    private void populateSearchResultsWindow(List<ArcadeEntry> searchResults)
    {

    }



}
