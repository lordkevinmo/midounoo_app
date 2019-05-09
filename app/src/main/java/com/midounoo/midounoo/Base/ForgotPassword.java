package com.midounoo.midounoo.Base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.midounoo.midounoo.R;

public class ForgotPassword extends AppCompatActivity {

    private static final String st = "Reinitialisation de mot de passe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

       final EditText email = findViewById(R.id.ForgotEmail);
        MaterialButton btn = findViewById(R.id.resetPassword);

        Toolbar toolbar = findViewById(R.id.passwordToolbar);
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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email.getText())){
                    auth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(findViewById(R.id.passwordActivity),
                                                "Email envoyé avec succès", Snackbar.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(findViewById(R.id.passwordActivity),
                                            e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}
