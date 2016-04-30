package cs403x.crowdcade;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joshua on 4/25/2016.
 */
public class ArcadeEntry {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String LOCATION_NAME = "locationName";
    private static final String ADDRESS = "address";
    private static final String LOCATION_LON = "locationLon";
    private static final String LOCATION_LAT = "locationLat";
    private static final String CONDITION = "condition";
    private static final String VISITS = "visits";
    private static final String PHOTO = "photo";

    long id = -1;
    String name;
    String locationName;
    String address;
    Bitmap photo = null;

    double locationLon;
    double locationLat;
    double condition;

    int visits;

    public static List<ArcadeEntry> fromJSONArray(String s){
        List<ArcadeEntry> entries = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(s);
            for(int i = 0; i < array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                ArcadeEntry entry = new ArcadeEntry(obj);
                entries.add(entry);
            }
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
        return entries;
    }

    public ArcadeEntry(JSONObject obj){
        try {
            this.id = obj.getInt(ID);
            this.name = obj.getString(NAME);
            this.locationName = obj.getString(LOCATION_NAME);
            this.address = obj.getString(ADDRESS);
            this.locationLon = obj.getDouble(LOCATION_LON);
            this.locationLat = obj.getDouble(LOCATION_LAT);
            this.condition = obj.getDouble(CONDITION);
            this.visits = obj.getInt(VISITS);
            if(obj.has(PHOTO)){
                this.photo = getBitmapFromString(obj.getString(PHOTO));
            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
    }

    public ArcadeEntry(String name, String locationName, String address, double locationLat, double locationLon, double condition){
        this.name = name;
        this.locationName = locationName;
        this.address = address;
        this.locationLon = locationLon;
        this.locationLat = locationLat;
        this.condition = condition;
    }

    public ArcadeEntry(String name, String locationName, String address, double locationLat, double locationLon, double condition, Bitmap photo) {
        this.name = name;
        this.locationName = locationName;
        this.address = address;
        this.locationLon = locationLon;
        this.locationLat = locationLat;
        this.condition = condition;
        this.photo = photo;
    }

    public JSONObject getJSON(){
        JSONObject obj = new JSONObject();
        try {
            obj.put(ID, id);
            obj.put(NAME, name);
            obj.put(LOCATION_NAME, locationName);
            obj.put(ADDRESS, address);
            obj.put(LOCATION_LON, locationLon);
            obj.put(LOCATION_LAT, locationLat);
            obj.put(CONDITION, condition);
            obj.put(VISITS, visits);
            if(photo != null){
                obj.put(PHOTO, getStringFromBitmap(photo));
            }
            else{
                obj.put(PHOTO, "");
            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

        return obj;
    }



    private String getStringFromBitmap(Bitmap bitmapPicture) {
 /*
 * This functions converts Bitmap picture to a string which can be
 * JSONified.
 * */
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        if(stringPicture.isEmpty()){
            return null;
        }
/*
* This Function converts the String back to Bitmap
* */
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }




    @Override
    public String toString() {
        return getJSON().toString();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(double locationLon) {
        this.locationLon = locationLon;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getCondition() {
        return condition;
    }

    public void setCondition(double rating) {
        this.condition = rating;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public Bitmap getPhoto() { return photo; }

    public void setPhoto(Bitmap photo) { this.photo = photo; }



}
