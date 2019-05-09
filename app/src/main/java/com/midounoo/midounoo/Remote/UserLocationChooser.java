package com.midounoo.midounoo.Remote;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.midounoo.midounoo.R;

import static com.midounoo.midounoo.Common.CommonClass.requestLatitude;
import static com.midounoo.midounoo.Common.CommonClass.requestLongitude;


public class UserLocationChooser extends FragmentActivity
        implements LocationListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1002;
    GoogleMap mGoogleMap;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    Double userLatitude, userLongitude;
    private ProgressDialog myProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_user_location_chooser);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;

        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Map Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        // Display Progress Bar.
        myProgress.show();

        mapFragment.getMapAsync((this::onMyMapReady));
        getLocation();

        findViewById(R.id.setup_location).setOnClickListener(
            (v -> {
                if (userLongitude != null && userLatitude != null) {
                    requestLatitude = userLatitude;
                    requestLongitude = userLongitude;
                    finish();
                } else
                    Toast.makeText(UserLocationChooser.this,
                            "Veuillez cliquer sur votre position",
                            Toast.LENGTH_SHORT).show();
            })
        );
    }


    private void onMyMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mGoogleMap.setOnMapLoadedCallback(
            () -> {
                // Map loaded. Dismiss this dialog, removing it from the screen.
                myProgress.dismiss();

                askPermissionsAndShowMyLocation();
            }
        );
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        mGoogleMap.setMyLocationEnabled(true);

    }

    private void askPermissionsAndShowMyLocation() {

        int accessCoarsePermission
                = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFinePermission
                = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
            // The Permissions to ask user.
            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};
            // Show a dialog asking the user to allow the above permissions.
            ActivityCompat.requestPermissions(UserLocationChooser.this
                    , permissions,
                    REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

            return;
        }


        // Show current location on Map.
        this.showMyLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (read/write).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Show current location on Map.
                    this.showMyLocation();
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Criteria to find location provider.
        Criteria criteria = new Criteria();

        // Returns the name of the provider that best meets the given criteria.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this,
                    "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(UserLocationChooser.class.getSimpleName()
                    , "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }


    private void showMyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = this.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation;
        try {
            // This code need permissions (Asked above ***)
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
            // Getting Location.
            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);
        }
        // With Android API >= 23, need to catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(this,
                    "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
            Log.e(UserLocationChooser.class.getSimpleName(),
                    "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (myLocation != null) {

            userLatitude = myLocation.getLatitude();
            userLongitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            // Add Marker to Map
            MarkerOptions option = new MarkerOptions();
            option.title("Votre Position");
            option.snippet("....");
            option.position(latLng);
            Marker currentMarker = mGoogleMap.addMarker(option);
            currentMarker.showInfoWindow();
        } else {
            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
            Log.i(UserLocationChooser.class.getSimpleName(), "Location not found");
        }


    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Return or set the user location
     */
    private void getLocation() {
        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        FusedLocationProviderClient mFusedLocationClient;
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(UserLocationChooser.this, "Veuillez activer " +
                    "votre service de localisation", Toast.LENGTH_SHORT).show();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission1 != PackageManager.PERMISSION_GRANTED &&
                permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserLocationChooser.this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(UserLocationChooser.this);
            mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(UserLocationChooser.this,
                    (location -> {
                        if (location != null){
                            userLatitude = location.getLatitude();
                            userLongitude = location.getLongitude();
                        } else {
                            Toast.makeText(UserLocationChooser.this,
                                    "Problème with location", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                )
            );
        }
    }
}
