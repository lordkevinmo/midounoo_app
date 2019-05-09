package com.midounoo.midounoo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.midounoo.midounoo.Model.Lieux;
import com.midounoo.midounoo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LieuxAdapter extends RecyclerView.Adapter<LieuxAdapter.LieuxViewHolder> {

    private Context mContext;
    private List<Lieux> listeLieux;

    @NonNull
    @Override
    public LieuxViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lieux_content, viewGroup, false);
        return new LieuxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LieuxViewHolder lieuxViewHolder, int i) {
        Lieux lieux = listeLieux.get(i);
        lieuxViewHolder.lieuxTitre.setText(lieux.getLieuxTitre());
        lieuxViewHolder.lieuxAdresse.setText(lieux.getLieuxAdresse());
        Glide.with(mContext).load(lieux.getLieuxIcone()).into(lieuxViewHolder.lieuxIcone);
    }

    @Override
    public int getItemCount() {
        return listeLieux.size();
    }

    public LieuxAdapter(Context mContext, List<Lieux> listeLieux){
        this.listeLieux = listeLieux;
        this.mContext = mContext;
    }

    static class LieuxViewHolder extends RecyclerView.ViewHolder{

        private ImageView lieuxIcone;
        private TextView lieuxTitre, lieuxAdresse;

        public LieuxViewHolder(@NonNull View itemView) {
            super(itemView);
            lieuxIcone = itemView.findViewById(R.id.lieuxIcone);
            lieuxTitre = itemView.findViewById(R.id.lieuxTitre);
            lieuxAdresse = itemView.findViewById(R.id.lieuxAdresse);
        }
    }
    //Return the position of element in the recyclerview
    public Lieux getLieuxPosition(int position) {
        return listeLieux.get(position);
    }

}
