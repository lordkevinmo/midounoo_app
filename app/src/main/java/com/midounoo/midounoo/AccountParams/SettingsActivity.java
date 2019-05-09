package com.midounoo.midounoo.AccountParams;

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
import com.midounoo.midounoo.Base.BeginActivity;
import com.midounoo.midounoo.Base.BureauActivity;
import com.midounoo.midounoo.Base.DomicileActivity;
import com.midounoo.midounoo.Base.UserActivity;
import com.midounoo.midounoo.Model.Lieux;
import com.midounoo.midounoo.Adapters.LieuxAdapter;
import com.midounoo.midounoo.Model.User;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Utility.ItemClickSupport;
import com.midounoo.midounoo.Utility.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 123;

    private RecyclerView recyclerView;
    CircleImageView photoDeProfil;
    private TextView username;
    FirebaseDatabase mfbDatabase;
    private DatabaseReference mDatabase;
    LieuxAdapter lieuxAdapter;
    List<Lieux> lieuxList;

    private static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private  String domicile = "Ajouter un domicile";
    private  String bureau = "Ajouter un bureau";
    private static final String mDomicile = "Domicile";
    private static final String mBureaux = "Bureau";
    private static final String st = "Paramètres";
    private StorageReference reference;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Récupération des champs utilisateur(premier card)
        recyclerView = findViewById(R.id.placelistview);
        photoDeProfil = findViewById(R.id.photoDeProfil);
        username = findViewById(R.id.username);

        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        ActionBar action = getSupportActionBar();
        if (action != null) {
            action.setTitle(st);
            action.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(
                (v -> finish())
        );

        Glide.with(this).load(R.drawable.ic_person_outline_black_24dp).into(photoDeProfil);

        // Récupération des instances firebase.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Récupération du unique id de l'utilisateur.
        if (user != null){
            String uid = user.getUid();
            mfbDatabase = FirebaseDatabase.getInstance();
            //récupération du champ Users!
            mDatabase = mfbDatabase.getReference().child("Users").child(uid);
        }

        photoDeProfil.setOnClickListener(
            (v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent,
                        "Selectionner une image"), RC_PHOTO_PICKER);
            })
        );


        ValueEventListener userV = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 mUser = dataSnapshot.getValue(User.class);
                if (mUser != null) {
                    username.setText(mUser.getfName());
                    domicile = mUser.getDomicile();
                    bureau = mUser.getBureau();
                    try {
                        if (mUser.getPhotoUrl() != null)
                            Glide.with(SettingsActivity.this)
                                .load(mUser.getPhotoUrl()).into(photoDeProfil);
                        else
                            Glide.with(SettingsActivity.this)
                                    .load(R.drawable.ic_person_outline_black_24dp)
                                    .into(photoDeProfil);
                    }catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(userV);

        //setup recycleview
        lieuxList = new ArrayList<>();
        lieuxAdapter = new LieuxAdapter(SettingsActivity.this, lieuxList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, VERTICAL_LIST, 2));
        recyclerView.setAdapter(lieuxAdapter);

        this.configureOnClickRecyclerView();
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
            ((recyclerView1, position, v) -> {
                Lieux lieux = lieuxAdapter.getLieuxPosition(position);
                switch (lieux.getLieuxTitre()){
                    case "Domicile":
                        sendToActivity(DomicileActivity.class);
                        break;
                    case "Bureau" :
                        sendToActivity(BureauActivity.class);
                        break;
                    default:
                        break;
                }
            })
        );
    }

    private void prepareLieux() {
        int lieux[] = new int[] {
                R.drawable.ic_business_black_24dp,
                R.drawable.ic_home_black_24dp
        };

        Lieux mLieux = new Lieux(lieux[1], mDomicile, domicile);
        lieuxList.add(mLieux);
        mLieux = new Lieux(lieux[0], mBureaux, bureau);
        lieuxList.add(mLieux);

        lieuxAdapter.notifyDataSetChanged();
    }

    private void sendToActivity(Class mClasse) {
        startActivity(new Intent(SettingsActivity.this, mClasse));
    }

    public void ModifierAction(View view){
        sendToActivity(UserActivity.class);
    }

    public void Deconnexion(View view){
        FirebaseAuth.getInstance().signOut();
        Intent deconnexion = new Intent(SettingsActivity.this, BeginActivity.class);
        deconnexion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(deconnexion);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareLieux();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK &&
                data != null && data.getData() != null){
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null){
                try {
                    Glide.with(SettingsActivity.this)
                            .load(selectedImageUri)
                            .into(photoDeProfil);
                }catch (Exception e){}

                String imName = UUID.randomUUID().toString();
                final StorageReference photoRef = reference.child("photo_profile/"+imName);
                photoRef.putFile(selectedImageUri).addOnSuccessListener(SettingsActivity.this,
                    (taskSnapshot) -> {
                        Toast.makeText(SettingsActivity.this,
                                "Upload réussi", Toast.LENGTH_SHORT).show();
                        photoRef.getDownloadUrl().addOnSuccessListener((uri) ->
                            {

                                mUser.setPhotoUrl(uri.toString());
                                mDatabase.setValue(mUser);
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
