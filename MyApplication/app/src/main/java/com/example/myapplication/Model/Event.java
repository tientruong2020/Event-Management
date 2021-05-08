package com.example.myapplication.Model;

public class Event {
    //Variable name must similar to Database
    private String eventDescription;
    private String eventImageUrl;
    private String eventID;
    private String eventPublisher;

    public Event() {
    }

    public Event(String eventDescription, String eventImageUrl, String eventID, String eventPublisher) {
        this.eventDescription = eventDescription;
        this.eventImageUrl = eventImageUrl;
        this.eventID = eventID;
        this.eventPublisher = eventPublisher;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(String eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

