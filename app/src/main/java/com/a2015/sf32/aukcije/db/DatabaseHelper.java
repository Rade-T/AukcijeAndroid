package com.a2015.sf32.aukcije.db;

import android.content.Context;
import java.sql.SQLException;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a2015.sf32.aukcije.model.Auction;
import com.a2015.sf32.aukcije.model.Bid;
import com.a2015.sf32.aukcije.model.Item;
import com.a2015.sf32.aukcije.model.User;
import com.a2015.sf32.aukcije.model.UserAuction;
import com.a2015.sf32.aukcije.model.UserNotification;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "aukcije.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    private Dao<Auction, Integer> auctionsDAO = null;
    private Dao<Bid, Integer> bidsDAO = null;
    private Dao<Item, Integer> itemsDAO = null;
    private Dao<User, Integer> usersDAO = null;
    private Dao<UserAuction, Integer> userAuctionsDAO = null;
    private Dao<UserNotification, Integer> userNotificationsDAO = null;

    private RuntimeExceptionDao<Auction, Integer> auctionsRuntimeDAO = null;
    private RuntimeExceptionDao<Bid, Integer> bidsRuntimeDAO = null;
    private RuntimeExceptionDao<Item, Integer> itemsRuntimeDAO = null;
    private RuntimeExceptionDao<User, Integer> usersRuntimeDAO = null;
    private RuntimeExceptionDao<UserAuction, Integer> userAuctionsRuntimeDAO = null;
    private RuntimeExceptionDao<UserNotification, Integer> userNotificationsRuntimeDAO = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Auction.class);
            TableUtils.createTable(connectionSource, Bid.class);
            TableUtils.createTable(connectionSource, Item.class);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, UserAuction.class);
            TableUtils.createTable(connectionSource, UserNotification.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Auction, Integer> getAuctionDAO() throws SQLException {
        if (auctionsDAO == null) {
            auctionsDAO = getDao(Auction.class);
        }
        return auctionsDAO;
    }

    public Dao<Bid, Integer> getBidDAO() throws SQLException {
        if (bidsDAO == null) {
            bidsDAO = getDao(Bid.class);
        }
        return bidsDAO;
    }

    public Dao<Item, Integer> getItemDAO() throws SQLException {
        if (itemsDAO == null) {
            itemsDAO = getDao(Item.class);
        }
        return itemsDAO;
    }

    public Dao<User, Integer> getDao() throws SQLException {
        if (usersDAO == null) {
            usersDAO = getDao(User.class);
        }
        return usersDAO;
    }

    public RuntimeExceptionDao<Auction, Integer> getAuctionsRuntimeDAO() {
        if (auctionsRuntimeDAO == null) {
            auctionsRuntimeDAO = getRuntimeExceptionDao(Auction.class);
        }
        return auctionsRuntimeDAO;
    }

    public RuntimeExceptionDao<Bid, Integer> getBidsRuntimeDAO() {
        if (bidsRuntimeDAO == null) {
            bidsRuntimeDAO = getRuntimeExceptionDao(Bid.class);
        }
        return bidsRuntimeDAO;
    }

    public RuntimeExceptionDao<User, Integer> getUsersRuntimeDAO() {
        if (usersRuntimeDAO == null) {
            usersRuntimeDAO = getRuntimeExceptionDao(User.class);
        }
        return usersRuntimeDAO;
    }

    public RuntimeExceptionDao<Item, Integer> getItemsRuntimeDAO() {
        if (itemsRuntimeDAO == null) {
            itemsRuntimeDAO = getRuntimeExceptionDao(Item.class);
        }
        return itemsRuntimeDAO;
    }

    public RuntimeExceptionDao<UserAuction, Integer> getUserAuctionsRuntimeDAO() {
        if (userAuctionsRuntimeDAO == null) {
            userAuctionsRuntimeDAO = getRuntimeExceptionDao(UserAuction.class);
        }
        return userAuctionsRuntimeDAO;
    }

    public RuntimeExceptionDao<UserNotification, Integer> getUserNotificationsRuntimeDAO() {
        if (userNotificationsRuntimeDAO == null) {
            userNotificationsRuntimeDAO = getRuntimeExceptionDao(UserNotification.class);
        }
        return userNotificationsRuntimeDAO;
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Auction.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        super.close();
    }
}
