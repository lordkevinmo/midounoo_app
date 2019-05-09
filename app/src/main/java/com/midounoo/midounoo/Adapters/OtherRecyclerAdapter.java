package com.midounoo.midounoo.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.Query;
import com.midounoo.midounoo.Base.ListFoodRest;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.Restaurants;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Utility.ItemClickSupport;


import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class OtherRecyclerAdapter extends FirebaseRecyclerAdapter<Restaurants, OtherViewHolder> {

    @BindView(R.id.other_foodImage) ImageView image;
    @BindView(R.id.other_city) TextView city;
    @BindView(R.id.other_restaurantName) TextView restau;
    @BindView(R.id.other_delay) TextView delay;
    @BindView(R.id.other_nberFavorite) TextView nber;
    @BindView(R.id.statusRestau) TextView status;

    Activity mActivity;
    Query query;
    RecyclerView recyclerView;

    /**
     * @param mRef            The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param mModelClass     Firebase will marshall the data at a location into an instance of a class that you provide
     * @param mLayout         This is the mLayout used to represent a single list item. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of mModelClass.
     * @param activity        The activity containing the ListView
     * @param viewHolderClass
     */
    public OtherRecyclerAdapter(Query mRef, Class<Restaurants> mModelClass, int mLayout,
                                Activity activity,
                                Class<OtherViewHolder> viewHolderClass, RecyclerView rView) {
        super(mRef, mModelClass, mLayout, activity, viewHolderClass, rView);
        this.mActivity = activity;
        this.query = mRef;
        this.recyclerView = rView;
    }

    @Override
    protected void populateViewHolder(OtherViewHolder viewHolder, Restaurants model,
                                      int position, List<String> mKeys) {
        if (model != null) {
            Glide.with(viewHolder.otherFoodImage.getContext())
                    .load(model.getImage()).into(viewHolder.otherFoodImage);
            viewHolder.otherCity.setText(model.getAdresse());
            viewHolder.restaurant.setText(model.getNameRestau());
            viewHolder.delay.setText(R.string.delay_w);
            viewHolder.otherNberFavorite.setText(String.valueOf(CommonClass.rating));
            if (model.isOpen()) {
                viewHolder.status.setTextColor(Color.GREEN);
                viewHolder.status.setText(R.string.open);
            } else {
                viewHolder.status.setTextColor(Color.RED);
                viewHolder.status.setText(R.string.closed);
            }
        }
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
            ((recyclerView, position1, v) -> {
                Intent intent = new Intent(viewHolder.mView.getContext(),
                        ListFoodRest.class);
                intent.putExtra("key", mKeys.get(position1));
                mActivity.startActivity(intent);
            })
        );
    }

    @Override
    protected List<Restaurants> filters(List<Restaurants> models, CharSequence constraint) {
        return null;
    }

    @Override
    protected Map<String, Restaurants> filterKeys(List<Restaurants> mModels) {
        return null;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
