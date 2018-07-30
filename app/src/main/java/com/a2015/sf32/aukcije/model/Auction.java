package com.a2015.sf32.aukcije.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "auctions")
public class Auction {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private double startPrice;

    @DatabaseField
    private Date startDate;

    @DatabaseField
    private Date endDate;

    @DatabaseField
    private boolean finished;

    @DatabaseField(foreign = true)
    private User user;

    @DatabaseField(foreign = true)
    private Item item;

    @DatabaseField(foreign = true)
    private User topBid;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Bid> bids;

    public Auction() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getTopBid() {
        return topBid;
    }

    public void setTopBid(User topBid) {
        this.topBid = topBid;
    }

    public ForeignCollection<Bid> getBids() {
        return bids;
    }

    public void setBids(ForeignCollection<Bid> bids) {
        this.bids = bids;
    }

    @Override
    public String toString() {
        return "Auction{" +
                "id=" + id +
                '}';
    }
}
