package com.midounoo.midounoo.Common;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.midounoo.midounoo.Base.FoodDetailsActivity;
import com.midounoo.midounoo.Db.OrderViewModel;
import com.midounoo.midounoo.Model.User;
import com.midounoo.midounoo.Remote.ApiService;
import com.midounoo.midounoo.Remote.RetrofitClient;

import java.util.concurrent.atomic.AtomicInteger;

import androidx.lifecycle.ViewModelProviders;

public final class CommonClass {

    public static User cUser;

    static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static FirebaseUser user = auth.getCurrentUser();
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9001;
    public static final int PERMISSIONS_LOCATION_REQUEST = 9002;
    public static final int ERROR_DIALOG_REQUEST = 9003;
    public static double usersLatitude;
    public static double usersLongitude;
    public static Double requestLatitude;
    public static Double requestLongitude;
    public static int numberAddToShop = 0;
    private static final String FINAL_URL = "https://fcm.googleapis.com/";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";


    public static ApiService getFCMService(){
        return RetrofitClient.getClient(FINAL_URL).create(ApiService.class);
    }


    public static int rating;
    public static final String DELETE = "Supprimer";

    public static String setStatuts(String status) {

        if (status.equals("0")){
            return "Passée";
        } else if (status.equals("1")){
            return "Prêt à être livrer";
        } else if (status.equals("2")){
            return "En cours de livraison";
        } else {
            return "Livrée";
        }

    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager manager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    private static double degreesToRadian(double degree){
        return degree * Math.PI / 180;
    }

    public static double coordinateDistanceInKm(LatLng l1, LatLng l2){
        double earthRadiusKm = 6371;
        double dLat, dLong;

        dLat = l2.latitude - l1.latitude;
        dLong = l2.longitude - l1.longitude;

        double a = Math.pow(Math.sin(dLat/2), 2) + Math.pow(Math.sin(dLong/2), 2)
                * Math.cos(degreesToRadian(l1.latitude)) * Math.cos(degreesToRadian(l2.latitude));
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadiusKm * c;
    }


}
