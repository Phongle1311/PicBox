package com.hcmus.picbox.activities;

import static android.Manifest.permission.ACCESS_MEDIA_LOCATION;
import static android.Manifest.permission.SET_WALLPAPER;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.ScreenSlidePagerAdapter;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.transformers.ZoomOutPageTransformer;
import com.hcmus.picbox.utils.PermissionUtils;
import com.hcmus.picbox.works.DeleteHelper;

/**
 * Created on 16/11/2022 by Phong Le
 */
public class DisplayMediaActivity extends AppCompatActivity {

    private ScreenSlidePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_media);

        // Request permission to show geolocation and map
        if (Build.VERSION.SDK_INT >= 29) {
            if (!PermissionUtils.checkPermissions(this, ACCESS_MEDIA_LOCATION)) {
                PermissionUtils.requestPermissions(this, 123, ACCESS_MEDIA_LOCATION);
            }
        }
        if (Build.VERSION.SDK_INT <= 28) {
            if (!PermissionUtils.checkPermissions(this, WRITE_EXTERNAL_STORAGE)) {
                PermissionUtils.requestPermissions(this, 124, WRITE_EXTERNAL_STORAGE);
            }
        }
        if (!PermissionUtils.checkPermissions(this, SET_WALLPAPER)) {
            PermissionUtils.requestPermissions(this, 125, SET_WALLPAPER);
        }

        // Get selected model's position
        Bundle bundle = getIntent().getBundleExtra("model");
        String albumId = bundle.getString("category", MediaHolder.KEY_TOTAL_ALBUM);
        int position = bundle.getInt("position", 0);

        // Init View pager
        ViewPager2 viewPager = findViewById(R.id.pager);
        adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                AlbumHolder.sGetAlbumById(albumId).getMediaList(),
                albumId.equals(MediaHolder.sSecretAlbum.getId()), this::finish);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position, false); // immediately scroll to position
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DeleteHelper.DELETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            adapter.removeFragment(ScreenSlidePagerAdapter.deletePosition);
        }
    }
}