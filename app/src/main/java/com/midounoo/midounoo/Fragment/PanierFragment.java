package com.midounoo.midounoo.Fragment;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.midounoo.midounoo.Adapters.OrderAdapter;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Db.OrderViewModel;
import com.midounoo.midounoo.Model.AppResponse;
import com.midounoo.midounoo.Model.Notificacion;
import com.midounoo.midounoo.Model.Order;
import com.midounoo.midounoo.Model.Request;
import com.midounoo.midounoo.Model.Sender;
import com.midounoo.midounoo.Model.Token;
import com.midounoo.midounoo.Model.User;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Remote.ApiService;
import com.midounoo.midounoo.Remote.UserLocationChooser;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.midounoo.midounoo.Common.CommonClass.FAILURE_RESULT;
import static com.midounoo.midounoo.Common.CommonClass.SUCCESS_RESULT;
import static com.midounoo.midounoo.Common.CommonClass.coordinateDistanceInKm;
import static com.midounoo.midounoo.Common.CommonClass.numberAddToShop;
import static com.midounoo.midounoo.Common.CommonClass.requestLatitude;
import static com.midounoo.midounoo.Common.CommonClass.requestLongitude;

/**
 * Fragment s'occuppant du panier {@link Fragment}
 *
 * Elle permet de valider so panier et de passer des commandes.
 */
public class PanierFragment extends BottomSheetDialogFragment {

    public PanierFragment() {
        // Required empty public constructor
    }

