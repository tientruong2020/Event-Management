package com.example.myapplication.Model;

public class User {
    private String FullName;
    private String Email;
    private String bio;
    private String imageUrl;
    private String ID;

    public User() {
    }

    public User(String fullName, String email, String bio, String imageUrl, String ID) {
        FullName = fullName;
        Email = email;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.ID = ID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
