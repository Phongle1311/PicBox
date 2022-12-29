package com.hcmus.picbox.activities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.GROUP_MODE_DEFAULT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_FOOD_QUESTION;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_GROUP_MODE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_LANGUAGE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PASSWORD;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PET_QUESTION;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_SPAN_COUNT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.PASSWORD_DEFAULT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.SPAN_COUNT_DEFAULT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.LANGUAGE_DEFAULT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.SPAN_COUNT_DEFAULT;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.ViewPagerAdapter;
import com.hcmus.picbox.fragments.PhotosFragment;
import com.hcmus.picbox.utils.PermissionUtils;
import com.hcmus.picbox.utils.SharedPreferencesUtils;
import com.hcmus.picbox.works.DeleteHelper;
import com.hcmus.picbox.works.LoadStorageHelper;

public class MainActivity extends AppCompatActivity {

    /**
     * Use registerForActivityResult instead of onRequestPermissionResult because
     * the old method is deprecated
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    LoadStorageHelper.getAllMediaFromStorage(this);
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
    private ViewPagerAdapter adapter;
    private ViewPager mainViewPager;
    private BottomNavigationView bottomBar;

    //    private final ActivityResultLauncher<String> requestWritePermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    Toast.makeText(this, "write permission is already granted!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Permissions denied, Permissions are required to use the app...", Toast.LENGTH_SHORT).show();
//                }
//            });
    private FloatingActionButton cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSharedPreferencesDefault();
        initUI();
        initViewPager();

        // check permission
//        if (PermissionUtils.checkPermissions(this, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)) {
//            Toast.makeText(this, "write permission is already granted!", Toast.LENGTH_SHORT).show();
//        }
//        else if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
//            Toast.makeText(this, "need to show rationale", Toast.LENGTH_LONG).show();
//        } else {
//            requestWritePermissionLauncher.launch(WRITE_EXTERNAL_STORAGE);
//        }

        if (PermissionUtils.checkPermissions(this, READ_EXTERNAL_STORAGE)) {
            LoadStorageHelper.getAllMediaFromStorage(this);
        } else if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            // TODO: show dialog to educate user and persuade user to grant permission
            Toast.makeText(this, "need to show rationale", Toast.LENGTH_LONG).show();
        } else {
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DeleteHelper.DELETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            PhotosFragment photosFragment = adapter.getPhotosFragment();
            if (photosFragment != null)
                photosFragment.finishDelete();
        }
    }

    private void initSharedPreferencesDefault() {
        String[] SharedPreferencesKeys = {KEY_SPAN_COUNT, KEY_GROUP_MODE, KEY_LANGUAGE};
        for (String key : SharedPreferencesKeys) {
            if (!SharedPreferencesUtils.checkKeyExist(this, key)) {
                switch (key) {
                    case KEY_SPAN_COUNT:
                        SharedPreferencesUtils.saveData(this, KEY_SPAN_COUNT, SPAN_COUNT_DEFAULT);
                        break;
                    case KEY_GROUP_MODE:
                        SharedPreferencesUtils.saveData(this, KEY_GROUP_MODE, GROUP_MODE_DEFAULT);
                        break;
                    case KEY_LANGUAGE:
                        SharedPreferencesUtils.saveData(this, KEY_LANGUAGE, LANGUAGE_DEFAULT);
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
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
            int itemId = item.getItemId();
            if (itemId == R.id.photos) {
                mainViewPager.setCurrentItem(0);
            } else if (itemId == R.id.gallery) {
                mainViewPager.setCurrentItem(1);
            } else if (itemId == R.id.drawing) {
                mainViewPager.setCurrentItem(2);
            } else if (itemId == R.id.setting) {
                mainViewPager.setCurrentItem(3);
            }
            return true;
        });
    }
}