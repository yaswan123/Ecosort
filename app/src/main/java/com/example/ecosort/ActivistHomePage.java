package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ActivistHomePage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private FirebaseFirestore db;
    private List<NoteEntity> noteList;
    private ImageButton addNotesButton;
    private Button logoutButton; // Logout button
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activist_home_page);

        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Ensure user is logged in
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Log.e("ActivistHomePage", "User not logged in. Redirecting to login.");
            startActivity(new Intent(this, ActivistLoginActivity.class));
            finish();
            return;
        }

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_view);
        addNotesButton = findViewById(R.id.addnotes);
        logoutButton = findViewById(R.id.logoutButton); // Ensure this exists in XML

        if (logoutButton == null) {
            Log.e("ActivistHomePage", "Logout button is NULL! Check XML layout.");
        }

        db = FirebaseFirestore.getInstance();
        noteList = new ArrayList<>();
        adapter = new NotesAdapter(noteList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotes();

        addNotesButton.setOnClickListener(v -> startActivity(new Intent(this, AddNotes.class)));
        logoutButton.setOnClickListener(v -> logoutUser()); // Logout functionality
    }

    private void loadNotes() {
        db.collection("notes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(ActivistHomePage.this, "Error loading notes!", Toast.LENGTH_SHORT).show();
                    return;
                }
                noteList.clear();
                if (value != null) {
                    for (var doc : value.getDocuments()) {
                        NoteEntity note = doc.toObject(NoteEntity.class);
                        noteList.add(note);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void logoutUser() {
        // Clear session
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();

        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ActivistLoginActivity.class));
        finish();
    }
}
