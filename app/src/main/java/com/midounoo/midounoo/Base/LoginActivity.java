package com.midounoo.midounoo.Base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.midounoo.midounoo.R;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        findViewById(R.id.phonecheerz).setOnClickListener((v ->
            startActivity(new Intent(LoginActivity.this, PhoneLogin.class))
        ));

        findViewById(R.id.gmailcheerz).setOnClickListener(
            (v -> startActivity(new Intent(LoginActivity.this, GoogleLogin.class))
        ));

        findViewById(R.id.fbcheerz).setOnClickListener(
            v -> findViewById(R.id.fb).performClick()
        );
    }

    public void EmailLoginAction(View view) {
        sendToEmailActivity();
    }

    public void AccountCreateAction(View view) {
        sendToEmailActivity();
    }

    public void sendToEmailActivity() {
        startActivity(new Intent(LoginActivity.this, EmailLoginActivity.class));
    }
}