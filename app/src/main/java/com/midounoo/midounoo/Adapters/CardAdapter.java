package com.midounoo.midounoo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.midounoo.midounoo.AdapterModel.Card;
import com.midounoo.midounoo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context mContext;
    private List<Card> cards;

    public CardAdapter(Context context, List<Card> cardList){
        this.mContext = context;
        this.cards = cardList;
    }

    public Card getCardPosition(int position){return cards.get(position);}

    @NonNull
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_content, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card carte = getCardPosition(position);
        holder.carttextview.setText(carte.getCardLabel());
        Glide.with(mContext).load(carte.getCardIcone()).into(holder.cardView);

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        private ImageView cardView;
        private TextView carttextview;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            carttextview = itemView.findViewById(R.id.cardtextview);
        }
    }
}
