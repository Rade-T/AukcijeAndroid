package com.a2015.sf32.aukcije.activities;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.db.DatabaseHelper;
import com.a2015.sf32.aukcije.model.Auction;
import com.a2015.sf32.aukcije.model.Bid;
import com.a2015.sf32.aukcije.model.DataSingleton;
import com.a2015.sf32.aukcije.model.Item;
import com.a2015.sf32.aukcije.model.User;
import com.a2015.sf32.aukcije.model.UserAuction;
import com.a2015.sf32.aukcije.model.UserNotification;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LogInActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<User, Integer> usersDAO;
    private RuntimeExceptionDao<Auction, Integer> auctionsDAO;
    private RuntimeExceptionDao<Item, Integer> itemsDAO;
    private RuntimeExceptionDao<UserAuction, Integer> userAuctionsDAO;
    private RuntimeExceptionDao<UserNotification, Integer> userNotificationsDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usersDAO = getHelper().getUsersRuntimeDAO();
        auctionsDAO = getHelper().getAuctionsRuntimeDAO();
        itemsDAO = getHelper().getItemsRuntimeDAO();
        userAuctionsDAO = getHelper().getUserAuctionsRuntimeDAO();
        userNotificationsDAO = getHelper().getUserNotificationsRuntimeDAO();

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new LoginOnClickListener());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("Preferences test", preferences.getString("splash_duration_pref", ""));
    }

    private class LoginOnClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {

            EditText txt_username = (EditText) findViewById(R.id.text_username);
            EditText txt_password = (EditText) findViewById(R.id.text_password);

            String username = txt_username.getText().toString();
            String password = txt_password.getText().toString();

            if (username.equals("")) {
                Toast.makeText(v.getContext(), "Korisnicko ime ne sme biti prazno", Toast.LENGTH_SHORT).show();
            }

            if (password.equals("")) {
                Toast.makeText(v.getContext(), "Lozinka ne sme biti prazna", Toast.LENGTH_SHORT).show();
            }

            if (usersDAO.queryForAll().size() < 1) {
                mockEntities();
            }

            if (!username.equals("") && !password.equals("")) {
                QueryBuilder<User, Integer> qb = usersDAO.queryBuilder();
                List<User> users;

                try {
                    qb.where().eq("email", username).and().eq("password", password);
                    users = usersDAO.query(qb.prepare());
                    if (users.size() > 0) {
                        User user = users.get(0);
                        logInUser(user);
                    } else {
                        Toast.makeText(v.getContext(), "Pogresan username ili lozinka", Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void logInUser(User user) {
        SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        sp.putString("name_pref", user.getName());
        sp.putString("email_pref", user.getEmail());
        sp.putString("address_pref", user.getAddress());
        sp.putString("phone_pref", user.getPhone());
        sp.putString("password_pref", user.getPassword());
        sp.commit();

        DataSingleton.getInstance().setUser(user);
        new AuctionChecker(this).start();
        displayUserNotifications(user);

        startActivity(new Intent(LogInActivity.this, ItemsActivity.class));
        finish();
    }

    private void checkAuctions() {
        itemsDAO = getHelper().getItemsRuntimeDAO();
        auctionsDAO = getHelper().getAuctionsRuntimeDAO();
        userAuctionsDAO = getHelper().getUserAuctionsRuntimeDAO();
        usersDAO = getHelper().getUsersRuntimeDAO();
        Date current = new Date();
        List<Item> unsoldItems = itemsDAO.queryForEq("sold", false);
        for (Item item :
                unsoldItems) {
            if (item.getCurrentAuction() != null) {
                Auction currentAuction = item.getCurrentAuction();
                auctionsDAO.refresh(currentAuction);
                if (!currentAuction.isFinished() && currentAuction.getEndDate().before(current)) {
                    currentAuction.setFinished(true);
                    item.setSold(true);
                    item.setCurrentAuction(null);
                    itemsDAO.update(item);
                    auctionsDAO.update(currentAuction);

                    List<UserAuction> auctionUsers = userAuctionsDAO.queryForEq("auction_id", currentAuction.getId());
                    for (UserAuction userAuction:
                            auctionUsers) {
                        User auctionUser = userAuction.getUser();
                        usersDAO.refresh(auctionUser);
                        if (auctionUser.getId() == currentAuction.getTopBid().getId()) {
                            sendNotification(auctionUser,
                                    "Aukcija " + currentAuction.getId(),
                                    "Pobedili ste u aukciji " + currentAuction.getId(),
                                    currentAuction);
                        } else {
                            sendNotification(auctionUser,
                                    "Aukcija " + currentAuction.getId(),
                                    "Aukcija " + currentAuction.getId() + " je zavrsena.",
                                    null);
                        }
                    }
                }
            }
        }
    }

    private class AuctionChecker extends Thread {
        private DatabaseHelper databaseHelper;
        private RuntimeExceptionDao<Item, Integer> itemsDAO;
        private RuntimeExceptionDao<Auction, Integer> auctionsDAO;
        private RuntimeExceptionDao<UserAuction, Integer> userAuctionsDAO;
        private RuntimeExceptionDao<User, Integer> usersDAO;
        private Context mContext;

        public AuctionChecker(Context context) {
            this.mContext = context;
        }

        @Override
        public void run() {
            try {
                this.itemsDAO = this.getHelper().getItemsRuntimeDAO();
                this.auctionsDAO = this.getHelper().getAuctionsRuntimeDAO();
                this.userAuctionsDAO = this.getHelper().getUserAuctionsRuntimeDAO();
                this.usersDAO = this.getHelper().getUsersRuntimeDAO();
                Date current = new Date();
                List<Item> unsoldItems = this.itemsDAO.queryForEq("sold", false);
                for (Item item :
                        unsoldItems) {
                    if (item.getCurrentAuction() != null) {
                        Auction currentAuction = item.getCurrentAuction();
                        this.auctionsDAO.refresh(currentAuction);
                        if (!currentAuction.isFinished() && currentAuction.getEndDate().before(current)) {
                            currentAuction.setFinished(true);
                            item.setSold(true);
                            item.setCurrentAuction(null);
                            this.itemsDAO.update(item);
                            this.auctionsDAO.update(currentAuction);

                            List<UserAuction> auctionUsers = this.userAuctionsDAO.queryForEq("auction_id", currentAuction.getId());
                            for (UserAuction userAuction:
                                 auctionUsers) {
                                User auctionUser = userAuction.getUser();
                                this.usersDAO.refresh(auctionUser);
                                if (auctionUser.getId() == currentAuction.getTopBid().getId()) {
                                    sendNotification(auctionUser,
                                            "Aukcija " + currentAuction.getId(),
                                            "Pobedili ste u aukciji " + currentAuction.getId(),
                                            currentAuction);
                                } else {
                                    sendNotification(auctionUser,
                                            "Aukcija " + currentAuction.getId(),
                                            "Aukcija " + currentAuction.getId() + " je zavrsena.",
                                            null);
                                }
                            }
                        }
                    }
                }
                displayUserNotifications(DataSingleton.getInstance().getUser());
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                if (this.databaseHelper != null) {
                    OpenHelperManager.releaseHelper();
                    this.databaseHelper = null;
                }
                mContext = null;
            }
        }

        private DatabaseHelper getHelper() {
            if (databaseHelper == null) {
                databaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);
            }
            return databaseHelper;
        }
    }

    private void sendNotification(User recipient, String title, String text, Auction auction) {
        UserNotification notification = new UserNotification();
        notification.setUser(recipient);
        notification.setTitle(title);
        notification.setText(text);
        notification.setAuction(auction);
        userNotificationsDAO.create(notification);
    }

    @TargetApi(16)
    private void displayUserNotifications(User user) {
        List<UserNotification> userNotifications = userNotificationsDAO.queryForEq("user_id", user.getId());
        List<UserNotification> all = userNotificationsDAO.queryForAll();

        for(int i = 0;i < userNotifications.size();i++) {
            UserNotification notification = userNotifications.get(i);
            Notification.Builder nBuilder = new Notification.Builder(getBaseContext());
            nBuilder.setSmallIcon(android.R.mipmap.sym_def_app_icon);
            nBuilder.setContentTitle(notification.getTitle());
            nBuilder.setContentText(notification.getText());
            if (notification.getAuction() != null) {
                Auction auction = notification.getAuction();
                auctionsDAO.refresh(auction);
                if (!auction.isFinished()) {
                    Item auctionItem = auction.getItem();
                    itemsDAO.refresh(auctionItem);
                    Intent intent = new Intent(this, ItemActivity.class);
                    intent.putExtra("itemId", auctionItem.getId());
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    nBuilder.setContentIntent(pendingIntent);
                } else {
                    Intent intent = new Intent(this, AuctionActivity.class);
                    intent.putExtra("auction_id", auction.getId());
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    nBuilder.setContentIntent(pendingIntent);
                }
            }
            NotificationManager manager = (NotificationManager)
                    this.getSystemService(this.NOTIFICATION_SERVICE);
            manager.notify(i, nBuilder.build());
            userNotificationsDAO.delete(notification);
        }
    }

    private void mockEntities() {
        User u1 = new User();
        u1.setAddress("Adresa korisnika 1");
        u1.setEmail("k1");
        u1.setName("Korisnik 1");
        u1.setPassword("k1");
        u1.setPhone("1232");
        u1.setPicture("");
        usersDAO.create(u1);

        User u2 = new User();
        u2.setAddress("Adresa korisnika 2");
        u2.setEmail("k2");
        u2.setName("Korisnik 2");
        u2.setPassword("k2");
        u2.setPhone("12323");
        u2.setPicture("");
        usersDAO.create(u2);

        Item i1 = new Item();
        i1.setName("Predmet 1");
        i1.setDescription("Predmet na aukciji 1");
        i1.setSold(false);
        itemsDAO.create(i1);

        Item i2 = new Item();
        i2.setName("Predmet 2");
        i2.setDescription("Predmet na aukciji 2");
        i2.setSold(false);
        itemsDAO.create(i2);

        Item i3 = new Item();
        i3.setName("Predmet 3");
        i3.setDescription("Prodat predmet");
        i3.setSold(true);
        itemsDAO.create(i3);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Auction a1 = new Auction();
        a1.setUser(u1);
        a1.setItem(i1);
        a1.setStartPrice(200);
        try {
            a1.setStartDate(sdf.parse("01/01/2017"));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        try {
            a1.setEndDate(sdf.parse("01/10/2017"));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        auctionsDAO.create(a1);

        Auction a2 = new Auction();
        a2.setUser(u2);
        a2.setItem(i2);
        a2.setStartPrice(200);
        try {
            a2.setStartDate(sdf.parse("02/01/2017"));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        try {
            a2.setEndDate(sdf.parse("02/10/2017"));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        auctionsDAO.create(a2);

        Auction a3 = new Auction();
        a3.setUser(u2);
        a3.setItem(i3);
        a3.setStartPrice(500);
        try {
            a3.setStartDate(sdf.parse("02/01/2017"));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        try {
            a3.setEndDate(sdf.parse("02/03/2017"));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        a3.setTopBid(u1);
        a3.setFinished(true);
        auctionsDAO.create(a3);

        UserAuction ua3 = new UserAuction(u1, a3);
        userAuctionsDAO.create(ua3);

        i1.setCurrentAuction(a1);
        itemsDAO.update(i1);

        i2.setCurrentAuction(a2);
        itemsDAO.update(i2);

        UserNotification un1 = new UserNotification();
        un1.setUser(u1);
        un1.setTitle("Aukcija 1");
        un1.setText("Vasa ponuda vise nije najveca");
        userNotificationsDAO.create(un1);
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
