package com.a2015.sf32.aukcije.model;

import com.j256.ormlite.field.DatabaseField;

public class UserAuction {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private User user;

    @DatabaseField(foreign = true)
    private Auction auction;

    public UserAuction() {}

    public UserAuction(User user, Auction auction) {
        this.user = user;
        this.auction = auction;
    }

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

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
