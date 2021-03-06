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

public class PhoneActivity extends AppCompatActivity {

    EditText phone;
    final static String st = "Modifier votre numéro de téléphone";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Récupération du unique id de l'utilisateur.
        if (user != null){
            String uid = user.getUid();
            FirebaseDatabase mfbDatabase = FirebaseDatabase.getInstance();
            //récupération du champ Users!
            mDatabase = mfbDatabase.getReference().child("Users").child(uid);
        }

        phone = findViewById(R.id.phoneText);

        ValueEventListener userV = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    phone.setText(user.getNumberPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(userV);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbarp);
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

    public void setUserPhone(View v){
        try {
            mDatabase.child("numberPhone").setValue(phone.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
