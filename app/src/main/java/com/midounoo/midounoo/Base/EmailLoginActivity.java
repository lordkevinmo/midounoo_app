package com.midounoo.midounoo.Base;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.User;
import com.midounoo.midounoo.R;

import java.util.Objects;

public class EmailLoginActivity extends AppCompatActivity{

    TextInputEditText username, password, nom, prenom, telephone;
    TextInputLayout name_text_input, name_password_input,
            name_layout, prenom_layout, phone_layout;
    FirebaseAuth auth;
    ProgressBar progressBar;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        //initialiser firebase auth et databasereference
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //
        //Forcer l'affichage en mode portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Desactiver le clavier à l'affichage de la page
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Redéfinition de la ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar action = getSupportActionBar();
        if (action != null) {
            action.setTitle(null);
            action.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener((e) -> finish());

        //Récupérationn des objets username, password, ...
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        telephone = findViewById(R.id.telephone);

        // TExtinputLayout
        name_text_input = findViewById(R.id.name_text_input);
        prenom_layout = findViewById(R.id.prenom_layout);
        name_password_input = findViewById(R.id.name_password_input);
        name_layout = findViewById(R.id.name_layout);
        phone_layout = findViewById(R.id.phone_layout);

        progressBar = findViewById(R.id.progressBar);


        findViewById(R.id.forgotPassword).setOnClickListener(
                (event) -> startActivity(new
                        Intent(EmailLoginActivity.this, ForgotPassword.class))
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            Intent mainIntent = new Intent(EmailLoginActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }
    }

    public void ConnexionAction(View view){
        if (CommonClass.isConnectedToInternet(this))
            SignIn(Objects.requireNonNull(username.getText()).toString(),
                    Objects.requireNonNull(password.getText()).toString());
        else
            Snackbar.make(findViewById(R.id.signLayout),
                    R.string.connection, Snackbar.LENGTH_LONG).show();
    }


    public void SubscribeAction(View view){
        if (CommonClass.isConnectedToInternet(this))
            SignUp(Objects.requireNonNull(username.getText()).toString(),
                    Objects.requireNonNull(password.getText()).toString());
        else
            Snackbar.make(findViewById(R.id.signLayout),
                    R.string.connection, Snackbar.LENGTH_LONG).show();
    }

    private void SignUp(final String username, String password) {
        if (!validateFormSignUp()){
            return;
        }
        showProgressBar();

        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener(EmailLoginActivity.this,
                (task -> {
                    if (task.isSuccessful()) {
                        //Si l'inscription marche
                        FirebaseUser user = auth.getCurrentUser();
                        refreshUI(user);

                    } else {
                        //Si l'inscription échoue
                        Toast.makeText(EmailLoginActivity.this,
                                "L'inscription a échoué", Toast.LENGTH_SHORT).show();
                        refreshUI(null);
                    }
                    hideProgressBar();
                }));
    }


    // Fonction qui connecte l'utilisateur
    private  void SignIn(String username, String password) {
        if (!validateFormSignIn()){
            return;
        }

        showProgressBar();

        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(EmailLoginActivity.this,
                (task -> {
                    if (task.isSuccessful()){
                        // Si la connexion a réussi
                        FirebaseUser user = auth.getCurrentUser();
                        refreshUI(user);
                    }
                })).addOnFailureListener(
                (e -> {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(EmailLoginActivity.this,
                                "Le mot de passe est erroné", Toast.LENGTH_SHORT).show();
                        refreshUI(null);
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        String errorCode =
                                ((FirebaseAuthInvalidUserException) e).getErrorCode();
                        if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                            emailVerification();
                        }
                    }
                })
        );

        hideProgressBar();
    }

    // Méthode pour persister l'objet dans la bdd
    private void writeUser(String Uid, String name, String fName, String email,
                           String numberPhone){
        User user = new User(name, fName, email, numberPhone);

        mDatabase.child("Users").child(Uid).setValue(user);
    }

    private void refreshUI(FirebaseUser user) {

        if (user != null) {
            if (!user.isEmailVerified()){
                sendVerificationEmail();
            } else {
                Intent mainIntent = new Intent(EmailLoginActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }

        } else {
            showViews(username, password,
                    findViewById(R.id.buttonConnexion), findViewById(R.id.forgotPassword));
        }
    }

    //Fonction qui met à jour l'interface
    private void emailVerification() {
        hiddenViews(name_text_input, username, name_password_input,
                password, findViewById(R.id.buttonConnexion)
        , findViewById(R.id.forgotPassword), findViewById(R.id.logos));
        showViews(name_layout, prenom_layout, phone_layout,
                nom, prenom, telephone, findViewById(R.id.subscription));
    }


    private void sendVerificationEmail() {

        final FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification().addOnCompleteListener(EmailLoginActivity.this,
            (task -> {
                if (task.isSuccessful()){
                    String uuid = user.getUid();
                    writeUser(uuid, Objects.requireNonNull(nom.getText()).toString(),
                            Objects.requireNonNull(prenom.getText()).toString(), user.getEmail(),
                            Objects.requireNonNull(telephone.getText()).toString());
                    hiddenViews(name_layout, prenom_layout, phone_layout,
                            nom, prenom, telephone, findViewById(R.id.subscription));
                    showViews(findViewById(R.id.confirmationDuMail));
                    //finish();
                } else {
                    Toast.makeText(EmailLoginActivity.this,
                            "Nous n'avons pas pu vous envoyez un mail de vérification, réessayez",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmailLoginActivity.this, LoginActivity.class));
                }
            }));
    }


    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    /*
        Fonction qui valide le formulaire que ca soit pour l'inscription
        ou la connexion.
     */
    private boolean validateFormSignIn(){
        boolean valid = true;

        String userEmail = Objects.requireNonNull(username.getText()).toString();
        if (TextUtils.isEmpty(userEmail)){
            username.setError("Requis");
            valid = false;
        } else {
            username.setError(null);
        }

        String userPassword = Objects.requireNonNull(password.getText()).toString();
        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Requis");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private boolean validateFormSignUp(){
        boolean valid = true;

        String userName = Objects.requireNonNull(nom.getText()).toString();
        if (TextUtils.isEmpty(userName)){
            nom.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            nom.setError(null);
        }

        String userFirstName = Objects.requireNonNull(prenom.getText()).toString();
        if (TextUtils.isEmpty(userFirstName)) {
            prenom.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            prenom.setError(null);
        }

        String userTelephone = Objects.requireNonNull(telephone.getText()).toString();
        if (TextUtils.isEmpty(userTelephone)) {
            telephone.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            telephone.setError(null);
        }

        return valid;
    }

    private void hiddenViews(View... views){
        for (View v : views){
            v.setVisibility(View.GONE);
        }
    }

    private void showViews(View... views){
        for (View v : views){
            v.setVisibility(View.VISIBLE);
        }
    }

}
