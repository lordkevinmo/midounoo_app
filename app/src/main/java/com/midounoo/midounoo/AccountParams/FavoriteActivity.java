package com.midounoo.midounoo.AccountParams;


import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.midounoo.midounoo.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    FirebaseDatabase mDatabase;
    DatabaseReference reference;
    FirebaseRecyclerAdapter adapter;
    private static final String st = "Mes favoris";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //Firebase Init
        mDatabase = FirebaseDatabase.getInstance();
        reference = mDatabase.getReference("Favorite");
        recyclerView = findViewById(R.id.favoriteView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Toolbar toolbar = findViewById(R.id.favoriteappbar);
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
            loadFavorites(user.getUid());
        }
    }

    private void loadFavorites(String uid) {

    }
}
