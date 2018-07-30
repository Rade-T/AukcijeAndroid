package com.a2015.sf32.aukcije.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "items")
public class Item {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String description;

    @DatabaseField
    public String picture;

    @DatabaseField
    public boolean sold;

    @DatabaseField(foreign = true)
    public Auction currentAuction;

    @ForeignCollectionField(eager = true)
    public ForeignCollection<Auction> auctions;

    public Item() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public ForeignCollection<Auction> getAuctions() {
        return auctions;
    }

    public void setAuctions(ForeignCollection<Auction> auctions) {
        this.auctions = auctions;
    }

    public Auction getCurrentAuction() {
        return currentAuction;
    }

    public void setCurrentAuction(Auction currentAuction) {
        this.currentAuction = currentAuction;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                '}';
    }
}
