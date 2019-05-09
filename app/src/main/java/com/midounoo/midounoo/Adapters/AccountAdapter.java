package com.midounoo.midounoo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.midounoo.midounoo.AdapterModel.Account;
import com.midounoo.midounoo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private Context mContext;
    private List<Account> listAccount;

     static class AccountViewHolder extends RecyclerView.ViewHolder {

        private TextView account_titre;
        private ImageView account_icone;

        public AccountViewHolder(View itemView) {
            super(itemView);
            account_titre = itemView.findViewById(R.id.account_titre);
            account_icone = itemView.findViewById(R.id.account_icon);
        }
    }

    public AccountAdapter(Context mContext, List<Account> listAccount) {
        this.mContext = mContext;
        this.listAccount = listAccount;
    }

    public Account getAccountPosition(int position){
          return listAccount.get(position);
    }

    @NonNull
    @Override
    public AccountAdapter.AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_content, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.AccountViewHolder holder, int position) {
        Account account = listAccount.get(position);
        holder.account_titre.setText(account.getAccountTitre());
        Glide.with(mContext).load(account.getAccountIcon()).into(holder.account_icone);
    }

    @Override
    public int getItemCount() {
        return listAccount.size();
    }


}
