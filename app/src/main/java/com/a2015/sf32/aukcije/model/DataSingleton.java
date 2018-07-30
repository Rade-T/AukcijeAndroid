package com.a2015.sf32.aukcije.model;

import java.util.ArrayList;

public class DataSingleton {
    private static DataSingleton instance = null;
    private User user;

    private DataSingleton() {

    }

    public static DataSingleton getInstance() {
        if (instance == null) {
            instance = new DataSingleton();
        }
        return instance;
    }

    public static void setInstance(DataSingleton instance) {
        DataSingleton.instance = instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
