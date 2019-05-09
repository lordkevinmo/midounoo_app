package com.midounoo.midounoo.Base;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midounoo.midounoo.Model.User;
import com.midounoo.midounoo.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ReplaceNameActivity extends AppCompatActivity {

    EditText nom;
    final static String st = "Modifier votre nom";
    private FirebaseDatabase mfbDatabase;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_name);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Récupération du unique id de l'utilisateur.
        if (user != null){
            String uid = user.getUid();
            mfbDatabase = FirebaseDatabase.getInstance();
            //récupération du champ Users!
            mDatabase = mfbDatabase.getReference().child("Users").child(uid);
        }

        nom = findViewById(R.id.nameText);


        ValueEventListener userV = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user!= null) {
                    nom.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(userV);

        //Opération sur le toolbar
        Toolbar toolbar = findViewById(R.id.toolbarR);
        setSupportActionBar(toolbar);
        ActionBar action = getSupportActionBar();
        if (action != null) {
            action.setDisplayHomeAsUpEnabled(true);
            action.setTitle(st);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void setName(View view) {

        try {
            mDatabase.child("name").setValue(nom.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
