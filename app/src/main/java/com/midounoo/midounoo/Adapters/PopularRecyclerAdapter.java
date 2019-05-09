package com.midounoo.midounoo.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.midounoo.midounoo.Base.FoodDetailsActivity;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.Food;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Utility.ItemClickSupport;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class PopularRecyclerAdapter extends FirebaseRecyclerAdapter<Food, PopularViewHolder> {

        @BindView(R.id.popular_foodImage) ImageView img;

        private Activity mActivity;
        private Query query;
        private String categoryName, restaurantName;
        private RecyclerView rView;
        /**
         * @param mRef            The Firebase location to watch for data changes. Can also be a slice of a location, using some
         *                        combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
         * @param mModelClass     Firebase will marshall the data at a location into an instance of a class that you provide
         * @param mLayout         This is the mLayout used to represent a single list item. You will be responsible for populating an
         *                        instance of the corresponding view with the data from an instance of mModelClass.
         * @param activity        The activity containing the ListView
         * @param viewHolderClass
         */
        public PopularRecyclerAdapter(Query mRef, Class<Food> mModelClass, int mLayout,
                                    Activity activity, Class<PopularViewHolder> viewHolderClass,
                                      RecyclerView rv) {
            super(mRef, mModelClass, mLayout, activity, viewHolderClass, rv);
            this.mActivity = activity;
            this.query = mRef;
            this.rView = rv;
        }

        @Override
        protected void populateViewHolder(PopularViewHolder viewHolder, Food model,
                                          final int position, List<String> mKeys) {

            if (model != null){
                FirebaseDatabase.getInstance().getReference("Category")
                        .child(model.getMenuId()).child("name")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                categoryName = dataSnapshot.getValue(String.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                FirebaseDatabase.getInstance().getReference("Restaurants").
                        child(model.getRestaurantId()).child("nameRestau")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                restaurantName = dataSnapshot.getValue(String.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                Glide.with(viewHolder.popularFoodImage.getContext())
                        .load(model.getImage()).into(viewHolder.popularFoodImage);
                viewHolder.popularFoodName.setText(model.getName());
                viewHolder.restaurant.setText(restaurantName);
                viewHolder.popularFoodCategory.setText(categoryName);
                viewHolder.popularNberFavorite.setText(String.valueOf(CommonClass.rating));

                ItemClickSupport.addTo(rView).setOnItemClickListener(
                    ((recyclerView, position1, v) -> {
                        /*Intent intent = new Intent(viewHolder.mView.getContext(),
                                FoodDetailsActivity.class);
                        intent.putExtra("FoodId", mKeys.get(position1));
                        mActivity.startActivity(intent);*/
                    })
                );
            }
        }

        @Override
        protected List<Food> filters(List<Food> models, CharSequence constraint) {
            return null;
        }

        @Override
        protected Map<String, Food> filterKeys(List<Food> mModels) {
            return null;
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }

