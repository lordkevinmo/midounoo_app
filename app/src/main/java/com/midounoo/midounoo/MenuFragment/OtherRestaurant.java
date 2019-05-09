package com.midounoo.midounoo.MenuFragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.midounoo.midounoo.Adapters.OtherViewHolder;
import com.midounoo.midounoo.Base.ListFoodRest;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.Favorites;
import com.midounoo.midounoo.Model.Restaurants;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Utility.ItemClickSupport;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.midounoo.midounoo.Common.CommonClass.usersLatitude;
import static com.midounoo.midounoo.Common.CommonClass.usersLongitude;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherRestaurant extends Fragment {

    private DatabaseReference restaurant;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView otherMenu;
    private FirebaseUser user;
    private FirebaseDatabase db;

    public OtherRestaurant() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseAuth auth;

        db = FirebaseDatabase.getInstance();
        restaurant = db.getReference("Restaurants");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_other_restaurant, container, false);
        otherMenu = view.findViewById(R.id.other_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        otherMenu.setLayoutManager(layoutManager);
        otherMenu.setItemAnimator(new DefaultItemAnimator());

        //loading(view);
        return view;
    }

    /**
     *
     * Vérifie si la connexion internet bosse avant de retourner les menus
     *
     * @param view Instance de la vue charger via l'activité mère
     */
    private void loading(View view){
        if (CommonClass.isConnectedToInternet(Objects.requireNonNull(getActivity())))
            prepareMenu();
        else
            Snackbar.make(view.findViewById(R.id.menuLayout),
                    R.string.connection, Snackbar.LENGTH_INDEFINITE).show();
    }

    /**
     *Afficher les menus à l'accueil
     *
     * les variables options contiennent les requêtes pour filtrer les résultats
     *
     */

    private void prepareMenu() {

        FirebaseRecyclerOptions<Restaurants> options1 =
                new FirebaseRecyclerOptions.Builder<Restaurants>()
                        .setQuery(restaurant.orderByKey(), Restaurants.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Restaurants, OtherViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final OtherViewHolder otherViewHolder,
                                            final int i,
                                            @NonNull final Restaurants restaurants) {


                Glide.with(Objects.requireNonNull(getActivity())).
                        load(restaurants.getImage()).into(otherViewHolder.otherFoodImage);
                otherViewHolder.otherCity.setText(restaurants.getAdresse());
                otherViewHolder.restaurant.setText(restaurants.getNameRestau());
                otherViewHolder.delay.setText(getString(R.string.delay_w));
                otherViewHolder.otherNberFavorite.setText(String.valueOf(CommonClass.rating));
                if (restaurants.isOpen()){
                    otherViewHolder.status.setTextColor(Color.GREEN);
                    otherViewHolder.status.setText(getString(R.string.open));
                } else {
                    otherViewHolder.status.setTextColor(Color.RED);
                    otherViewHolder.status.setText(getString(R.string.closed));
                }

                ItemClickSupport.addTo(otherMenu).setOnItemClickListener(
                    ((recyclerView, position, v) -> {

                        Intent foodDetail = new Intent(getActivity(), ListFoodRest.class);
                        foodDetail.putExtra("key",
                                adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    })
                );

                GeoFire geoFire = new GeoFire(db.getReference("Restaurant_location"));
                final GeoQuery query = geoFire.queryAtLocation(
                        new GeoLocation(usersLatitude, usersLongitude), 10);
                query.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        //if (query != null){

                       // } else {
                         //   otherMenu.setVisibility(View.GONE);

                        //}
                    }

                    @Override
                    public void onKeyExited(String key) {

                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public OtherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getActivity())
                        .inflate(R.layout.other_content, parent, false );

                return new OtherViewHolder(v);
            }

        };
        adapter.startListening();
        otherMenu.setAdapter(adapter);
    }

    private void loadingRestaurants(String key){

    }


    /*

    private void checkFavorite(final int i, final ImageView v){
        favorite.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()){
                    if (post.getValue(Favorites.class) != null){
                        if (!Objects.requireNonNull(post.getValue(Favorites.class)).getUserId().equals(user.getUid()) &&
                                !Objects.requireNonNull(post.getValue(Favorites.class)).getRestaurantId().equals(adapter.getRef(i).getKey())){
                            Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.ic_favorite_white_24dp)
                                    .into(v);
                            fav = new Favorites(adapter.getRef(i).getKey(),user.getUid());
                            favorite.push().setValue(fav);
                            break;
                        } else if (Objects.requireNonNull(post.getValue(Favorites.class)).getUserId().equals(user.getUid()) &&
                                Objects.requireNonNull(post.getValue(Favorites.class)).getRestaurantId().equals(adapter.getRef(i).getKey())){
                            Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.ic_favorite_border_black_24dp)
                                    .into(v);
                            favorite.child(Objects.requireNonNull(post.getKey())).removeValue();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
*/
    @Override
    public void onStart() {
        super.onStart();
        prepareMenu();
        //adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}