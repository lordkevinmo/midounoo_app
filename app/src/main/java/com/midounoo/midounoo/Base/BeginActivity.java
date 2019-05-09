package com.midounoo.midounoo.Base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import com.midounoo.midounoo.R;

import androidx.appcompat.app.AppCompatActivity;

public class BeginActivity extends AppCompatActivity {

    //private ImageView logoBienvenu;
    private static final int TIME_OUT = 7300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //logoBienvenu = findViewById(R.id.logoBienvenu);
        new Handler().postDelayed(
            () -> {
                Intent loginIntent = new Intent(
                        BeginActivity.this, LoginActivity.class);
                loginIntent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
            , TIME_OUT
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //logoBienvenu.animate().alpha(0f).setDuration(5500);
        findViewById(R.id.begin_layout).animate().alpha(0f).setDuration(7500);
    }

}
