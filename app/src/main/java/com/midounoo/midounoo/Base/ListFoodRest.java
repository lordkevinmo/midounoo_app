package com.midounoo.midounoo.Base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midounoo.midounoo.Adapters.FoodViewHolder;
import com.midounoo.midounoo.Model.Food;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Utility.ItemClickSupport;

public class ListFoodRest extends AppCompatActivity {

    private DatabaseReference food;
    private FirebaseRecyclerAdapter adapter;

    private String categoryName, restaurantName;
    RecyclerView foodsList;
    String  key;
    DatabaseReference category, restaurants;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food_rest);


        foodsList = findViewById(R.id.foods);
        //Getting the intent key
        assert getIntent()!=null;
        key = getIntent().getStringExtra("key");

        //Firebase init
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        restaurants = db.getReference("Restaurants");
        food = db.getReference("Food");
        category = db.getReference("Category");

        //RecyclerView setup
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        foodsList.setLayoutManager(lm);
        foodsList.setItemAnimator(new DefaultItemAnimator());

        prepareMenus(key);
        //Toolbar setup
        Toolbar bar = findViewById(R.id.foods_toolbar);
        setSupportActionBar(bar);
        actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        bar.setNavigationOnClickListener(
                (v -> finish())
        );
    }

    private void prepareMenus(String cle) {
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(food.orderByChild("restaurantId").equalTo(cle), Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder foodViewHolder,
                                            int i, @NonNull final Food food) {

                //Query for populate the view
                category.child(food.getMenuId()).child("name")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            categoryName = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
                );

                restaurants.child(food.getRestaurantId()).child("nameRestau")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            restaurantName = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
                );


                Glide.with(ListFoodRest.this)
                        .load(food.getImage()).into(foodViewHolder.foodImage);
                foodViewHolder.foodName.setText(food.getName());
                foodViewHolder.foodCategory.setText(categoryName);
                foodViewHolder.nberFavorite.setText("0");
                foodViewHolder.restaurant.setText(restaurantName);
                ItemClickSupport.addTo(foodsList).setOnItemClickListener(
                    ((recyclerView, position, v) -> {
                        Intent foodDetail = new Intent(ListFoodRest.this,
                                FoodDetailsActivity.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        foodDetail.putExtra("restau", restaurantName);
                        startActivity(foodDetail);
                    })
                );
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(ListFoodRest.this)
                        .inflate(R.layout.category_detail, parent, false );

                return new FoodViewHolder(v);
            }
        };
        adapter.startListening();
        foodsList.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
