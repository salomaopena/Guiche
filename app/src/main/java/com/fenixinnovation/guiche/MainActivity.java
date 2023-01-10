package com.fenixinnovation.guiche;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        int SPLASH_DISPLAY_LENGTH = 3000;
        new Handler().postDelayed(() -> {
            Intent homeIntent = WebActivity.getStartIntent(MainActivity.this);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            MainActivity.this.startActivity(homeIntent);
            MainActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DISPLAY_LENGTH);
    }
}