package com.example.myapplication.Model;

public class User {
    //Variable name must similar to Database
    private String userFullName;
    private String userEmail;
    private String userBio;
    private String userImageUrl;
    private String userID;

    public User() {
    }

    public User(String userFullName, String userEmail, String userBio, String userImageUrl, String userID) {
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userBio = userBio;
        this.userImageUrl = userImageUrl;
        this.userID = userID;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
