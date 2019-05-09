package com.midounoo.midounoo.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.midounoo.midounoo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BoissonViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgBoisson;
    public TextView boissonTitle;
    public TextView boissonPrice;

    public BoissonViewHolder(@NonNull View itemView) {
        super(itemView);
        imgBoisson = itemView.findViewById(R.id.gin);
        boissonTitle = itemView.findViewById(R.id.nom_boisson);
        boissonPrice = itemView.findViewById(R.id.prix_boisson);
    }
}
