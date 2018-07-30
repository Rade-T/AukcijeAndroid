package com.a2015.sf32.aukcije.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField(unique = true)
    private String email;

    @DatabaseField
    private String password;

    @DatabaseField
    private String picture;

    @DatabaseField
    private String address;

    @DatabaseField
    private String phone;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Auction> auctions;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Bid> bids;

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ForeignCollection<Auction> getAuctions() {
        return auctions;
    }

    public void setAuctions(ForeignCollection<Auction> auctions) {
        this.auctions = auctions;
    }

    public ForeignCollection<Bid> getBids() {
        return bids;
    }

    public void setBids(ForeignCollection<Bid> bids) {
        this.bids = bids;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }
}
