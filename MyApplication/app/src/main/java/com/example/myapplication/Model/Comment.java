package com.example.myapplication.Model;

public class Comment {
    private String Content,UID;
    private long createdAt;

    public Comment(){
    }

    public Comment(String Content, String UID, long createdAt) {
        this.Content = Content;
        this.UID = UID;
        this.createdAt = createdAt;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
