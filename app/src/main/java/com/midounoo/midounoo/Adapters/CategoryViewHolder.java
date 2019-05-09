package com.midounoo.midounoo.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.midounoo.midounoo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.categoryImage);
            textView = itemView.findViewById(R.id.categoryName);
        }
    }
