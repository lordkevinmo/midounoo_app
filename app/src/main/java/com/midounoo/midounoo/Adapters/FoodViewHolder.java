package com.midounoo.midounoo.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.midounoo.midounoo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodViewHolder extends RecyclerView.ViewHolder {

    public ImageView foodImage, favoriteClean;
    public TextView foodName, foodCategory, nberFavorite, restaurant;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        foodImage = itemView.findViewById(R.id.foodImage);
        favoriteClean = itemView.findViewById(R.id.favorite_clean);
        foodName = itemView.findViewById(R.id.foodName);
        foodCategory = itemView.findViewById(R.id.foodCategory);
        restaurant = itemView.findViewById(R.id.restaurantName);
        nberFavorite = itemView.findViewById(R.id.nberFavorite);
    }
}
