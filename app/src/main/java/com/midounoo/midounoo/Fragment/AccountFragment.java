package com.midounoo.midounoo.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.midounoo.midounoo.AccountParams.FavoriteActivity;
import com.midounoo.midounoo.AccountParams.HistoryActivity;
import com.midounoo.midounoo.AccountParams.PaymentActivity;
import com.midounoo.midounoo.AccountParams.SettingsActivity;
import com.midounoo.midounoo.AccountParams.SupportActivity;
import com.midounoo.midounoo.AdapterModel.Account;
import com.midounoo.midounoo.Adapters.AccountAdapter;
import com.midounoo.midounoo.Utility.ItemClickSupport;
import com.midounoo.midounoo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private AccountAdapter accountAdapter;
    private List<Account> accountList;
    private RecyclerView recyclerView;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        recyclerView = view.findViewById(R.id.accountView);
        accountList = new ArrayList<>();
        accountAdapter = new AccountAdapter(getActivity(), accountList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(accountAdapter);

        prepareAccount();

        this.configureOnClickRecyclerView();
        return view;
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
            ((recyclerView1, position, v) -> {
                Account account = accountAdapter.getAccountPosition(position);
                switch (account.getAccountTitre()){
                    case "Paramètres":
                        sendToActivity(SettingsActivity.class);
                        break;
                    case "Support" :
                        sendToActivity(SupportActivity.class);
                        break;
                    case "Historique" :
                        sendToActivity(HistoryActivity.class);
                        break;
                    case "Paiement":
                        sendToActivity(PaymentActivity.class);
                        break;
                    case "Vos favoris":
                        sendToActivity(FavoriteActivity.class);
                        break;
                    default:
                        break;
                }
            })
        );
    }

    private void sendToActivity(Class mClasse) {
        startActivity(new Intent(getActivity(), mClasse));
    }

    private void prepareAccount() {

        int accounts[] = new int[] {
                R.drawable.baseline_favorite_black_24,
                //R.drawable.baseline_payment_black_24,
                R.drawable.baseline_history_black_24,
                R.drawable.baseline_help_black_24,
                R.drawable.baseline_settings_black_24
        };

        Account account = new Account("Vos favoris", accounts[0]);
        accountList.add(account);
       // account = new Account("Paiement", accounts[1]);
        //accountList.add(account);
        account = new Account("Historique", accounts[1]);
        accountList.add(account);
        account = new Account("Support", accounts[2]);
        accountList.add(account);
        account = new Account("Paramètres", accounts[3]);
        accountList.add(account);

        accountAdapter.notifyDataSetChanged();
    }

}
