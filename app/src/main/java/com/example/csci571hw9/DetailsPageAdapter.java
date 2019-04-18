package com.example.csci571hw9;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DetailsPageAdapter extends FragmentPagerAdapter {

    int mNoOfTabs;
    private Details mContext;

    public DetailsPageAdapter(FragmentManager fm, int NumberOFTabs, Details context){
        super(fm);
        this.mNoOfTabs = NumberOFTabs;
        mContext = context;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                DetailsTabProduct tabProduct = new DetailsTabProduct();
                return tabProduct;
            case 1:
                DetailsTabShipping tabShipping = new DetailsTabShipping();
                return tabShipping;
            case 2:
                DetailsTabPhotos tabPhotos = new DetailsTabPhotos();
                return tabPhotos;
            case 3:
                DetailsTabSimilar tabSimilar = new DetailsTabSimilar();
                return tabSimilar;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
