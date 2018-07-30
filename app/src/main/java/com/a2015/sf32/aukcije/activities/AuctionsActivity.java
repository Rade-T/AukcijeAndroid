package com.a2015.sf32.aukcije.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.adapters.AuctionAdapter;
import com.a2015.sf32.aukcije.adapters.DrawerListAdapter;
import com.a2015.sf32.aukcije.db.DatabaseHelper;
import com.a2015.sf32.aukcije.model.Auction;
import com.a2015.sf32.aukcije.model.DataSingleton;
import com.a2015.sf32.aukcije.model.DrawerItem;
import com.a2015.sf32.aukcije.model.User;
import com.a2015.sf32.aukcije.model.UserAuction;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class AuctionsActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayList<DrawerItem> drawerItems = new ArrayList<>();
    private RelativeLayout drawerPane;
    private List<Auction> auctions;
    private ListView auctionsLayout;
    private AuctionAdapter auctionAdapter;
    private DatabaseHelper databaseHelper;
    private RelativeLayout searchArea;
    private RuntimeExceptionDao<Auction, Integer> auctionsDAO;
    private RuntimeExceptionDao<UserAuction, Integer> userAuctionsDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctions);
        auctionsDAO = getHelper().getAuctionsRuntimeDAO();
        userAuctionsDAO = getHelper().getUserAuctionsRuntimeDAO();

        prepareMenu(drawerItems);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.navList);
        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, drawerItems);
        searchArea = (RelativeLayout) findViewById(R.id.search_area);
        searchArea.setVisibility(RelativeLayout.GONE);

        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);

        TextView txtUsername = (TextView) findViewById(R.id.userName);
        TextView txtName = (TextView) findViewById(R.id.name);

        txtUsername.setText(DataSingleton.getInstance().getUser().getEmail());
        txtName.setText(DataSingleton.getInstance().getUser().getName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        User user = DataSingleton.getInstance().getUser();
        List<UserAuction> userAuctions = userAuctionsDAO.queryForEq("user_id", user.getId());
        auctions = new ArrayList<>();
        for (UserAuction ua:
             userAuctions) {
            auctionsDAO.refresh(ua.getAuction());
            auctions.add(ua.getAuction());
        }
        auctionsLayout = (ListView)findViewById(R.id.auctionsView);
        auctionAdapter = new AuctionAdapter(this, auctions);
        auctionsLayout.setAdapter(auctionAdapter);
        auctionsLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromList(position);
            }
        });

        Spinner category = (Spinner) findViewById(R.id.query_category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.item_query_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        Spinner visibility = (Spinner) findViewById(R.id.query_visibility);
        ArrayAdapter<CharSequence> visibilityAdapter = ArrayAdapter.createFromResource(this,
                R.array.auction_query_visibility, android.R.layout.simple_spinner_item);
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visibility.setAdapter(visibilityAdapter);

        Button searchButton = (Button) findViewById(R.id.search_items);
        searchButton.setOnClickListener(new AuctionsActivity.SearchButtonClickListener());
    }

    private void selectItemFromList(int position) {
        Auction auction = auctions.get(position);
        Intent intent = new Intent(AuctionsActivity.this, AuctionActivity.class);
        intent.putExtra("auction_id", auction.getId());
        startActivity(intent);
    }

    private void prepareMenu(ArrayList<DrawerItem> drawerItems) {
        drawerItems.add(new DrawerItem("Aukcije", "Prikazi sve aukcije"));
        drawerItems.add(new DrawerItem("Predmeti", "Prikazi sve predmete"));
        drawerItems.add(new DrawerItem("Podesavanja", "Idi na podesavanja"));
        drawerItems.add(new DrawerItem("Filtriraj", "Filtriraj aukcije po predmetima"));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }
    }

    private class SearchButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            EditText query_text = (EditText) findViewById(R.id.query_text);
            Spinner category = (Spinner) findViewById(R.id.query_category);
            String query = query_text.getText().toString();
            String query_category = category.getSelectedItem().toString();
            Spinner visibility = (Spinner) findViewById(R.id.query_visibility);
            String query_visibility = visibility.getSelectedItem().toString();

            List<Auction> filteredList = new ArrayList<>();
            User user = DataSingleton.getInstance().getUser();
            List<UserAuction> userAuctions = userAuctionsDAO.queryForEq("user_id", user.getId());
            List<Auction> auctions = new ArrayList<>();
            for (UserAuction ua:
                    userAuctions) {
                auctionsDAO.refresh(ua.getAuction());
                auctions.add(ua.getAuction());
            }

            for (Auction auction :
                    auctions) {
                getHelper().getItemsRuntimeDAO().refresh(auction.getItem());
                switch (query_category) {
                    case "Naziv":
                        if (auction.getItem().getName().contains(query)) {
                            Date current = new Date();
                            switch (query_visibility) {
                                case "Sve":
                                    filteredList.add(auction);
                                    break;
                                case "U toku":
                                    if (auction.getStartDate().before(current) &&
                                            auction.getEndDate().after(current)) {
                                        filteredList.add(auction);
                                    }
                                    break;
                                case "Zavrsene":
                                    if (auction.getEndDate().before(current)) {
                                        filteredList.add(auction);
                                    }
                                    break;
                            }
                        }
                        break;
                    case "Opis":
                        if (auction.getItem().getDescription().contains(query)) {
                            Date current = new Date();
                            switch (query_visibility) {
                                case "Sve":
                                    filteredList.add(auction);
                                    break;
                                case "U toku":
                                    if (auction.getStartDate().before(current) &&
                                            auction.getEndDate().after(current)) {
                                        filteredList.add(auction);
                                    }
                                    break;
                                case "Zavrsene":
                                    if (auction.getEndDate().before(current)) {
                                        filteredList.add(auction);
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }

            auctions = filteredList;
            auctionAdapter.clear();
            auctionAdapter.addAll(auctions);
            auctionAdapter.notifyDataSetChanged();
            searchArea.setVisibility(RelativeLayout.GONE);
            query_text.clearFocus();
        }
    }

    private void selectItemFromDrawer(int position) {
        switch (position) {
            case 0:
                Intent aukcije = new Intent(AuctionsActivity.this, AuctionsActivity.class);
                startActivity(aukcije);
                break;
            case 1:
                Intent predmeti = new Intent(AuctionsActivity.this, ItemsActivity.class);
                startActivity(predmeti);
                break;
            case 2:
                Intent podesavanja = new Intent(AuctionsActivity.this, SettingsActivity.class);
                startActivity(podesavanja);
                break;
            case 3:
                searchArea.setVisibility(RelativeLayout.VISIBLE);
            default: break;
        }
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerPane);
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
