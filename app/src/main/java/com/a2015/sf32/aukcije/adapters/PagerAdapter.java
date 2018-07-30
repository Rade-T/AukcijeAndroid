package com.a2015.sf32.aukcije.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.a2015.sf32.aukcije.fragments.AuctionFragment;
import com.a2015.sf32.aukcije.fragments.ItemFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    int itemId;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, int itemId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.itemId = itemId;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("itemId", this.itemId);
        switch (position) {
            case 0:
                ItemFragment itemFragment = new ItemFragment();
                itemFragment.setArguments(bundle);
                return itemFragment;
            case 1:
                AuctionFragment auctionFragment = new AuctionFragment();
                auctionFragment.setArguments(bundle);
                return auctionFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
