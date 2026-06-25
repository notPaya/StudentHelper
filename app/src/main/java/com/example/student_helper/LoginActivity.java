package com.example.student_helper;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.student_helper.utils.UserUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        etEmail      = findViewById(R.id.etLoginEmail);
        etPassword   = findViewById(R.id.etLoginPassword);
        btnLogin     = findViewById(R.id.btnLogin);
        progressBar  = findViewById(R.id.progressLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> loginUser());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void loginUser() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Unesite ispravan email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Lozinka mora imati minimum 6 karaktera!", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        goToApp();
                    } else {
                        Toast.makeText(this, "Greška: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Pokušajte ponovo"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showForgotPasswordDialog() {
        EditText input = new EditText(this);
        input.setHint("Unesi svoj email...");
        input.setText(etEmail.getText() != null ? etEmail.getText().toString().trim() : "");
        input.setPadding(40, 30, 40, 10);

        new AlertDialog.Builder(this)
                .setTitle("🔑 Reset lozinke")
                .setMessage("Unesi svoj email i poslat ćemo ti link za resetovanje lozinke.")
                .setView(input)
                .setPositiveButton("Pošalji", (dialog, which) -> {
                    String email = input.getText().toString().trim();

                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, "Unesite ispravan email!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this,
                                            "Email za reset lozinke je poslan! Provjeri inbox. 📧",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this,
                                            "Greška: " + (task.getException() != null
                                                    ? task.getException().getMessage() : "Pokušajte ponovo"),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .setNegativeButton("Odustani", null)
                .show();
    }

    private void goToApp() {
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
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
    }
}