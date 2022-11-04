package com.hcmus.picbox.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PhotosFragment();
            case 1:
                return new GalleryFragment();
            case 2:
                return new DrawingFragment();
            case 3:
                return new SettingFragment();
            default:
                return new PhotosFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
