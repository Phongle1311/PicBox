package com.hcmus.picbox.activities;

import static android.Manifest.permission.ACCESS_MEDIA_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_GROUP_MODE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_LANGUAGE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_SPAN_COUNT;

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
import com.hcmus.picbox.utils.SharedPreferencesUtils;
import com.hcmus.picbox.utils.StorageUtils;

public class MainActivity extends AppCompatActivity {

    private ViewPager mainViewPager;
    private BottomNavigationView bottomBar;
    private FloatingActionButton cameraButton;

    /**
     * Use registerForActivityResult instead of onRequestPermissionResult because
     * the old method is deprecated
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    StorageUtils.getAllMediaFromStorage(this);
                } else {
                    Toast.makeText(this, "Permissions denied, Permissions are required to use the app...", Toast.LENGTH_SHORT).show();
                }
            });

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

        initSharedPreferencesDefault();
        initUI();
        initViewPager();
        if(!PermissionUtils.checkPermissions(this,ACCESS_MEDIA_LOCATION)){
            PermissionUtils.requestPermissions(this,123,ACCESS_MEDIA_LOCATION);
        }
        // check permission
        if (PermissionUtils.checkPermissions(this, READ_EXTERNAL_STORAGE))
            StorageUtils.getAllMediaFromStorage(this);
        else if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            // TODO: show dialog to educate user and persuade user to grant permission
            Toast.makeText(this, "need to show rationale", Toast.LENGTH_LONG).show();
        } else
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);

        cameraButton.setOnClickListener(view -> {
            if (PermissionUtils.checkPermissions(this, CAMERA)) {
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(cameraIntent);
            } else if (shouldShowRequestPermissionRationale(CAMERA)) {
                bottomBar.getMenu().findItem(R.id.setting).setChecked(true);
                mainViewPager.setCurrentItem(3);
            } else
                requestCameraPermissionLauncher.launch(CAMERA);
        });
    }

    private void initSharedPreferencesDefault() {
        String[] SharedPreferencesKeys = {"num_columns_of_row", "group_mode", "language"} ;
        for (String key : SharedPreferencesKeys){
            if (!SharedPreferencesUtils.checkKeyExist(this, key)){
                switch (key){
                    case "num_columns_of_row":
                        SharedPreferencesUtils.saveData(this,KEY_SPAN_COUNT, 4);
                        break;
                    case "group_mode":
                        SharedPreferencesUtils.saveData(this, KEY_GROUP_MODE, getResources().getString(R.string.month));
                        break;
                    case "language":
                        SharedPreferencesUtils.saveData(this, KEY_LANGUAGE, "vietnamese");
                        break;
                }
            }
        }
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