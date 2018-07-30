package com.a2015.sf32.aukcije.model;

import com.j256.ormlite.field.DatabaseField;

public class UserNotification {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private User user;

    @DatabaseField
    private String title;

    @DatabaseField
    private String text;

    @DatabaseField(foreign = true)
    private Auction auction;

    public UserNotification() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
