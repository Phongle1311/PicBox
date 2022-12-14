package com.hcmus.picbox.activities;

import static android.Manifest.permission.ACCESS_MEDIA_LOCATION;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.ScreenSlidePagerAdapter;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.transformers.ZoomOutPageTransformer;
import com.hcmus.picbox.utils.PermissionUtils;

/**
 * Created on 16/11/2022 by Phong Le
 */
public class DisplayMediaActivity extends AppCompatActivity {
    private final ActivityResultLauncher<String> requestLocationMediaPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Permissions denied, Permissions are required to use the app...", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_media);
        if (Build.VERSION.SDK_INT >= 29) {
            if (!PermissionUtils.checkPermissions(this, ACCESS_MEDIA_LOCATION)) {
                PermissionUtils.requestPermissions(this, 123, ACCESS_MEDIA_LOCATION);
            }
        }
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
                        MediaHolder.sDeletedAlbum.getMediaList(), this::finish);
                break;
            case MediaHolder.KEY_FAVOURITE_ALBUM:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.sFavouriteAlbum.getMediaList(), this::finish);
                break;
            case MediaHolder.KEY_SECRET_ALBUM:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.sSecretAlbum.getMediaList(), this::finish);
                break;
            default:
                adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getLifecycle(),
                        MediaHolder.sTotalAlbum.getMediaList(), this::finish);
                break;
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position, false); // smoothScroll = false to immediately scroll to position
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
    }
}