package com.a2015.sf32.aukcije.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.adapters.DrawerListAdapter;
import com.a2015.sf32.aukcije.db.DatabaseHelper;
import com.a2015.sf32.aukcije.model.Auction;
import com.a2015.sf32.aukcije.model.DataSingleton;
import com.a2015.sf32.aukcije.model.DrawerItem;
import com.a2015.sf32.aukcije.model.User;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AuctionActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayList<DrawerItem> drawerItems = new ArrayList<>();
    private RelativeLayout drawerPane;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Auction, Integer> auctionsDAO;
    private RuntimeExceptionDao<User, Integer> usersDAO;
    private Auction auction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction);

        prepareMenu(drawerItems);
        setTitle("Aukcija");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.navList);
        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, drawerItems);

        drawerList.setOnItemClickListener(new AuctionActivity.DrawerItemClickListener());
        drawerList.setAdapter(adapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        TextView txtUsername = (TextView) findViewById(R.id.userName);
        TextView txtName = (TextView) findViewById(R.id.name);

        txtUsername.setText(DataSingleton.getInstance().getUser().getEmail());
        txtName.setText(DataSingleton.getInstance().getUser().getName());

        TextView txtBrojAukcije = (TextView) findViewById(R.id.broj_aukcije);
        TextView txtPocetnaCena = (TextView) findViewById(R.id.pocetna_cena);
        TextView txtPocetniDatum = (TextView) findViewById(R.id.pocetni_datum);
        TextView txtZavrsniDatum = (TextView) findViewById(R.id.zavrsni_datum);
        TextView txtImeKorisnika = (TextView) findViewById(R.id.ime_korisnika);
        TextView txtNazivPredmeta = (TextView) findViewById(R.id.naziv_predmeta);

        auctionsDAO = getHelper().getAuctionsRuntimeDAO();
        usersDAO = getHelper().getUsersRuntimeDAO();
        int auction_id = getIntent().getIntExtra("auction_id", 1);
        auction = auctionsDAO.queryForId(auction_id);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        txtBrojAukcije.setText("Aukcija broj " + auction.getId());
        txtPocetnaCena.setText("Pocetna cena: " + String.valueOf(auction.getStartPrice()) );
        txtPocetniDatum.setText("Pocetni datum: " + sdf.format(auction.getStartDate()) );
        txtZavrsniDatum.setText("Zavrsni datum: " + sdf.format(auction.getEndDate()) );

        getHelper().getUsersRuntimeDAO().refresh(auction.getUser());
        txtImeKorisnika.setText("Aukciju kreirao " + String.valueOf(auction.getUser().getName()) );

        getHelper().getItemsRuntimeDAO().refresh(auction.getItem());
        txtNazivPredmeta.setText("Naziv predmeta na aukciji: " + String.valueOf(auction.getItem().getName()) );

        if (auction.isFinished()) {
            User winner = auction.getTopBid();
            usersDAO.refresh(winner);
            if (winner.getId() == DataSingleton.getInstance().getUser().getId()) {
                showOwnerInfo();
            }
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }
    }

    private void selectItemFromDrawer(int position) {
        if (position == 0) {
            Intent aukcije = new Intent(this, AuctionsActivity.class);
            startActivity(aukcije);
        } else if (position == 1) {
            Intent predmeti = new Intent(this, ItemsActivity.class);
            startActivity(predmeti);
        } else if (position == 2) {
            Intent podesavanja = new Intent(this, SettingsActivity.class);
            startActivity(podesavanja);
        }

        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerPane);
    }

    private void showOwnerInfo() {
        User owner = auction.getUser();
        usersDAO.refresh(owner);
        TextView txtName = (TextView) findViewById(R.id.user_name);
        TextView txtEmail = (TextView) findViewById(R.id.user_email);
        TextView txtAddress = (TextView) findViewById(R.id.user_address);
        TextView txtPhone = (TextView) findViewById(R.id.user_phone);

        txtName.setText("Ime: " + owner.getName());
        txtEmail.setText("Email: " + owner.getEmail());
        txtAddress.setText("Adresa: " + owner.getAddress());
        txtPhone.setText("Broj telefona: " + owner.getPhone());
    }

    private void prepareMenu(ArrayList<DrawerItem> drawerItems) {
        drawerItems.add(new DrawerItem("Aukcije", "Prikazi sve aukcije"));
        drawerItems.add(new DrawerItem("Predmeti", "Prikazi sve predmete"));
        drawerItems.add(new DrawerItem("Podesavanja", "Idi na podesavanja"));
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
