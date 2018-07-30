package com.a2015.sf32.aukcije.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.activities.ItemsActivity;
import com.a2015.sf32.aukcije.adapters.BidAdapter;
import com.a2015.sf32.aukcije.db.DatabaseHelper;
import com.a2015.sf32.aukcije.model.Auction;
import com.a2015.sf32.aukcije.model.Bid;
import com.a2015.sf32.aukcije.model.DataSingleton;
import com.a2015.sf32.aukcije.model.Item;
import com.a2015.sf32.aukcije.model.User;
import com.a2015.sf32.aukcije.model.UserAuction;
import com.a2015.sf32.aukcije.model.UserNotification;
import com.a2015.sf32.aukcije.notifications.NotificationService;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.query.In;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuctionFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Item, Integer> itemsDAO;
    private RuntimeExceptionDao<Auction, Integer> auctionsDAO;
    private RuntimeExceptionDao<Bid, Integer> bidsDAO;
    private RuntimeExceptionDao<UserAuction, Integer> userAuctionsDAO;
    private RuntimeExceptionDao<User, Integer> usersDAO;
    private RuntimeExceptionDao<UserNotification, Integer> userNotificationsDAO;

    private List<Bid> bids;
    private BidAdapter bidAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auction, container, false);
        itemsDAO = getHelper().getItemsRuntimeDAO();
        auctionsDAO = getHelper().getAuctionsRuntimeDAO();
        bidsDAO = getHelper().getBidsRuntimeDAO();
        userAuctionsDAO = getHelper().getUserAuctionsRuntimeDAO();
        usersDAO = getHelper().getUsersRuntimeDAO();
        userNotificationsDAO = getHelper().getUserNotificationsRuntimeDAO();

        Button btnNewBid = (Button) view.findViewById(R.id.btnNewBid);
        int itemId = getArguments().getInt("itemId");

        Item item = itemsDAO.queryForId(itemId);
        Auction auction = item.getCurrentAuction();
        auctionsDAO.refresh(auction);
        ListView bidsList = (ListView) view.findViewById(R.id.bidsView);
        try {
            bids = new ArrayList<>(auction.getBids());
            bidAdapter = new BidAdapter(inflater.getContext(), bids);
            bidsList.setAdapter(bidAdapter);
            btnNewBid.setOnClickListener(new BidButtonClickListener(auction));
        } catch (NullPointerException ex) {
            Intent intent = new Intent(getContext(), ItemsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return view;
    }

    private class BidButtonClickListener implements View.OnClickListener {
        Auction auction;

        public BidButtonClickListener(Auction auction) {
            this.auction = auction;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle("Nova Ponuda");
            final EditText input = new EditText(v.getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            dialog.setView(input);

            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    User user = DataSingleton.getInstance().getUser();
                    double newPrice = Double.valueOf(input.getText().toString());

                    if (newPrice <= auction.getStartPrice()) {
                        Toast.makeText(getContext(), "Ponuda mora biti veca od pocetne", Toast.LENGTH_SHORT).show();
                    } else {
                        Bid newBid = new Bid();
                        newBid.setAuction(auction);
                        newBid.setDateTime(new Date());
                        newBid.setPrice(newPrice);
                        newBid.setUser(user);
                        RuntimeExceptionDao<Bid, Integer> bidRuntimeDAO = getHelper().getBidsRuntimeDAO();
                        QueryBuilder<Bid, Integer> qb = bidRuntimeDAO.queryBuilder();
                        RuntimeExceptionDao<UserAuction, Integer> userAuctionsRuntimeDAO = getHelper().getUserAuctionsRuntimeDAO();
                        QueryBuilder<UserAuction, Integer> uqb = userAuctionsRuntimeDAO.queryBuilder();

                        try {
                            qb.selectRaw("MAX(price)").where().eq("auction_id", String.valueOf(auction.getId()));
                            String[] result = bidRuntimeDAO.queryRaw(qb.prepareStatementString()).getFirstResult();

                            if (result[0] == null) {
                                getActivity().startService(new Intent(getActivity(), NotificationService.class));
                                auctionsDAO.refresh(auction);
                                auction.setTopBid(user);
                                auctionsDAO.update(auction);
                            } else if (newBid.getPrice() > Integer.valueOf(result[0])) {
                                getActivity().startService(new Intent(getActivity(), NotificationService.class));
                                auctionsDAO.refresh(auction);
                                usersDAO.refresh(auction.getTopBid());
                                sendNotification(auction.getTopBid(),
                                        "Aukcija " + auction.getId(),
                                        "Vasa ponuda vise nije najveca",
                                        auction);
                                auction.setTopBid(user);
                                auctionsDAO.update(auction);
                            }

                            List<UserAuction> userAuctions =
                                    uqb.where().eq("user_id", String.valueOf(user.getId()))
                                            .and()
                                            .eq("auction_id", String.valueOf(auction.getId())).query();

                            if (userAuctions.size() < 1) {
                                userAuctionsDAO.create(new UserAuction(user, auction));
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                        bidsDAO.create(newBid);
                        auctionsDAO.refresh(auction);
                        Toast.makeText(getContext(), "Ponuda napravljena", Toast.LENGTH_SHORT).show();
                        bidAdapter.clear();
                        bidAdapter.addAll(auction.getBids());
                        bidAdapter.notifyDataSetChanged();
                    }
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            dialog.show();
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

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
