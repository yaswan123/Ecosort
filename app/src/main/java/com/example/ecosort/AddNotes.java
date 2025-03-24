package com.example.ecosort;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddNotes extends AppCompatActivity {
    private EditText notesInput;
    private Button saveButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        notesInput = findViewById(R.id.notes);
        saveButton = findViewById(R.id.addnote); // Initialize button
        db = FirebaseFirestore.getInstance();

        // Set click listener for save button
        saveButton.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String userNote = notesInput.getText().toString().trim();
        if (userNote.isEmpty()) {
            Toast.makeText(this, "Please enter a note!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Unknown User");

        String noteId = db.collection("notes").document().getId();
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("noteId", noteId);
        noteData.put("noteContent", userNote);
        noteData.put("username", username); // Store username

        db.collection("notes").document(noteId).set(noteData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Note added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after adding note
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add note!", Toast.LENGTH_SHORT).show());
    }
}
