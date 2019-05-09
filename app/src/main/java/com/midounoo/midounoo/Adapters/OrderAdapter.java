package com.midounoo.midounoo.Adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.Order;
import com.midounoo.midounoo.R;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    private Context context;
    private List<Order> orderList = Collections.emptyList();

    public OrderAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);

        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        Locale locale = new Locale("fr", "TG");
        NumberFormat nbFormat = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(order.getProductPrice())) * (Integer.parseInt(order.getProductQuantity()));
        holder.orderPrice.setText(nbFormat.format(price));
        holder.orderItem.setText(order.getProductName());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrderList(List<Order> orders) {
        orderList = orders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView orderItem;
        private TextView orderPrice;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItem = itemView.findViewById(R.id.order_item);
            orderPrice = itemView.findViewById(R.id.order_price);

            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(R.string.action_select);
            menu.add(0,0, getAdapterPosition(), CommonClass.DELETE);
        }
    }

}
