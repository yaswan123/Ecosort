package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView activistLogin; // Activist text
    private DatabaseReference usersRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.username);
        passwordField = findViewById(R.id.userpassword);
        loginButton = findViewById(R.id.userlogin);
        activistLogin = findViewById(R.id.popup); // Get Activist TextView

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Auto-login if already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, Home.class)); // Redirect to Home page
            finish();
        }

        loginButton.setOnClickListener(v -> checkUserLogin());

        // Set click listener for activist login
        activistLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ActivistLoginActivity.class);
            startActivity(intent);
        });
    }

    private void checkUserLogin() {
        String emailInput = emailField.getText().toString().trim();
        String passwordInput = passwordField.getText().toString().trim();

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "Enter email and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailKey = emailInput.replace(".", "_").replace("#", "_").replace("$", "_")
                .replace("[", "_").replace("]", "_");

        usersRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users user = snapshot.getValue(Users.class);
                    if (user != null && user.getPassword().equals(passwordInput)) {
                        // Save login session
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();

                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, Home.class)); // Redirect to Home page
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
