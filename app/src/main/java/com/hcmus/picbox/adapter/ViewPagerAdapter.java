package com.hcmus.picbox.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.hcmus.picbox.constant.TAB_VALUES;
import com.hcmus.picbox.fragment.CreativityFragment;
import com.hcmus.picbox.fragment.AlbumFragment;
import com.hcmus.picbox.fragment.PhotosFragment;
import com.hcmus.picbox.fragment.SettingFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case TAB_VALUES.ALBUM:
                return new AlbumFragment();
            case TAB_VALUES.CREATIVITY:
                return new CreativityFragment();
            case TAB_VALUES.SETTING:
                return new SettingFragment();
            default:
                return new PhotosFragment();
        }
    }

    @Override
    public int getCount() {
        return TAB_VALUES.TAB_COUNTS;
    }
}
