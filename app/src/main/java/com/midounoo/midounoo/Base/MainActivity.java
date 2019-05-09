package com.midounoo.midounoo.Base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.midounoo.midounoo.Fragment.AccountFragment;
import com.midounoo.midounoo.Fragment.PanierFragment;
import com.midounoo.midounoo.Fragment.RestaurantFragment;
import com.midounoo.midounoo.Fragment.SearchFragment;
import com.midounoo.midounoo.Model.Token;
import com.midounoo.midounoo.R;

import java.util.Objects;

import androidx.fragment.app.Fragment;

import static com.midounoo.midounoo.Common.CommonClass.numberAddToShop;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser currentUser;

    private RestaurantFragment restaurantFragment;
    private AccountFragment accountFragment;
    private SearchFragment searchFragment;
    private PanierFragment panierFragment;

    private static final String compte = "Compte";
    private static final String search = "Rechercher";
    private static final String panier = "Panier";
   private static final String local = "Se localiser";
//    private static final String CHANNEL_ID = "MIDOUNOO_CHANNEL";



    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    MaterialButton txt;
    AHBottomNavigation bottomNavigation;
    FrameLayout homeFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        bottomNavigation = findViewById(R.id.bottomNavigation);
        homeFrame = findViewById(R.id.homeFragment);
        txt = findViewById(R.id.localize);


        //Fragment object instantiation
        restaurantFragment = new RestaurantFragment();
        accountFragment = new AccountFragment();
        searchFragment = new SearchFragment();
        panierFragment = new PanierFragment();

        currentUser = mAuth.getCurrentUser();
        sendToBegin();


        txt.setOnClickListener(
            (v ->
                getLocation()
            )
        );

        setFragment(restaurantFragment);

        if (getIntent().hasExtra("OPEN_PANIER")) {
            setFragment(panierFragment);
        }

        //Bottom navigation view management
        AHBottomNavigationItem item1, item2, item3, item4;
        item1 = new AHBottomNavigationItem(R.string.restau,
                R.drawable.ic_restaurant_menu_black_24dp,
                R.color.colorPrimary); //restaurant
        item2 = new AHBottomNavigationItem(R.string.Rechercher,
                R.drawable.ic_search_black_24dp,
                R.color.colorPrimary); //recherche
        item3 = new AHBottomNavigationItem(R.string.panier,
                R.drawable.ic_shopping_basket_black_24dp,
                R.color.colorPrimary); //panier
        item4 = new AHBottomNavigationItem(R.string.compte,
                R.drawable.ic_perm_identity_black_24dp,
                R.color.colorPrimary); //compte

        //Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        // Use colored navigation with circle reveal effect
        //bottomNavigation.setColored(true);

        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

        // Set background color
        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorTask));

        // Change colors
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.timestamp));
        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorPrimary));

        //Manage titles
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);

        bottomNavigation.setOnTabSelectedListener(
                (position, wasSelected) -> {
                    switch (position){
                        case 0:
                            setFragment(restaurantFragment);
                            txt.setText(local);
                            txt.setClickable(true);
                            return true;
                        case 1:
                            setFragment(searchFragment);
                            txt.setText(search);
                            txt.setClickable(false);
                            return true;
                        case 2:
                            setFragment(panierFragment);
                            txt.setText(panier);
                            txt.setClickable(false);
                            return true;
                        case 3:
                            setFragment(accountFragment);
                            txt.setText(compte);
                            txt.setClickable(false);
                            return true;
                        default:
                            return false;
                    }
                }
        );

        setNotification();
    }

    private void sendRegistrationToServer(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        Token t = new Token(token, false);
        tokens.child(currentUser.getUid())
                .setValue(t);
    }

    /**
     *
     * @param fragment fragment via lequel on réalise les commit
     */
    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction =  getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homeFragment, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Dans la logique du cycle de vie d'une activité, on doit appeler
     * la méthode sendRegistrationToServer ici pour être sûr qu'on peut
     * récupérer le token de l'utilisateur
     */
    @Override
    protected void onStart() {
        super.onStart();

        sendToBegin();
        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(
                (task -> {
                    if (!task.isSuccessful()){
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }
                    if (currentUser != null)
                        sendRegistrationToServer(Objects
                                .requireNonNull(task.getResult()).getToken());
            })
        );

        setNotification();
    }

    /**
     * Cette fonction vérifie si l'utilisateur s'est authentifié
     * Il est renvoyé sur la page d'authentification sinon
     */
    public void sendToBegin(){
        if (currentUser == null){
            startActivity(new Intent(MainActivity.this, BeginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //GoogleApiAvailability g = GoogleApiAvailability.getInstance();
        setNotification();
    }

    private void setNotification(){
        if (numberAddToShop != 0){
            AHNotification notification = new AHNotification.Builder()
                    .setText(String.valueOf(numberAddToShop))
                    .setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                            R.color.notificationColor))
                    .setTextColor(ContextCompat.getColor(MainActivity.this,
                            R.color.colorTask))
                    .build();
            bottomNavigation.setNotification(notification, 2);
        }
    }

    /*
    * Find other way to deal with this. Erase before pushing to production
    private int getTheCount(){
        AtomicInteger numberOfMenu = new AtomicInteger();
        OrderViewModel model = ViewModelProviders.of(MainActivity.this)
                .get(OrderViewModel.class);

        model.getAllOrders().observe(MainActivity.this,
                (orders -> numberOfMenu.set(orders.size())));

        return numberOfMenu.get();
    }
*/
    /**
     * Return or set the user location
     */
    private void getLocation() {
        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        FusedLocationProviderClient mFusedLocationClient;
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(MainActivity.this, "Veuillez activer " +
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
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient = LocationServices.
                    getFusedLocationProviderClient(MainActivity.this);
            mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this,
                    (location -> {
                        if (location != null){
                            DatabaseReference ref = FirebaseDatabase
                                    .getInstance().getReference("UsersLocation");
                            GeoFire geoFire = new GeoFire(ref);
                            geoFire.setLocation(currentUser.getUid(),
                                new GeoLocation(location.getLatitude(),
                                        location.getLongitude()),
                                (key, error) -> {
                                    if (error != null) {
                                        System.err.println("There was an error saving"+
                                                " the location to GeoFire: " + error);
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "Votre localisation a été enregistrée avec" +
                                                        "succès",
                                                Toast.LENGTH_SHORT)
                                                .show();

                                    }
                                });
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Problème with location", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                )
            );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //moveTaskToBack(true);
    }
}
