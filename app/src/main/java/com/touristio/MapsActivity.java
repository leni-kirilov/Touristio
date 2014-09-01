package com.touristio;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        new SearchCoordinatesTask(this).execute("London", "Berlin", "Blagoevgrad");
    }

    private void setMarker(String cityName, LatLng cityCoordinates) {
        MarkerOptions marker = new MarkerOptions()
                .position(cityCoordinates)
                .title(cityName);
        mMap.addMarker(marker);
    }

    private String getMapsApiKey() {
        return getResources().getString(R.string.google_maps_key);
    }

    /**
     * AsyncTask for searching for coordinates of cities,places etc
     * <p/>
     * Created following this example: http://developer.android.com/training/basics/network-ops/connecting.html
     * and this example: http://developer.android.com/training/location/display-address.html
     */
    private class SearchCoordinatesTask extends AsyncTask<String, Void, Map<String, LatLng>> {

        private Context mContext;
        private Geocoder geocoder;

        public SearchCoordinatesTask(Context context) {
            mContext = context;
            geocoder = new Geocoder(mContext, Locale.getDefault());
        }

        @Override
        protected Map<String, LatLng> doInBackground(String... cityNames) {

            Map<String, LatLng> coordinates = new HashMap<String, LatLng>();

            for (String city : cityNames) {
                coordinates.put(city, getCoordinates(city));
            }

            return coordinates;
        }

        private LatLng getCoordinates(String cityName) {
            LatLng coordinates = null;
            try {
                List<Address> fromLocationName = geocoder.getFromLocationName(cityName, 1);
                coordinates = new LatLng(fromLocationName.get(0).getLatitude(), fromLocationName.get(0).getLongitude());

            } catch (IOException e) {
                Log.e("ERROR", e.getMessage(), e);
                coordinates = new LatLng(0, 0);
            }

            return coordinates;
        }

        @Override
        protected void onPostExecute(Map<String, LatLng> cityCoordinates) {
            for (String city : cityCoordinates.keySet()) {
                setMarker(city, cityCoordinates.get(city));
            }
        }
    }
}
