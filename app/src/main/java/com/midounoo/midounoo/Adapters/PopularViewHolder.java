package com.midounoo.midounoo.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midounoo.midounoo.Model.Favorites;
import com.midounoo.midounoo.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PopularViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public ImageView popularFoodImage, popularFavoriteClean;
    public TextView popularFoodName, popularFoodCategory, popularNberFavorite, restaurant;
    private View mView;
    public int position;
    public FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference favorite = FirebaseDatabase.getInstance()
            .getReference("Favorite");
    private String key = Objects.requireNonNull(
            FirebaseAuth.getInstance().getCurrentUser()).getUid();


    public PopularViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        popularFoodImage = itemView.findViewById(R.id.popular_foodImage);
        popularFavoriteClean = itemView.findViewById(R.id.popular_favorite_clean);
        popularFoodName = itemView.findViewById(R.id.popular_foodName);
        popularFoodCategory = itemView.findViewById(R.id.popular_foodCategory);
        restaurant = itemView.findViewById(R.id.popular_restaurantName);
        popularNberFavorite = itemView.findViewById(R.id.popular_nberFavorite);

        popularFavoriteClean.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        favorite.child(Objects.requireNonNull(mAdapter.getRef(position).getKey()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(key).exists()){
                    Favorites f = dataSnapshot.getValue(Favorites.class);
                    if (f != null) {
                        if (f.isCheck()){
                            f.setCheck(false);
                            Glide.with(mView.getContext())
                                    .load(R.drawable.ic_favorite_border_black_24dp)
                                    .into(popularFavoriteClean);
                        } else {
                            f.setCheck(true);
                            Glide.with(mView.getContext())
                                    .load(R.drawable.ic_favorite_orange_24dp)
                                    .into(popularFavoriteClean);
                        }

                        favorite.child(Objects.requireNonNull(mAdapter.getRef(position).getKey()))
                                .child(key).setValue(f);
                    }
                } else {
                    Favorites f = new Favorites(mAdapter.getRef(position).getKey(), key);
                    f.setCheck(true);
                    favorite.child(Objects.requireNonNull(mAdapter.getRef(position).getKey()))
                            .child(key).setValue(f);
                    Glide.with(mView.getContext())
                            .load(R.drawable.ic_favorite_orange_24dp)
                            .into(popularFavoriteClean);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
