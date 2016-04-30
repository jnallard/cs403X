package cs403x.crowdcade;

import android.graphics.Bitmap;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    long id = -1;
    String name;
    String locationName;
    String address;
    Bitmap photo;

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
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
    }

    public ArcadeEntry(String name, String locationName, String address, double locationLon, double locationLat, double condition){
        this.name = name;
        this.locationName = locationName;
        this.address = address;
        this.locationLon = locationLon;
        this.locationLat = locationLat;
        this.condition = condition;
    }

    public ArcadeEntry(String name, String locationName, String address, double locationLon, double locationLat, double condition, Bitmap photo) {
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
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

        return obj;
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
