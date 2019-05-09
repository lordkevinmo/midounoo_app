package com.midounoo.midounoo.Base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class BureauActivity extends AppCompatActivity {

    EditText adresseBureau;
    final static String st = "Modifier votre adresse de bureau";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bureau);

        //Firebase Init
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase mfbDatabase;
        //Récupération du unique id de l'utilisateur.
        if (user != null){
            String uid = user.getUid();
            mfbDatabase = FirebaseDatabase.getInstance();
            //récupération du champ Users!
            mDatabase = mfbDatabase.getReference().child("Users").child(uid);
        }

        adresseBureau = findViewById(R.id.bureauText);

        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User adresse = dataSnapshot.getValue(User.class);
                adresseBureau.setText(adresse.getBureau());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(vel);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarbur);
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
    }

    public void setBureau(View view) {
        try {
            mDatabase.child("bureau").setValue(adresseBureau.getText().toString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finish();
    }
}
