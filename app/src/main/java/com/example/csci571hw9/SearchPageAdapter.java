package com.example.csci571hw9;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SearchPageAdapter extends FragmentPagerAdapter {

    int mNoOfTabs;

    public SearchPageAdapter(FragmentManager fm, int NumberOFTabs){
        super(fm);
        this.mNoOfTabs = NumberOFTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                SearchTab1 tab1 = new SearchTab1();
                return tab1;
            case 1:
                SearchTab2 tab2 = new SearchTab2();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
