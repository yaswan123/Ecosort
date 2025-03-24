package com.example.ecosort;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, username, password, role, qualification;
    private Button registerButton;
    private DatabaseReference activistsRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.registeremail);
        username = findViewById(R.id.registerusername);
        password = findViewById(R.id.registerpassword);
        role = findViewById(R.id.registerrole);
        qualification = findViewById(R.id.registerqualification);
        registerButton = findViewById(R.id.registerbutton);

        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        activistsRef = fdb.getReference("activists");
        usersRef = fdb.getReference("users");

        registerButton.setOnClickListener(v -> insertUser());
    }

    private void insertUser() {
        String emailInput = email.getText().toString().trim();
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String roleInput = role.getText().toString().trim();
        String qualificationInput = qualification.getText().toString().trim();

        if (emailInput.isEmpty() || usernameInput.isEmpty() || passwordInput.isEmpty() ||
                roleInput.isEmpty() || qualificationInput.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            Toast.makeText(RegisterActivity.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert email to valid Firebase key format
        String emailKey = emailInput.replace(".", "_").replace("#", "_")
                .replace("$", "_").replace("[", "_").replace("]", "_");

        // Choose target branch based on role
        DatabaseReference targetRef;
        boolean isActivist = roleInput.equalsIgnoreCase("activist");
        targetRef = isActivist ? activistsRef : usersRef;

        // Check if user already exists
        targetRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(RegisterActivity.this, "User already registered!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isActivist) {
                        Activist activist = new Activist(usernameInput, emailInput, roleInput, qualificationInput, passwordInput);
                        targetRef.child(emailKey).setValue(activist)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(RegisterActivity.this, "Activist Registered Successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Users user = new Users(usernameInput, emailInput, roleInput, qualificationInput, passwordInput);
                        targetRef.child(emailKey).setValue(user)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(RegisterActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
