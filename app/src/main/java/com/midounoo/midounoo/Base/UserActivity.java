package com.midounoo.midounoo.Base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.midounoo.midounoo.AccountParams.SettingsActivity;
import com.midounoo.midounoo.Model.User;
import com.midounoo.midounoo.R;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private User mUser;
    TextView nomBdd, prenomBdd, mailBdd, phoneNumber;
    CircleImageView cv;
    private StorageReference reference;
    private static final int RC_PHOTO_PICKER = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        Toolbar tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        ActionBar action = getSupportActionBar();
        if (action != null) {
            action.setTitle(R.string.param_modif);
            action.setDisplayHomeAsUpEnabled(true);
        }
        tb.setNavigationOnClickListener(
            (v -> finish())
        );

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        nomBdd = findViewById(R.id.nomBdd);
        prenomBdd = findViewById(R.id.prenombdd);
        mailBdd = findViewById(R.id.mailbdd);
        phoneNumber = findViewById(R.id.phoneNumber);
        cv = findViewById(R.id.profil);
        Glide.with(this).load(R.drawable.ic_person_outline_black_24dp).into(cv);
        cv.setOnClickListener(
            (v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent,
                        "Selectionner une image"), RC_PHOTO_PICKER);
            })
        );

        if (user != null){
            String uuid = user.getUid();
            databaseReference = firebaseDatabase.getReference().child("Users").child(uuid);
        }

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                getUserValues();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private void getUserValues() {
        nomBdd.setText(mUser.getName());
        prenomBdd.setText(mUser.getfName());
        mailBdd.setText(mUser.getEmail());
        phoneNumber.setText(mUser.getNumberPhone());
        if (mUser.getPhotoUrl() != null){
            Glide.with(this).load(mUser.getPhotoUrl()).into(cv);
        } else {
            Glide.with(this).load(R.drawable.ic_person_outline_black_24dp).into(cv);
        }

    }

    private void sendToActivity(Class mClasse) {
        startActivity(new Intent(UserActivity.this, mClasse));
    }

    public void changeName(View view){
        sendToActivity(ReplaceNameActivity.class);
    }

    public void changeFName(View view){
        sendToActivity(ReplaceFNameActivity.class);
    }

    public void changeMail(View view) {
        sendToActivity(ChangeMailActivity.class);
    }

    public void changePhoneNumber(View view){
        sendToActivity(PhoneActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK &&
                data != null && data.getData() != null){
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null){
                String imName = UUID.randomUUID().toString();
                final StorageReference photoRef = reference.child("photo_profile/"+imName);
                photoRef.putFile(selectedImageUri).addOnSuccessListener(UserActivity.this,
                    (taskSnapshot) -> {
                        Toast.makeText(UserActivity.this,
                                "Upload réussi", Toast.LENGTH_SHORT).show();
                        photoRef.getDownloadUrl().addOnSuccessListener((uri) ->
                            {
                                mUser.setPhotoUrl(uri.toString());
                                databaseReference.setValue(mUser);
                            }
                        );
                    })
                    .addOnFailureListener((e -> Toast.makeText(this,
                            ""+ e.getMessage(), Toast.LENGTH_SHORT).show())
                    );
            }
        }
    }
}
