package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivistLoginActivity extends AppCompatActivity {
    private static final String TAG = "ActivistLoginActivity";
    private EditText emailField, passwordField;
    private Button loginButton;
    private DatabaseReference activistsRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activist_login_page);

        emailField = findViewById(R.id.username);
        passwordField = findViewById(R.id.userpassword);
        loginButton = findViewById(R.id.loginbuttton);

        activistsRef = FirebaseDatabase.getInstance().getReference("activists");
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Auto-login if already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, ActivistHomePage.class));
            finish();
        }

        loginButton.setOnClickListener(v -> checkActivistLogin());
    }

    private void checkActivistLogin() {
        String emailInput = emailField.getText().toString().trim();
        String passwordInput = passwordField.getText().toString().trim();

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "Enter email and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailKey = emailInput.replace(".", "_").replace("#", "_").replace("$", "_")
                .replace("[", "_").replace("]", "_");

        activistsRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Activist activist = snapshot.getValue(Activist.class);
                    if (activist != null && activist.getPassword().equals(passwordInput)) {
                        // Save login session
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();

                        Toast.makeText(ActivistLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ActivistLoginActivity.this, ActivistHomePage.class));
                        finish();
                    } else {
                        Toast.makeText(ActivistLoginActivity.this, "Invalid password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivistLoginActivity.this, "Activist not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivistLoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
