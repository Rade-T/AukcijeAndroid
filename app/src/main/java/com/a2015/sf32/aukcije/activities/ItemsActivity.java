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
import com.a2015.sf32.aukcije.adapters.DrawerListAdapter;
import com.a2015.sf32.aukcije.adapters.ItemAdapter;
import com.a2015.sf32.aukcije.db.DatabaseHelper;
import com.a2015.sf32.aukcije.model.DataSingleton;
import com.a2015.sf32.aukcije.model.DrawerItem;
import com.a2015.sf32.aukcije.model.Item;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity {
    private RuntimeExceptionDao<Item, Integer> itemsDAO;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayList<DrawerItem> drawerItems = new ArrayList<>();
    private RelativeLayout drawerPane;
    private List<Item> items;
    private ListView itemsLayout;
    private ItemAdapter itemAdapter;
    private DatabaseHelper databaseHelper;
    private RelativeLayout searchArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        prepareMenu(drawerItems);
        itemsDAO = getHelper().getItemsRuntimeDAO();

        setTitle("Predmeti");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.navList);
        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, drawerItems);

        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);

        TextView txtUsername = (TextView) findViewById(R.id.userName);
        TextView txtName = (TextView) findViewById(R.id.name);

        txtUsername.setText(DataSingleton.getInstance().getUser().getEmail());
        txtName.setText(DataSingleton.getInstance().getUser().getName());

        searchArea = (RelativeLayout) findViewById(R.id.search_area);
        searchArea.setVisibility(RelativeLayout.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        itemsLayout = (ListView) findViewById(R.id.itemsView);
        items = itemsDAO.queryForEq("sold", false);
        itemAdapter = new ItemAdapter(this, items);
        itemsLayout.setAdapter(itemAdapter);
        itemsLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        Button searchButton = (Button) findViewById(R.id.search_items);
        searchButton.setOnClickListener(new SearchButtonClickListener());
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void prepareMenu(ArrayList<DrawerItem> drawerItems) {
        drawerItems.add(new DrawerItem("Aukcije", "Prikazi sve aukcije"));
        drawerItems.add(new DrawerItem("Predmeti", "Prikazi sve predmete"));
        drawerItems.add(new DrawerItem("Podesavanja", "Idi na podesavanja"));
        drawerItems.add(new DrawerItem("Filtriraj", "Filtriraj predmete"));
    }

    private class SearchButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            EditText query_text = (EditText) findViewById(R.id.query_text);
            Spinner category = (Spinner) findViewById(R.id.query_category);
            String query = query_text.getText().toString();
            String query_category = category.getSelectedItem().toString();
            Log.d("Spinner", "Kategorija je " + query_category);
            List<Item> filteredList = new ArrayList<Item>();

            for (Item item :
                    getHelper().getItemsRuntimeDAO().queryForEq("sold", false)) {
                switch (query_category) {
                    case "Naziv":
                        if (item.getName().contains(query)) {
                            filteredList.add(item);
                        }
                        break;
                    case "Opis":
                        if (item.getDescription().contains(query)) {
                            filteredList.add(item);
                        }
                        break;
                }
            }

            items = filteredList;
            itemAdapter.clear();
            itemAdapter.addAll(items);
            itemAdapter.notifyDataSetChanged();
            searchArea.setVisibility(RelativeLayout.GONE);
            query_text.clearFocus();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }
    }

    private void selectItemFromList(int position) {
        Item item = items.get(position);
        Intent intent = new Intent(ItemsActivity.this, ItemActivity.class);
        intent.putExtra("itemId", item.getId());
        startActivity(intent);
    }

    private void selectItemFromDrawer(int position) {
        if (position == 0) {
            Intent aukcije = new Intent(ItemsActivity.this, AuctionsActivity.class);
            startActivity(aukcije);
        } else if (position == 1) {
            Intent predmeti = new Intent(ItemsActivity.this, ItemsActivity.class);
            startActivity(predmeti);
        } else if (position == 2) {
            Intent podesavanja = new Intent(ItemsActivity.this, SettingsActivity.class);
            startActivity(podesavanja);
        } else if (position == 3) {
            searchArea.setVisibility(RelativeLayout.VISIBLE);
        }

        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerPane);
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
