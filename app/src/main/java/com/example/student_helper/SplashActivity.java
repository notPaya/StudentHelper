package com.example.student_helper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.student_helper.utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = settings.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String uid = UserUtils.getUid();
                SharedPreferences prefs = getSharedPreferences("profile_" + uid, MODE_PRIVATE);
                boolean isFirstLaunch = prefs.getBoolean("first_launch", true);

                Intent intent;
                if (isFirstLaunch) {
                    intent = new Intent(this, OnboardingActivity.class);
                } else {
                    intent = new Intent(this, MainActivity.class);
                }
                startActivity(intent);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}