package cs403x.crowdcade;

import android.app.Activity;

/**
 * Created by Joshua on 12/8/2015.
 */
public abstract class ResponseRunnable implements Runnable {
    String data;
    Activity activity;

    public ResponseRunnable(Activity activity){
        this.activity = activity;
    }

    void setResponseData(String data){
        this.data = data;
    }

    @Override
    public void run() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                runOnMainThread();
            }
        });
    }

    public abstract void runOnMainThread();
}
