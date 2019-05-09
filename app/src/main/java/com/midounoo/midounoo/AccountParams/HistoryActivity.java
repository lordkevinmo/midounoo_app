package com.midounoo.midounoo.AccountParams;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.midounoo.midounoo.Adapters.OrderHistoryViewHolder;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.Request;
import com.midounoo.midounoo.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
    Cette classe sert à lutilisateur de visionner ses activités de commande.
    Elle liste via une recyclerview l'historique des commandes de l'utilisateur.
    Les fichiers directement impliqués dans cette fonctionnalité sont :
    - Adapters/OrderHistoryViewHolder
    - res/layout/orderhistory
 */

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    FirebaseDatabase mDatabase;
    DatabaseReference reference;
    FirebaseRecyclerAdapter adapter;
    private static final String st = "Historique des commandes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Firebase Init
        mDatabase = FirebaseDatabase.getInstance();
        reference = mDatabase.getReference("Requests");
        recyclerView = findViewById(R.id.historyView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Toolbar toolbar = findViewById(R.id.historyappbar);
        setSupportActionBar(toolbar);
        ActionBar action = getSupportActionBar();
        if (action != null) {
            action.setTitle(st);
            action.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener((
                v -> finish()
        ));

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Récupération du unique id de l'utilisateur.
        if (user != null) {
            loadOrders(user.getUid());
        }
    }

    /* loadOrders prend en argument le numéro de téléphone de l'utilisateur et le compare
     * aux différentes valeurs de phone dans la liste des commandes. S'il trouve une correspondance,
     * elle sera afficher. */

    private void loadOrders(String userId) {
        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(reference.orderByChild("userId").equalTo(userId), Request.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderHistoryViewHolder>(options) {
            @NonNull
            @Override
            public OrderHistoryViewHolder onCreateViewHolder(
                    @NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.orderhistory,
                        parent,
                        false
                );

                return new OrderHistoryViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(
                    @NonNull final OrderHistoryViewHolder orderHistoryViewHolder,
                    final int i, @NonNull final Request request) {
                orderHistoryViewHolder.historyId.setText(adapter.getRef(i).getKey());
                orderHistoryViewHolder.historyDate.setText(request.getDate());
                orderHistoryViewHolder.historyPhone.setText(request.getPhone());
                orderHistoryViewHolder.historyStatus.setText(CommonClass
                        .setStatuts(request.getStatus()));
            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
