package com.a2015.sf32.aukcije.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.db.DatabaseHelper;
import com.a2015.sf32.aukcije.model.Auction;
import com.a2015.sf32.aukcije.model.Item;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.text.SimpleDateFormat;
import java.util.List;

public class ItemFragment extends Fragment {
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        TextView txtPrice = (TextView) view.findViewById(R.id.txtPrice);
        TextView txtStardDate = (TextView) view.findViewById(R.id.txtStartDate);
        TextView txtEndDate = (TextView) view.findViewById(R.id.txtEndDate);
        TextView txtItemName = (TextView) view.findViewById(R.id.txtItemName);
        TextView txtDescription = (TextView) view.findViewById(R.id.txtItemDescription);
        TextView txtItemSold = (TextView) view.findViewById(R.id.txtItemSold);
        int itemId = getArguments().getInt("itemId");

        Auction auction = findAuctionForItem(getHelper().getItemsRuntimeDAO().queryForId(itemId));
        txtPrice.setText("Pocetna cena: " + String.valueOf( auction.getStartPrice() ));
        txtStardDate.setText("Pocetni datum: " + sdf.format( auction.getStartDate() ));
        txtEndDate.setText("Zavrsni datum: " + sdf.format( auction.getEndDate() ));
        Log.i("Item ID", String.valueOf(itemId));
        RuntimeExceptionDao<Item, Integer> runtimeItemsDAO = getHelper().getItemsRuntimeDAO();
        Item selected = runtimeItemsDAO.queryForId(itemId);

        txtItemName.setText(selected.getName());
        txtDescription.setText(selected.getDescription());
        if (selected.sold) {
            txtItemSold.setText("Predmet je prodat");
        } else {
            txtItemSold.setText("Predmet nije prodat");
        }

        return view;
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private Auction findAuctionForItem(Item item) {
        Auction auction = null;
        RuntimeExceptionDao<Auction, Integer> runtimeActionDAO = getHelper().getAuctionsRuntimeDAO();
        List<Auction> auctions = runtimeActionDAO.queryForAll();

        for (Auction a :
                auctions) {
            if (a.getItem().getId() == item.getId()) {
                auction = a;
            }
        }
        return auction;
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
