package com.midounoo.midounoo.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midounoo.midounoo.Adapters.FoodViewHolder;
import com.midounoo.midounoo.Base.FoodDetailsActivity;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.Food;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Utility.ItemClickSupport;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CategoryDetailsFragment extends Fragment {

    private DatabaseReference food, restaurant;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView categorydetail;
    private String CategoryId = "";
    private String categoryName, restaurantName;

    public CategoryDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        RecyclerView.LayoutManager manager;

        //Firebase Init

        DatabaseReference category;
        FirebaseDatabase db;
        db = FirebaseDatabase.getInstance();
        food = db.getReference("Food");
        category = db.getReference("Category");
        restaurant = db.getReference("Restaurants");


        //RecycleView manage

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_details, container, false);

        categorydetail = view.findViewById(R.id.categorydetail);
        manager = new LinearLayoutManager(getActivity());
        categorydetail.setLayoutManager(manager);
        categorydetail.setItemAnimator(new DefaultItemAnimator());

        if (getArguments() != null) {
            Bundle arguments = getArguments();
            CategoryId = arguments.getString("CategoryId");
        }
        if (!TextUtils.isEmpty(CategoryId)){
            category.child(CategoryId).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    categoryName = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if (CommonClass.isConnectedToInternet(Objects.requireNonNull(getActivity())))
                loadFoodByCategory(CategoryId);
            else
                Toast.makeText(getActivity(), R.string.connection, Toast.LENGTH_SHORT).show();
        }



        return view;
    }

    private void loadFoodByCategory(String categoryId) {
        
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(food.orderByChild("menuId").equalTo(categoryId), Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getActivity())
                        .inflate(R.layout.category_detail, parent, false );

                return new FoodViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder foodViewHolder, int i, @NonNull Food food) {
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
                Glide.with(Objects.requireNonNull(getActivity()))
                        .load(food.getImage()).into(foodViewHolder.foodImage);
                foodViewHolder.foodName.setText(food.getName());
                foodViewHolder.foodCategory.setText(categoryName);
                foodViewHolder.nberFavorite.setText("0");
                foodViewHolder.restaurant.setText(restaurantName);
                ItemClickSupport.addTo(categorydetail).setOnItemClickListener(
                    ((recyclerView, position, v) -> {
                        Intent foodDetail = new Intent(getActivity(), FoodDetailsActivity.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        foodDetail.putExtra("restau", categoryName);
                        startActivity(foodDetail);
                    })
                );
            }
        };

        categorydetail.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
