package com.midounoo.midounoo.AccountParams;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.midounoo.midounoo.AdapterModel.Card;
import com.midounoo.midounoo.Adapters.CardAdapter;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Utility.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentActivity extends AppCompatActivity {

    private static final String st = "Méthode de paiement";
    private RecyclerView paiement;
    CardAdapter adapter;
    List<Card> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //Definition of the actionbar
        Toolbar toolbar = findViewById(R.id.paymentappbar);
        setSupportActionBar(toolbar);
        ActionBar action = getSupportActionBar();
        if (action != null) {
            action.setTitle(st);
            action.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //
        paiement = findViewById(R.id.paymentView);
        cards = new ArrayList<>();
        adapter = new CardAdapter(PaymentActivity.this, cards);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        paiement.setLayoutManager(layoutManager);
        paiement.setItemAnimator(new DefaultItemAnimator());
        paiement.setAdapter(adapter);

        loadMethodPaiement();

        this.configureRecyclerClick();
    }

    private void loadMethodPaiement() {
        int carts[] = new int[] {
          R.drawable.icons8_visa_24,
          R.drawable.icons8_paypal_24
        };

        Card cart = new Card(carts[0], "Carte de crédit ou de débit");
        cards.add(cart);
        cart = new Card(carts[1], "Paypal");
        cards.add(cart);

        adapter.notifyDataSetChanged();
    }

    private void configureRecyclerClick(){
        ItemClickSupport.addTo(paiement).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Card c = adapter.getCardPosition(position);
                switch (c.getCardLabel()){
                    case "Carte de crédit ou de débit":
                        break;
                    case "Paypal":
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void sendToActivity(Class mClasse) {
        startActivity(new Intent(PaymentActivity.this, mClasse));
    }


}
