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
public class ProximityRestaurant extends Fragment {

    private DatabaseReference food, category, restaurant, favorite, rating;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView nearMenu;
    private String categoryName, restaurantName;
    private FirebaseUser user;
    private Favorites fav;
    private FirebaseDatabase db;

    public ProximityRestaurant() {
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
        favorite = db.getReference("Favorite");
        rating = db.getReference("Rating");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_proximity_restaurant, container, false);
        nearMenu = view.findViewById(R.id.near_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        nearMenu.setLayoutManager(layoutManager);
        nearMenu.setItemAnimator(new DefaultItemAnimator());

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

        FirebaseRecyclerOptions<Food> options1 =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(food.orderByChild("restaurantId"), Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, NearViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final NearViewHolder foodViewHolder,
                                            final int i, @NonNull Food food) {

                foodViewHolder.mAdapter = adapter;
                foodViewHolder.position = i;

                String k = adapter.getRef(i).getKey();

                Query query = rating.orderByChild("foodId").equalTo(k);
                Query q = favorite.orderByChild("foodId").equalTo(k);

                q.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postData : dataSnapshot.getChildren()){
                            Favorites f = postData.getValue(Favorites.class);
                            if (f != null){
                                if (f.getUserId().equals(user.getUid())){
                                    if (f.isCheck()){
                                        Glide.with(Objects.requireNonNull(getActivity()))
                                                .load(R.drawable.ic_favorite_orange_24dp)
                                                .into(foodViewHolder.nearFavoriteClean);
                                    }else {
                                        Glide.with(Objects.requireNonNull(getActivity()))
                                                .load(R.drawable.ic_favorite_border_black_24dp)
                                                .into(foodViewHolder.nearFavoriteClean);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
                            foodViewHolder.nearNberFavorite.setText(String.valueOf(average));
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


                Glide.with(Objects.requireNonNull(getActivity())).load(food.getImage())
                        .into(foodViewHolder.nearFoodImage);
                foodViewHolder.nearFoodName.setText(food.getName());
                foodViewHolder.restaurant.setText(restaurantName);
                foodViewHolder.nearFoodCategory.setText(categoryName);
                ItemClickSupport.addTo(nearMenu).setOnItemClickListener(
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
            public NearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getActivity())
                        .inflate(R.layout.near_content, parent, false );

                return new NearViewHolder(v);
            }

        };
        adapter.startListening();
        nearMenu.setAdapter(adapter);
        //TODO: Complete this code for restaurant filtering. This part is delimited
        //Ce code est à compléter pour faire le filtrage d'affichage des restaus par n km de rayon
        //[-- BEGIN --]
        GeoFire geoFire = new GeoFire(db.getReference("Restaurant_location"));
        GeoQuery query = geoFire.queryAtLocation(
                new GeoLocation(usersLatitude, usersLongitude), 5);
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
        //[! -- END --]
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