package com.example.project_final;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;
    // these "request codes" are used to identify sub-activities that return results
    private static final int REQUEST_CODE_DETAILS_ACTIVITY = 1234;
    private GoogleMap mMap;
    String Country;
    String Countrycode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // ASKING USER FOR LOCATION PERMISSION
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_DETAILS_ACTIVITY);

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                recreate();
                return;
            }
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //BOTTOM NAVIGATION CLICK
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        if(Countrycode!= null) {
                            Intent intent = new Intent(MapsActivity.this, ListActivity.class);
                            intent.putExtra("countryCode", Countrycode);
                            intent.putExtra("country", Country);
                            startActivityForResult(intent, REQUEST_CODE_DETAILS_ACTIVITY);
                        }else{
                            Toast.makeText(getApplicationContext(),"A country must be selected",Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                return true;
            }
        });
        //GETTING LAST KNOWN LOCATION
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object


                            mMap.clear();

                            new geocode().execute("https://maps.googleapis.com/maps/api/geocode/json?latlng="+ location.getLatitude() +","+ location.getLongitude() +"&key=APIKEY");

                            LatLng lastloc = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(lastloc).title("Location Selected"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastloc));

                        }
                    }
                });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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


        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap = googleMap;
        // MAP CLICK RUN GEOCODE TO GET COUNTRY
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();

                new geocode().execute("https://maps.googleapis.com/maps/api/geocode/json?latlng="+ latLng.latitude +","+ latLng.longitude +"&key=AIzaSyAkCIDRpYnJ9ecHjnO8xcfOMh6KxqwIb1M");

                LatLng lastloc = new LatLng(latLng.latitude, latLng.longitude);
                mMap.addMarker(new MarkerOptions().position(lastloc).title("Location Selected"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastloc));
            }
        });
    }

    // GET COUNTRY CODE
    public class geocode extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            HttpURLConnection connection;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }
                connection.disconnect();

                return new JSONObject(responseStrBuilder.toString());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            JSONObject obj = result;
            try {

                JSONArray response = obj.getJSONArray("results");
                JSONObject c = response.getJSONObject(1);
                JSONArray address = c.getJSONArray("address_components");
                for(int i=0; i<address.length(); i++) {
                    JSONObject countr = address.getJSONObject(i);
                    JSONArray type = countr.getJSONArray("types");
                        for(int j=0; j<type.length(); j++) {
                            if(type.getString(j).equals("country")){
                                Country = countr.getString("long_name");
                                Countrycode = countr.getString("short_name");

                                TextView textView = (TextView)  findViewById(R.id.country);
                                textView.setText(Country + " , " + Countrycode);
                            }
                        }
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
