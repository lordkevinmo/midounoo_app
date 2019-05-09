package com.midounoo.midounoo.Base;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.midounoo.midounoo.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity {

    private static final String TAG = PhoneLogin.class.getSimpleName();

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // Firebase Auth variable
    private FirebaseAuth mAuth;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    TextInputLayout numeroView, verificationView;
    TextInputEditText numero, confirmation;
    MaterialButton inscription, confirmationBtn, resendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        //Forcer l'affichage en mode portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Desactiver le clavier à l'affichage de la page
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        numero = findViewById(R.id.phone_input);
        confirmation = findViewById(R.id.phone_verification_input);
        inscription = findViewById(R.id.send_phone_confirmation);
        confirmationBtn = findViewById(R.id.phone_confirmation);
        resendBtn = findViewById(R.id.resend_Token_button);
        numeroView = findViewById(R.id.phone_login_layout);
        verificationView = findViewById(R.id.phone_verification_layout);

        //Management of the toolbar
        Toolbar toolbar = findViewById(R.id.phone_login_toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null){
            bar.setTitle(null);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener((v -> finish()));
        //[End of management of toolbar]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("fr");
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, phoneAuthCredential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    numero.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(R.id.pLogin_layout), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            /**
             *
             * @param s verification ID
             * @param forceResendingToken token
             */
            @Override
            public void onCodeSent(String s, PhoneAuthProvider
                    .ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + s);

                // Save verification ID and resending token so we can use them later
                mVerificationId = s;
                mResendToken = forceResendingToken;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };

        inscription.setOnClickListener((v -> {
            if (!validatePhoneNumber()) {
                return;
            }

            startPhoneNumberVerification("+228" + Objects.requireNonNull(
                    numero.getText()).toString());
        }));

        confirmationBtn.setOnClickListener((v -> {

            if (TextUtils.isEmpty(confirmation.getText())) {
                confirmation.setError("Ne peut être vide");
                return;
            }

            verifyPhoneNumberWithCode(mVerificationId, Objects.requireNonNull(confirmation.getText()).toString());
        }));

        resendBtn.setOnClickListener((v ->
            resendVerificationCode(Objects.requireNonNull(numero.getText()).toString(), mResendToken)
        ));

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(Objects.requireNonNull(numero.getText()).toString());
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this,
                (task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                        // [START_EXCLUDE]
                        updateUI(STATE_SIGNIN_SUCCESS, user);
                        // [END_EXCLUDE]
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            // [START_EXCLUDE silent]
                            confirmation.setError("Code invalide.");
                            // [END_EXCLUDE]
                        }
                        // [START_EXCLUDE silent]
                        // Update UI
                        updateUI(STATE_SIGNIN_FAILED);
                        // [END_EXCLUDE]
                    }
            })
        );
    }
    // [END sign_in_with_phone]

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(numeroView, inscription);
                disableViews(verificationView, confirmationBtn, resendBtn);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(numeroView, numero, verificationView, confirmationBtn, resendBtn);
                disableViews(inscription);
                Snackbar.make(findViewById(R.id.pLogin_layout)
                        , "Code envoyé", Snackbar.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(numeroView, inscription, verificationView,
                        confirmationBtn, resendBtn);
                Snackbar.make(findViewById(R.id.pLogin_layout)
                        , "La vérification a échouée",
                        Snackbar.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
               disableViews(verificationView, numeroView, inscription,
                       confirmationBtn, resendBtn);
                Snackbar.make(findViewById(R.id.pLogin_layout)
                        , "Statut vérifié avec succès",
                        Snackbar.LENGTH_SHORT).show();

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        confirmation.setText(cred.getSmsCode());

                    } else {
                        confirmation.setText(R.string.instant_validation);
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                Snackbar.make(findViewById(R.id.pLogin_layout)
                        , "Inscription échouée",
                        Snackbar.LENGTH_SHORT).show();
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                Intent intent = new Intent(PhoneLogin.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }

        if (user == null) {
            // Signed out
            numero.setVisibility(View.VISIBLE);
            inscription.setVisibility(View.VISIBLE);
            confirmation.setVisibility(View.GONE);
            confirmationBtn.setVisibility(View.GONE);
            resendBtn.setVisibility(View.GONE);
        } else {
            Snackbar.make(findViewById(R.id.pLogin_layout)
                    , "Inscrit",
                    Snackbar.LENGTH_INDEFINITE).show();
            startActivity(new Intent(PhoneLogin.this, MainActivity.class));
        }
    }

    private boolean validatePhoneNumber() {
        if (TextUtils.isEmpty(numero.getText())) {
            numero.setError("Numéro de téléphone invalide");
            return false;
        }

        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

}
