package com.hcmus.picbox.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.ScreenSlidePagerAdapter;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.transformers.ZoomOutPageTransformer;

/**
 * Created on 16/11/2022 by Phong Le
 */
public class DisplayMediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_media);

        // Get model being selected
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("model");
        String category = bundle.getString("category", MediaHolder.KEY_TOTAL_ALBUM);
        int position = bundle.getInt("position", 0);

        // View pager logic
        ViewPager2 viewPager = findViewById(R.id.pager);
        ScreenSlidePagerAdapter adapter;
        switch (category) {
            case MediaHolder.KEY_DELETED_ALBUM:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.getDeletedAlbum().getDefaultList(), this::finish);
                break;
            case MediaHolder.KEY_FAVOURITE_ALBUM:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.getFavouriteAlbum().getDefaultList(), this::finish);
                break;
            case MediaHolder.KEY_SECRET_ALBUM:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.getSecretAlbum().getDefaultList(), this::finish);
                break;
            default:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.getTotalAlbum().getDefaultList(), this::finish);
                break;
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position, false); // smoothScroll = false to immediately scroll to position
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
    }
}