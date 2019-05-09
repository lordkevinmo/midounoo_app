package com.midounoo.midounoo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.midounoo.midounoo.Base.FoodDetailsActivity;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.Restaurants;
import com.midounoo.midounoo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class OtherAdapter extends RecyclerView.Adapter<OtherViewHolder> {

    private Context mCntext;
    private List<Restaurants> restaurants;

    public OtherAdapter(Context mCntext, List<Restaurants> restaurants) {
        this.mCntext = mCntext;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public OtherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.other_content, parent, false);

        return new OtherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OtherViewHolder holder, int position) {
        Restaurants restau = restaurants.get(position);
        Glide.with(mCntext).load(restau.getImage()).into(holder.otherFoodImage);
        holder.restaurant.setText(restau.getNameRestau());
        holder.otherCity.setText(restau.getAdresse());
        holder.delay.setText(R.string.delay_w);
        holder.otherNberFavorite.setText(String.valueOf(CommonClass.rating));
        if (restau.isOpen()) {
            holder.status.setTextColor(Color.GREEN);
            holder.status.setText(R.string.open);
        } else {
            holder.status.setTextColor(Color.RED);
            holder.status.setText(R.string.closed);
        }
        holder.mView.setOnClickListener(
                (v -> {
                    Intent i = new Intent(mCntext.getApplicationContext(),
                            FoodDetailsActivity.class);
                    i.putExtra("key", getRestaurant(position).getId());
                    mCntext.startActivity(i);
                })
        );
    }

    public void setRestaurants(List<Restaurants> restaurantsList){
        restaurants = restaurantsList;
        notifyDataSetChanged();
    }

    public Restaurants getRestaurant(int i){
        if (i > restaurants.size() - 1)
            return new Restaurants();
        else
            return restaurants.get(i);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}
