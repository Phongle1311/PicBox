package com.hcmus.picbox.activities;

import static android.Manifest.permission.CAMERA;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.ViewPagerAdapter;
import com.hcmus.picbox.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton cameraButton;
    private ViewPager mainViewPager;
    private BottomNavigationView bottomBar;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivity(cameraIntent);
                } else {
                    Toast.makeText(this, "Permissions denied, Permissions are required to use the app..", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        cameraButton.setOnClickListener(view -> {
            if (PermissionUtils.checkPermissions(this, CAMERA)) {
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(cameraIntent);
            }
            else if (shouldShowRequestPermissionRationale(CAMERA)) {
                bottomBar.getMenu().findItem(R.id.setting).setChecked(true);
                mainViewPager.setCurrentItem(3);
            } else
                requestCameraPermissionLauncher.launch(CAMERA);
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