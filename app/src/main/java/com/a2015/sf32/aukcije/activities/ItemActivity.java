package com.a2015.sf32.aukcije.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.a2015.sf32.aukcije.R;

public class ItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        int itemId = getIntent().getIntExtra("itemId", 0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.itemTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Predmet"));
        tabLayout.addTab(tabLayout.newTab().setText("Aukcija"));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.itemViewPager);
        final com.a2015.sf32.aukcije.adapters.PagerAdapter pagerAdapter = new com.a2015.sf32.aukcije.adapters.PagerAdapter(
                getSupportFragmentManager(), tabLayout.getTabCount(), itemId);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
