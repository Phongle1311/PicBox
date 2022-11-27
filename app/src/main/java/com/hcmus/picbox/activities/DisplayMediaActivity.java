package com.hcmus.picbox.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.ScreenSlidePagerAdapter;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.models.dataholder.MediaHolder;

/**
 * Created on 16/11/2022 by Phong Le
 */
public class DisplayMediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_media);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("model");
        String category = bundle.getString("category", MediaHolder.KEY_TOTAL_ALBUM);
        int position = bundle.getInt("position", 0);

        ViewPager2 viewPager = findViewById(R.id.pager);
        ScreenSlidePagerAdapter adapter;
        switch (category) {
            case MediaHolder.KEY_DELETED_ALBUM:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.getDeletedAlbum().getDefaultList());
                break;
            case MediaHolder.KEY_FAVOURITE_ALBUM:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.getFavouriteAlbum().getDefaultList());
                break;
            case MediaHolder.KEY_SECRET_ALBUM:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.getSecretAlbum().getDefaultList());
                break;
            default:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.getTotalAlbum().getDefaultList());
                break;
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        topAppBar.setNavigationOnClickListener(view -> finish());
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.ic_favourite) {
                // TODO: add/remove image to/from favourite album
                return true;
            } else if (item.getItemId() == R.id.ic_more) {
                // TODO: show bottom drawer (show detail)
                return true;
            }
            return false;
        });
    }
}