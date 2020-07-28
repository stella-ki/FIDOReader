package com.challenge.fidoreader;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public static final int ITEM_LIST = 2;
    private Fragment[] fragments = null;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Fragment[] fragments){
        super(fragmentActivity);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return ITEM_LIST;
    }
}

