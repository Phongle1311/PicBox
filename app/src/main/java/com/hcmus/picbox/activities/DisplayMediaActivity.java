package com.hcmus.picbox.activities;

import static android.Manifest.permission.ACCESS_MEDIA_LOCATION;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.ScreenSlidePagerAdapter;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.transformers.ZoomOutPageTransformer;
import com.hcmus.picbox.utils.PermissionUtils;

/**
 * Created on 16/11/2022 by Phong Le
 */
public class DisplayMediaActivity extends AppCompatActivity {

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

        // Get model being selected
        Bundle bundle = getIntent().getBundleExtra("model");
        String albumId = bundle.getString("category", MediaHolder.KEY_TOTAL_ALBUM);
        int position = bundle.getInt("position", 0);

        // Init View pager
        ViewPager2 viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(),
                getLifecycle(), AlbumHolder.sGetAlbumById(albumId).getMediaList(), this::finish));
        viewPager.setCurrentItem(position, false); // immediately scroll to position
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
    }
}