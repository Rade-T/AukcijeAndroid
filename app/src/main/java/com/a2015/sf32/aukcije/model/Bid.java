package com.a2015.sf32.aukcije.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "bids")
public class Bid {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private double price;

    @DatabaseField
    private Date dateTime;

    @DatabaseField(foreign = true)
    private Auction auction;

    @DatabaseField(foreign = true)
    private User user;

    public Bid() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "id=" + id +
                '}';
    }
}
