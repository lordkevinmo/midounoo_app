package com.midounoo.midounoo.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.midounoo.midounoo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OtherViewHolder extends RecyclerView.ViewHolder  {


    public ImageView otherFoodImage, otherFavoriteClean, otherStart;
    public TextView otherCity, delay, otherNberFavorite, restaurant, status;
    public View mView;

    public OtherViewHolder(@NonNull View itemView) {
        super(itemView);
        otherFoodImage = itemView.findViewById(R.id.other_foodImage);
        otherFavoriteClean = itemView.findViewById(R.id.other_favorite_clean);
        otherCity = itemView.findViewById(R.id.other_city);
        delay = itemView.findViewById(R.id.other_delay);
        restaurant = itemView.findViewById(R.id.other_restaurantName);
        otherStart = itemView.findViewById(R.id.other_startImage);
        status = itemView.findViewById(R.id.statusRestau);
        otherNberFavorite = itemView.findViewById(R.id.other_nberFavorite);
        mView = itemView;
    }
}
