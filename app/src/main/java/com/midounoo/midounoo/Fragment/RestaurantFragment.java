package com.midounoo.midounoo.Fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.MenuFragment.OtherRestaurant;
import com.midounoo.midounoo.MenuFragment.PopularRestaurants;
import com.midounoo.midounoo.MenuFragment.ProximityRestaurant;
import com.midounoo.midounoo.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static com.midounoo.midounoo.Common.CommonClass.ERROR_DIALOG_REQUEST;
import static com.midounoo.midounoo.Common.CommonClass.PERMISSIONS_REQUEST_ENABLE_GPS;
import static com.midounoo.midounoo.Common.CommonClass.usersLatitude;
import static com.midounoo.midounoo.Common.CommonClass.usersLongitude;


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends Fragment {


    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private GeoFire geoFire;
    private int resolveur = 0;
    private boolean mLocationPermissionGranted;
    FirebaseUser user;


    public RestaurantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        PopularRestaurants popular = new PopularRestaurants();
        ProximityRestaurant near = new ProximityRestaurant();
        OtherRestaurant other = new OtherRestaurant();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        if (CommonClass.isConnectedToInternet(Objects.requireNonNull(getActivity()))) {
            FragmentManager manager = getChildFragmentManager();
            manager.beginTransaction()
                    .add(R.id.popular_foods, popular)
                    .commit();

            manager.beginTransaction()
                    .add(R.id.near_foods, near)
                    .commit();

            manager.beginTransaction()
                    .add(R.id.others_foods, other)
                    .commit();
        } else {
            view.findViewById(R.id.no_connexion).setVisibility(View.VISIBLE);
            view.findViewById(R.id.connexion).setVisibility(View.GONE);
        }

        view.findViewById(R.id.refresh).setOnClickListener(
            (v -> {
                if (CommonClass.isConnectedToInternet(Objects.requireNonNull(getActivity()))) {
                    view.findViewById(R.id.no_connexion).setVisibility(View.GONE);
                    view.findViewById(R.id.connexion).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.no_connexion).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.connexion).setVisibility(View.GONE);
                }
            })
        );

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void getLocation() {
        FusedLocationProviderClient mFusedLocationClient;
        mFusedLocationClient = LocationServices.
                getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation()
            .addOnSuccessListener(getActivity(),
                (location -> {
                    if (location != null) {
                        usersLatitude = location.getLatitude();
                        usersLongitude = location.getLongitude();
                        DatabaseReference ref = FirebaseDatabase
                                .getInstance().getReference("UsersLocation");
                        geoFire = new GeoFire(ref);
                        geoFire.setLocation(user.getUid(),
                                new GeoLocation(location.getLatitude(),
                                        location.getLongitude()),
                                (key, error) -> {
                                    if (error != null) {
                                        System.err.println("There was an error saving" +
                                                " the location to GeoFire: " + error);
                                    } else {
                                        System.out.println("Location saved on" +
                                                " server successfully!");
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(),
                                "Problème with location", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            )
        );
        resolveur++;
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage("Cette application nécessite que le GPS" +
                " fonctionne correctement, voulez-vous l'activer?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new
                                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        Button bt1 = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        //bt1.setBackgroundColor(getResources().getColor(R.color.colorTask));
        alert.show();
    }

    private boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) Objects.requireNonNull(getActivity())
                .getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission1 = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permission2 = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission1 != PackageManager.PERMISSION_GRANTED &&
                permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mLocationPermissionGranted = true;
        }
    }

    private boolean isServicesOK() {

        int available = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),
                    available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(),
                    "Vous ne pouvez pas utiliser carte", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Callback that is invoked when the user responds to the permissions
     * dialog.
     *
     * @param requestCode  Request code representing the permission request
     *                     issued by the app.
     * @param permissions  An array that contains the permissions that were
     *                     requested.
     * @param grantResults An array with the results of the request for each
     *                     permission requested.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getLocation();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getLocation();
                } else {
                    getLocationPermission();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resolveur < 1) {
            if (checkMapServices()) {
                if (mLocationPermissionGranted) {
                    getLocation();
                } else {
                    getLocationPermission();
                }
            }
        }
    }



}
