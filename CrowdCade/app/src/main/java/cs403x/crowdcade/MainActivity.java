package cs403x.crowdcade;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Intent;
import android.media.Rating;
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    /**
     * Used to store the last screen title. For use in .
     */
    private CharSequence mTitle;

    private MainActivity activity = this;

    // Report tab UI elements
    private EditText gameNameText;
    private EditText locationNameText;
    private RatingBar conditionRatingBar;
    private Button submitButton;
    private ImageButton cameraButton;
    private TabHost tabHost;

    private double locationLat;
    private double locationLon;

    private static final int DEFAULT_DISPLAY_COUNT = 10;

    private List<ArcadeEntry> arcadeEntryList;

    private static final int REQUEST_PHOTO = 0;

    private File photoFile;
    private Bitmap currentImage = null;

    //Use this runnable to determine what happens after the arcade locations are loaded.
    ResponseRunnable arcadeEntriesLoaded = new ResponseRunnable(activity) {

        @Override
        public void runOnMainThread() {
            arcadeEntryList = ArcadeEntry.fromJSONArray(data);
            Log.d("entries", arcadeEntryList.size() + "");
        }
    };

    //Use this runnable to determine what happens after the arcade entry is reported
    ResponseRunnable arcadeEntryAdded = new ResponseRunnable(activity) {

        @Override
        public void runOnMainThread() {
            arcadeEntryList = ArcadeEntry.fromJSONArray(data);
            Log.d("entries", arcadeEntryList.size() + "");
            makeToast("Entry Saved");
            tabHost.setCurrentTab(0);
        }
    };

    //Use this runnable to determine what happens after the arcade entry is reported
    ResponseRunnable arcadeEntryVisited = new ResponseRunnable(activity) {

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

        setupTabHost();

        //Getting locations happens ASYNC. Modifty the runnable to change behavior
        NetworkManager.getInstance().getArcadeEntries(arcadeEntriesLoaded);

        //Testing
        //ArcadeEntry testEntry = new ArcadeEntry("test2", "home", "45 street st.", 0, 0, 4.3);
        //testEntry.setId(1);
        //NetworkManager.getInstance().reportArcadeEntry(testEntry, arcadeEntryAdded);
        //NetworkManager.getInstance().reportArcadeEntryVisited(testEntry, arcadeEntryVisited);

        //Initialize UI elements
        gameNameText = (EditText) findViewById(R.id.gameNameText);
        locationNameText = (EditText) findViewById(R.id.locationText);
        conditionRatingBar = (RatingBar) findViewById(R.id.conditionRatingBar);

        photoFile = getPhotoFile();

        initializeSubmitButton();
        initializeCameraButton();
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

                if (isAllInfoAdded) {

                    ArcadeEntry newEntry = new ArcadeEntry(gameName, locationName, "", 0, 0, conditionValue);
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

    private void initializeCameraButton() {
        cameraButton = (ImageButton) findViewById(R.id.addPhotoButton);

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

    public File getPhotoFile() {
        File externalFilesDir = getApplicationContext()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, "IMG_" + createUniqueArcadeID());
    }

    private int createUniqueArcadeID()
    {
        return 1234;
    }

    public Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

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
}
