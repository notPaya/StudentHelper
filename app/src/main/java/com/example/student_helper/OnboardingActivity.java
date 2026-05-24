package com.example.student_helper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        TextInputEditText etName = findViewById(R.id.etOnboardingName);
        MaterialButton btnStart  = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {
            String name = etName.getText() != null
                    ? etName.getText().toString().trim() : "";

            SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            if (!name.isEmpty()) {
                editor.putString("profile_name", name);
            }
            // Označi da nije više prvi put
            editor.putBoolean("first_launch", false);
            editor.apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}