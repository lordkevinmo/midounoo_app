package com.midounoo.midounoo.Base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midounoo.midounoo.Model.User;
import com.midounoo.midounoo.R;

import org.json.JSONException;

public class FacebookLogin extends AppCompatActivity {

    private static final String TAG = FacebookLogin.class.getSimpleName();

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private CallbackManager mCallbackManager;
    private DatabaseReference userReference;

    String nom, prenom, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton =  findViewById(R.id.fb);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                changeActivity(LoginActivity.class);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Snackbar.make(findViewById(R.id.facebook_login_layout),
                        error.getMessage(), Snackbar.LENGTH_SHORT).show();
                // [START_EXCLUDE]
                changeActivity(LoginActivity.class);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]


    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            changeActivity(MainActivity.class);
        }
    }
    // [END on_start_check_user]

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this,
                (task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;

                        userReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    changeActivity(MainActivity.class);
                                } else {
                                    getUserInformation(token);
                                    if (email == null) {
                                        if (user != null) email = user.getEmail();
                                        else email = "email@midounoo.com";
                                    }

                                    if (nom == null) nom = "Nom";

                                    if (prenom == null) prenom = "Prenom";

                                    assert user != null;
                                    writeUser(user.getUid(), nom, prenom, email, "XX XX XX XX");

                                    changeActivity(MainActivity.class);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.facebook_login_layout),
                                "Authentication failed.",
                                Snackbar.LENGTH_SHORT).show();
                        changeActivity(LoginActivity.class);
                    }
                }
            )
        );
    }
    // [END auth_with_facebook]

    private void getUserInformation(AccessToken token){
        GraphRequest request = GraphRequest.newMeRequest(token,
            ((object, response) -> {
                try{
                    prenom = object.getString("first_name");
                    nom = object.getString("last_name");
                    email = object.getString("email");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }));
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void changeActivity(Class mclasse){
        Intent newIntent = new Intent(FacebookLogin.this, mclasse);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntent);
    }

    // Méthode pour persister l'objet dans la bdd
    private void writeUser(String Uid, String name, String fName, String email,
                           String numberPhone){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance()
                .getReference();
        User user = new User(name, fName, email, numberPhone);

        mDatabase.child("Users").child(Uid).setValue(user);
    }
}
