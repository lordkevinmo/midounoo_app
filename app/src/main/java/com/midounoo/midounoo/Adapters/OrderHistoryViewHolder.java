package com.midounoo.midounoo.Adapters;

import android.view.View;
import android.widget.TextView;

import com.midounoo.midounoo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderHistoryViewHolder extends RecyclerView.ViewHolder {

    public TextView historyId, historyDate, historyStatus, historyPhone;

    public OrderHistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        historyId = itemView.findViewById(R.id.historyId);
        historyDate = itemView.findViewById(R.id.historyDate);
        historyStatus = itemView.findViewById(R.id.historyStatus);
        historyPhone = itemView.findViewById(R.id.historyPhone);
    }
}
