package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class Home extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private FirebaseFirestore db;
    private List<NoteEntity> noteList;
    private Button logoutButton; // Logout button
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Ensure user is logged in
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Log.e("Home", "User not logged in. Redirecting to login.");
            startActivity(new Intent(this, LoginActivity.class)); // Ensure this activity exists
            finish();
            return;
        }

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_view);
        logoutButton = findViewById(R.id.logoutButton);

        if (logoutButton == null) {
            Log.e("Home", "Logout button is NULL! Check XML layout.");
        }

        db = FirebaseFirestore.getInstance();
        noteList = new ArrayList<>();
        adapter = new NotesAdapter(noteList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotes();

        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void loadNotes() {
        db.collection("notes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(Home.this, "Error loading notes!", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(this, LoginActivity.class)); // Ensure this activity exists
        finish();
    }
}
