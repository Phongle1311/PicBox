package com.hcmus.picbox.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;

    private FloatingActionButton cameraButton;
    private ViewPager mainViewPager;
    private BottomNavigationView bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivity(intent);
                } else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE);
                }
            }
        });

        initViewPager();
    }

    private void initUI() {
        mainViewPager = findViewById(R.id.viewpager_main);
        bottomBar = findViewById(R.id.bottom_navigation_view);
        bottomBar.setBackground(null);
        cameraButton = findViewById(R.id.cameraButton);
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mainViewPager.setAdapter(adapter);

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomBar.getMenu().findItem(R.id.photos).setChecked(true);
                        break;
                    case 1:
                        bottomBar.getMenu().findItem(R.id.gallery).setChecked(true);
                        break;
                    case 2:
                        bottomBar.getMenu().findItem(R.id.drawing).setChecked(true);
                        break;
                    case 3:
                        bottomBar.getMenu().findItem(R.id.setting).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomBar.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.photos:
                    mainViewPager.setCurrentItem(0);
                    break;
                case R.id.gallery:
                    mainViewPager.setCurrentItem(1);
                    break;
                case R.id.drawing:
                    mainViewPager.setCurrentItem(2);
                    break;
                case R.id.setting:
                    mainViewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });
    }
}