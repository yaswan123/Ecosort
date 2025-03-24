package com.example.ecosort;

public class NoteEntity {
    private String noteId;
    private String noteContent;
    private String username;

    // Empty constructor for Firebase
    public NoteEntity() {}

    public NoteEntity(String noteId, String noteContent, String username) {
        this.noteId = noteId;
        this.noteContent = noteContent;
        this.username = username;
    }

    // Getters
    public String getNoteId() {
        return noteId;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public String getUsername() {
        return username;
    }

    // Setters (if needed)
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
