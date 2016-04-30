package cs403x.crowdcade;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import cs403x.crowdcade.MainActivity;
import cs403x.crowdcade.NetworkManager;

/**
 * Created by Joshua on 4/30/2016.
 */
public class MapListener implements OnMapReadyCallback {

    private MainActivity mainActivity;
    public GoogleMap map;
    public Marker addressMarker = null;
    public boolean isClickable;

    public MapListener(MainActivity mainActivity, boolean isClickable){
        this.mainActivity = mainActivity;
        this.isClickable = isClickable;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mMapReport = googleMap;
        map = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMapFind.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMapFind.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //mMapFind.moveCamera(CameraUpdateFactory.zoomTo(10.0f));

        centerMapOnMyLocation(12.5f);

        //centerMapOnMyLocation(mMapReport, 13.5f);

        //Getting locations happens ASYNC. Modify the runnable to change behavior
        NetworkManager.getInstance().getArcadeEntries(mainActivity.arcadeEntriesLoaded);

        if (isClickable){
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {
                    if (addressMarker == null){
                        addressMarker = map.addMarker(new MarkerOptions().position(latLng));
                    }
                    else{
                        addressMarker.setPosition(latLng);
                    }
                }

            });
        }
    }

    private void centerMapOnMyLocation(float zoomLevel) {
        try {

            LocationManager locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(zoomLevel)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        }
        catch (SecurityException ex){
            ex.printStackTrace();
        }
    }

    public LatLng getSelectedLocation(){
        if (addressMarker != null) {
            return addressMarker.getPosition();
        }
        return null;
    }
}
