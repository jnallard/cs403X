package cs403x.crowdcade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity  {


    /**
     * Used to store the last screen title. For use in .
     */
    private CharSequence mTitle;

    private static MainActivity activity;



    private static final int DEFAULT_DISPLAY_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));

        setupTabHost();
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
