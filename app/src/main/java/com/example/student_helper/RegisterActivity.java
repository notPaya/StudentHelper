package com.example.student_helper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        etName            = findViewById(R.id.etRegisterName);
        etEmail           = findViewById(R.id.etRegisterEmail);
        etPassword        = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        btnRegister       = findViewById(R.id.btnRegister);
        progressBar       = findViewById(R.id.progressRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (name.isEmpty()) {
            Toast.makeText(this, "Unesite ime i prezime!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Unesite ispravan email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Lozinka mora imati minimum 6 karaktera!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Lozinke se ne poklapaju!", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        String uid = com.example.student_helper.utils.UserUtils.getUid();
                        SharedPreferences profilePrefs = getSharedPreferences("profile_" + uid, MODE_PRIVATE);
                        profilePrefs.edit().putString("profile_name", name).apply();

                        Toast.makeText(this, "Nalog kreiran! 🎉", Toast.LENGTH_SHORT).show();
                        goToApp();
                    } else {
                        Toast.makeText(this, "Greška: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Pokušajte ponovo"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToApp() {
        String uid = com.example.student_helper.utils.UserUtils.getUid();
        SharedPreferences prefs = getSharedPreferences("profile_" + uid, MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("first_launch", true);

        Intent intent;
        if (isFirstLaunch) {
            intent = new Intent(this, OnboardingActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }
}