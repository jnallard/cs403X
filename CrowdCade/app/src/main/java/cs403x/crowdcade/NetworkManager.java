package cs403x.crowdcade;

import android.graphics.Bitmap;
import android.text.Html;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Joshua on 4/25/2016.
 */
public class NetworkManager {

    private static NetworkManager instance = new NetworkManager();

    private NetworkManager(){

    }

    public static NetworkManager getInstance(){
        return instance;
    }

    public void getArcadeEntries(ResponseRunnable toRunAfterSend){
        ConnectionThread connectionThread = new ConnectionThread("http://jnallard.com/crowdcade/", "", toRunAfterSend, toRunAfterSend);
        connectionThread.start();
    }

    public void searchArcadeEntries(ResponseRunnable toRunAfterSend, String searchTerm){
        ConnectionThread connectionThread = new ConnectionThread("http://jnallard.com/crowdcade/?search="+searchTerm, "", toRunAfterSend, toRunAfterSend);
        connectionThread.start();
    }

    public void reportArcadeEntry(ArcadeEntry entry, ResponseRunnable toRunAfterSend){
        ConnectionThread connectionThread = new ConnectionThread("http://jnallard.com/crowdcade/?new=true", entry.toString(), toRunAfterSend, toRunAfterSend);
        connectionThread.start();
    }

    public void reportArcadeEntryVisited(ArcadeEntry entry, ResponseRunnable toRunAfterSend){
        ConnectionThread connectionThread = new ConnectionThread("http://jnallard.com/crowdcade/?visit=true", entry.toString(), toRunAfterSend, toRunAfterSend);
        connectionThread.start();
    }

    private class ConnectionThread extends Thread {
        String location;
        String data;
        ResponseRunnable toRunAfterSuccessfulSend;
        ResponseRunnable toRunAfterFailureSend;

        public ConnectionThread(String location, String data, ResponseRunnable toRunAfterSuccess, ResponseRunnable toRunAfterFail) {
            this.location = location;
            this.data = data;
            this.toRunAfterSuccessfulSend = toRunAfterSuccess;
            this.toRunAfterFailureSend = toRunAfterFail;
        }

        @Override
        public void run() {
            super.run();

            try {
                URL url = new URL(location);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "UTF-8");
                conn.setFixedLengthStreamingMode(data.length());

                conn.setUseCaches(false);
                PrintStream wr = new PrintStream(conn.getOutputStream());
                Log.d("print", data);
                wr.print(data);
                //wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String response = "";
                while ((line = rd.readLine()) != null) {
                    System.out.println(line);
                    response += line + "\n";
                }
                rd.close();
                wr.close();
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 299){
                    if(toRunAfterSuccessfulSend != null) {
                        toRunAfterSuccessfulSend.setResponseData(response);
                        toRunAfterSuccessfulSend.run();
                    }
                } else {
                    if(toRunAfterFailureSend != null) {
                        toRunAfterFailureSend.run();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                if(toRunAfterFailureSend != null) {
                    toRunAfterFailureSend.run();
                }
            }
        }
    }
}
