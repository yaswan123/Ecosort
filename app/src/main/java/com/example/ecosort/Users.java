package com.example.ecosort;

public class Users {
    private String username;
    private String email;
    private String role;
    private String qualification;
    private String password; // New field

    // Empty constructor for Firebase
    public Users() {
    }

    // Constructor with password
    public Users(String username, String email, String role, String qualification, String password) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.qualification = qualification;
        this.password = password;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getQualification() {
        return qualification;
    }

    public String getPassword() {
        return password;
    }
}
