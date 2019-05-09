package com.midounoo.midounoo.MenuFragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.midounoo.midounoo.Adapters.NearViewHolder;
import com.midounoo.midounoo.Adapters.PopularRecyclerAdapter;
import com.midounoo.midounoo.Adapters.PopularViewHolder;
import com.midounoo.midounoo.Base.FoodDetailsActivity;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.Favorites;
import com.midounoo.midounoo.Model.Food;
import com.midounoo.midounoo.Model.Rating;
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
public class PopularRestaurants extends Fragment {

    private DatabaseReference food, category, restaurant, rating;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView popularMenu;
    private String categoryName, restaurantName;
    private FirebaseUser user;
    private Favorites fav;
    private FirebaseDatabase db;


    public PopularRestaurants() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseAuth auth;
        db = FirebaseDatabase.getInstance();
        food = db.getReference("Food");
        category = db.getReference().child("Category");
        restaurant = db.getReference("Restaurants");
        rating = db.getReference("Rating");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        View v =  inflater.inflate(R.layout.fragment_popular_restaurants,
                container, false);

        popularMenu = v.findViewById(R.id.popular_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        popularMenu.setLayoutManager(layoutManager);
        popularMenu.setItemAnimator(new DefaultItemAnimator());

        //loading(v);

        return v;
    }

    /**
     *
     * Vérifie si la connexion internet bosse avant de retourner les menus
     *
     *
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

        FirebaseRecyclerOptions<Food> options1 =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(food.orderByChild("restaurantId"), Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, PopularViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final PopularViewHolder foodViewHolder,
                                            final int i, @NonNull Food food) {

                foodViewHolder.position = i;
                foodViewHolder.mAdapter = adapter;

                String k = adapter.getRef(i).getKey();

                Query query = rating.orderByChild("foodId").equalTo(k);

                query.addValueEventListener(new ValueEventListener() {
                    int count =0, sum = 0;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot nextData : dataSnapshot.getChildren()){
                            Rating item = nextData.getValue(Rating.class);
                            assert item != null;
                            sum+=Integer.parseInt(item.getRateValue());
                            count++;
                        }
                        if (count!=0) {
                            float average = (float) sum / count;
                            foodViewHolder.popularNberFavorite.setText(String.valueOf(average));
                        } else {
                            foodViewHolder.popularNberFavorite.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                category.child(food.getMenuId()).child("name")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                categoryName = dataSnapshot.getValue(String.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                restaurant.child(food.getRestaurantId()).child("nameRestau")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                restaurantName = dataSnapshot.getValue(String.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                Glide.with(Objects.requireNonNull(getActivity())).load(food.getImage()).into(foodViewHolder.popularFoodImage);
                foodViewHolder.popularFoodName.setText(food.getName());
                foodViewHolder.restaurant.setText(restaurantName);
                foodViewHolder.popularFoodCategory.setText(categoryName);
                ItemClickSupport.addTo(popularMenu).setOnItemClickListener(
                        ((recyclerView, position, v) -> {
                            Intent foodDetail = new Intent(getActivity(), FoodDetailsActivity.class);
                            foodDetail.putExtra("FoodId",
                                    adapter.getRef(position).getKey());
                            foodDetail.putExtra("restau", restaurantName);
                            startActivity(foodDetail);
                        })
                );
            }

            @NonNull
            @Override
            public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getActivity())
                        .inflate(R.layout.popular_content, parent, false );

                return new PopularViewHolder(v);
            }

        };
        adapter.startListening();
        popularMenu.setAdapter(adapter);

        GeoFire geoFire = new GeoFire(db.getReference("Restaurant_location"));
        GeoQuery query = geoFire.queryAtLocation(
                new GeoLocation(usersLatitude, usersLongitude), 10);
        query.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                loadingMenu(key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                loadingMenu(key);
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void loadingMenu(String key){
    }

    @Override
    public void onStart() {
        super.onStart();
        prepareMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }
}
