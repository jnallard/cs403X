package cs403x.crowdcade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TabHost;

import java.util.List;

public class MainActivity extends AppCompatActivity  {


    /**
     * Used to store the last screen title. For use in .
     */
    private CharSequence mTitle;

    private static MainActivity activity;

    // Report tab UI elements
    EditText gameNameText;
    EditText locationName;
    RatingBar conditionRatingBar;

    double locationLat;
    double locationLon;

    private static final int DEFAULT_DISPLAY_COUNT = 10;

    List<ArcadeEntry> arcadeEntryList;



    //Use this runnable to determine what happens after the arcade locations are loaded.
    ResponseRunnable arcadeEntriesLoaded = new ResponseRunnable() {
        @Override
        public void setResonseData(String data) {
            arcadeEntryList = ArcadeEntry.fromJSONArray(data);
        }

        @Override
        public void run() {
            Log.d("entries", arcadeEntryList.size() + "");
        }
    };

    //Use this runnable to determine what happens after the arcade entry is reported
    ResponseRunnable arcadeEntryAdded = new ResponseRunnable() {
        @Override
        public void setResonseData(String data) {
            arcadeEntryList = ArcadeEntry.fromJSONArray(data);
        }

        @Override
        public void run() {
            Log.d("entry saved", "true");
            Log.d("entries", arcadeEntryList.size() + "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));

        setupTabHost();

        //Getting locations happens ASYNC. Modifty the runnable to change behavior
        NetworkManager.getInstance().getArcadeEntries(arcadeEntriesLoaded);

        //Testing
        ArcadeEntry testEntry = new ArcadeEntry("test2", "home", "45 street st.", 0, 0, 4.3);
        NetworkManager.getInstance().reportArcadeEntry(testEntry, arcadeEntryAdded);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if(extras != null) {
            if(extras.containsKey("fromNotification")) {
                boolean fromNotification = extras.getBoolean("fromNotification");
                if(fromNotification) {
                    TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
                    tabHost.setCurrentTab(1);
                }
            }
        }
    }


    private void setupTabHost() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

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

    public static MainActivity getActivity(){
        return activity;
    }


}