    private RecyclerView orderShow;
    private DatabaseReference requests, userAdresse, restauId;
    private TextView totaux;
    private List<Order> orderList = new ArrayList<>();
    private TextView viewOrder, adresse;
    //private BottomSheetBehavior bottomSheetBehavior;
    private String errorMessage;
    private OrderViewModel orderViewModel;
    private OrderAdapter adapter;
    private static String username, numero, id;
    private LinearLayout privacy, addr;
    private TextView deliveryPrice;
    private ApiService mService;
    private RadioGroup rg;
    private View view;
    private FirebaseDatabase db;
    private GeoFire geoFire;
    private int compteur = 0; //Ce compteur sert à régler le problème avec le clearcheck du rg
    private double sommeTotal = 0;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_panier, container, false);

        totaux = view.findViewById(R.id.totaux);
        MaterialButton btn = view.findViewById(R.id.orderValidation);
        rg = view.findViewById(R.id.delivery);
        deliveryPrice = view.findViewById(R.id.prix_livraison);
        privacy = view.findViewById(R.id.delivery_privacy);
        addr = view.findViewById(R.id.adresse_layoutt);
        adresse = view.findViewById(R.id.adress_title);

        killNotif(view);

        orderViewModel = ViewModelProviders
                .of(Objects.requireNonNull(getActivity())).get(OrderViewModel.class);

        //Firebase Init
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");
        restauId = db.getReference("Restaurants");

        //Service init
        mService = CommonClass.getFCMService();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Récupération du unique id de l'utilisateur.
        if (user != null){
            username = user.getUid();
            //récupération du champ Users!
            userAdresse = db.getReference("Users").child(user.getUid());

            rg.setOnCheckedChangeListener(
                ((group, checkedId) -> {
                    switch (checkedId){
                        case R.id.localDelivery:
                            if (compteur == 0) {
                                startActivity(new Intent(getActivity(),
                                        UserLocationChooser.class));
                                compteur++;
                            }
                        break;
                        default:
                            break;
                    }
                })
            );

        }

        ValueEventListener userV = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    numero = user.getNumberPhone();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userAdresse.addListenerForSingleValueEvent(userV);


        //Recycler init
        orderShow  = view.findViewById(R.id.order_show);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        orderShow.setLayoutManager(manager);

        viewOrder = view.findViewById(R.id.view_order);
        //RelativeLayout linearLayout = view.findViewById(R.id.bottom_sheets);
        /*
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        viewOrder.setText(R.string.close);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        */
        // Inflate the layout for this fragment

        loadListOrder();
        /*
        viewOrder.setOnClickListener(
            (v -> {
                if (bottomSheetBehavior.getState()
                        != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    viewOrder.setText(R.string.close);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    viewOrder.setText(R.string.details);
                }
            })
        );
        */
        btn.setOnClickListener(
            (v -> {
                if (isFormValid()){
                    if (requestLatitude != null && requestLongitude != null) {
                        Request request = new Request(
                            numero,
                            username,
                            String.valueOf(sommeTotal),
                            orderList,
                            orderList.get(0)
                            .getRestaurantName()
                        );

                        String order_number = String.valueOf(System.currentTimeMillis());

                        requests.child(order_number).setValue(request);
                        DatabaseReference ref = FirebaseDatabase
                                .getInstance().getReference("RequestLocation");
                        GeoFire geoFire = new GeoFire(ref);
                        numberAddToShop = 0;
                        deliveryPrice.setText(null);
                        privacy.setVisibility(View.GONE);
                        geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            new GeoLocation(requestLatitude, requestLongitude),
                            (key, error) -> {
                                if (error != null) {
                                    System.err.println("There was an error saving" +
                                            " the location to GeoFire: " + error);
                                } else {
                                    Toast.makeText(getActivity(),
                                            "Votre localisation a été enregistrée avec" +
                                                    " succès",
                                            Toast.LENGTH_SHORT)
                                            .show();

                                }
                            }
                        );

                        orderViewModel.deleteAll();
                        rg.clearCheck();

                        sendNotificationOrder(order_number);
                    } else {
                        Snackbar.make(view.findViewById(R.id.fragment_panier_layout),
                                "Veuillez vous géolocaliser svp avant de valider la commande",
                                Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(view.findViewById(R.id.fragment_panier_layout),
                            "Veuillez valider votre adresse avant de valider la commande",
                            Snackbar.LENGTH_SHORT).show();
                }

                //Toast.makeText(getActivity(), "Merci pour votre commande", Toast.LENGTH_SHORT).show();
            })
        );
        return view;


    }

    private void killNotif(View view) {
        if (numberAddToShop == 0){
            view.findViewById(R.id.cart_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.bottom_sheets).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.cart_layout).setVisibility(View.GONE);
            view.findViewById(R.id.bottom_sheets).setVisibility(View.VISIBLE);
        }
    }


    //private void getLocation(){}

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Token serverToken = postSnapshot.getValue(Token.class);

                    Notificacion notificacion = new Notificacion("Midounoo",
                            "Vous avez une nouvelle commande "+ order_number);

                    assert serverToken != null;
                    Sender sender = new Sender(serverToken.getToken(), notificacion);

                    mService.sendNotification(sender)
                            .enqueue(new Callback<AppResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<AppResponse> call,
                                                       @NonNull Response<AppResponse> response) {
                                    if (response.code() == 200) {
                                        assert response.body() != null;
                                        if (response.body().success == 1)
                                            Toast.makeText(getActivity(),
                                                    "Merci pour votre commande",
                                                    Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(getActivity(),
                                                    "La Commande n'est pas passée",
                                                    Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<AppResponse> call,
                                                      @NonNull Throwable t) {
                                    Log.w("ERROR", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListOrder() {
        adapter = new OrderAdapter(getActivity());
        /// Get a new or existing ViewModel from the ViewModelProvider.

        // Add an observer on the LiveData returned by getAllOrders.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        orderViewModel.getAllOrders().observe(Objects.requireNonNull(getActivity()),
            (orders -> {
                orderList = orders;
                adapter.setOrderList(orders);
                if (orderList != null) {
                    if (orderList.size() != 0) {
                        restauId.orderByChild("nameRestau").equalTo(orderList.get(0).getRestaurantName())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot post : dataSnapshot.getChildren())
                                        id = post.getKey();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                        );
                    }
                }
                int total = 0;
                //Total price compute
                for (Order order:orders)
                    total += (Integer.parseInt(order.getProductPrice()) -
                            Integer.parseInt(order.getProductDiscount()))
                            *(Integer.parseInt(order.getProductQuantity()));
                Locale locale = new Locale("fr", "TG");
                NumberFormat nbFormat = NumberFormat.getCurrencyInstance(locale);

                sommeTotal = total;
                totaux.setText(nbFormat.format(total));
            })
        );

        DatabaseReference d = db.getReference("Restaurant_location");
        geoFire = new GeoFire(d);


        adapter.notifyDataSetChanged();
        orderShow.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(CommonClass.DELETE)){
            deleteCard(item.getOrder());
            numberAddToShop--;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCard(int position) {
        //Delete order from ROOM db
        orderViewModel.delete(orderList.get(position));
        orderList.remove(position);
        loadListOrder();
        if (rg.getCheckedRadioButtonId() != -1 ){
            rg.clearCheck();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        killNotif(view);
        if (requestLatitude != null && requestLongitude != null && id != null){
            geoFire.getLocation(id, new LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {

                    if (location!=null) {
                        double distance = coordinateDistanceInKm(
                                new LatLng(requestLatitude, requestLongitude),
                                new LatLng(location.latitude, location.longitude)
                        );
                        double prixDeLivraison = (50 * (1+Math.rint(distance/0.71)));
                        deliveryPrice.setText(String.valueOf(prixDeLivraison));
                        sommeTotal = sommeTotal + prixDeLivraison;
                        totaux.setText(String.valueOf(sommeTotal));

                        List<Address> addresses = null;
                        Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            addresses = geo.getFromLocation(
                                    requestLatitude,
                                    requestLongitude,
                                    1);
                            if (addresses.isEmpty()) {
                                adresse.setText(R.string.wait);
                            }
                        }
                        catch (IOException ioException) {
                            // Catch network or other I/O problems.
                            errorMessage = getString(R.string.service_not_available);
                            adresse.setText(errorMessage);
                        } catch (IllegalArgumentException illegalArgumentException) {
                            // Catch invalid latitude or longitude values.
                            errorMessage = getString(R.string.invalid_lat_long_used);
                            adresse.setText(errorMessage);
                        }

                        if (addresses == null || addresses.size()  == 0) {
                            if (errorMessage.isEmpty()) {
                                errorMessage = getString(R.string.no_address_found);
                                adresse.setText(errorMessage);
                            }
                        } else {
                            Address address = addresses.get(0);
                            ArrayList<String> addressFragments = new ArrayList<String>();

                            // Fetch the address lines using getAddressLine,
                            // join them, and send them to the thread.
                            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                addressFragments.add(address.getAddressLine(i));
                            }
                            adresse.setText(TextUtils.join(
                                    Objects.requireNonNull(System.getProperty("line.separator")),
                                            addressFragments));
                        }

                        addr.setVisibility(View.VISIBLE);
                        privacy.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("PROBLEM", "Something going wrong with location");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private boolean isFormValid(){
        boolean temp = true;
        if (rg.getCheckedRadioButtonId() == -1){
            temp = false;
        }
        return temp;
    }

}
