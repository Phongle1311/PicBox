package com.hcmus.picbox.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.hcmus.picbox.fragments.AlbumFragment;
import com.hcmus.picbox.fragments.CreativityFragment;
import com.hcmus.picbox.fragments.PhotosFragment;
import com.hcmus.picbox.fragments.SettingFragment;
import com.hcmus.picbox.models.dataholder.MediaHolder;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final int PHOTOS = 0;
    public static final int ALBUM = 1;
    public static final int CREATIVITY = 2;
    public static final int SETTING = 3;
    public static final int TAB_COUNTS = 4;
    private PhotosFragment photosFragment;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ALBUM:
                return new AlbumFragment();
            case CREATIVITY:
                return new CreativityFragment();
            case SETTING:
                return new SettingFragment();
            case PHOTOS:
                photosFragment = new PhotosFragment(MediaHolder.KEY_TOTAL_ALBUM);
                return photosFragment;
            default:
                throw new IllegalArgumentException("Invalid position!");
        }
    }

    @Override
    public int getCount() {
        return TAB_COUNTS;
    }

    public PhotosFragment getPhotosFragment() {
        return photosFragment;
    }
}
