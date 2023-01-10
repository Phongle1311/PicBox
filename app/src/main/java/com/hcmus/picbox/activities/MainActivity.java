package com.hcmus.picbox.activities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_LANGUAGE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.LANGUAGE_OPTION_1;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.LANGUAGE_OPTION_2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
import com.hcmus.picbox.database.album.AlbumEntity;
import com.hcmus.picbox.database.album.AlbumWithMedias;
import com.hcmus.picbox.database.album.AlbumsDatabase;
import com.hcmus.picbox.database.album.MediaEntity;
import com.hcmus.picbox.fragments.PhotosFragment;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.services.MediaTrackerService;
import com.hcmus.picbox.utils.PermissionUtils;
import com.hcmus.picbox.utils.SharedPreferencesUtils;
import com.hcmus.picbox.works.DeleteHelper;
import com.hcmus.picbox.works.LoadStorageHelper;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /**
     * Use registerForActivityResult instead of onRequestPermissionResult because
     * the old method is deprecated
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    LoadStorageHelper.getAllMediaFromStorage(this);
                    new Thread(this::getUserAlbums).start();
                } else {
                    Toast.makeText(this, "Grant this required permission on setting to use the app", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivity(cameraIntent);
                } else {
                    Toast.makeText(this, "Grant this required permission on setting to use this feature", Toast.LENGTH_SHORT).show();
                }
            });

//    private final ActivityResultLauncher<String> requestWritePermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (!isGranted) {
//                    Toast.makeText(this, "Grant this required permission on setting to use the app", Toast.LENGTH_SHORT).show();
//                }
//            });
    Intent serviceIntent;
    BroadcastReceiver receiver;
    private ViewPager mainViewPager;
    private BottomNavigationView bottomBar;
    private ViewPagerAdapter adapter;
    private FloatingActionButton cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initViewPager();

        // check permission
//        if (!PermissionUtils.checkPermissions(this, WRITE_EXTERNAL_STORAGE)) {
//            requestWritePermissionLauncher.launch(WRITE_EXTERNAL_STORAGE);
//        }

        if (!PermissionUtils.checkPermissions(this, READ_EXTERNAL_STORAGE)) {
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);
        }

        // start service observe changes
        serviceIntent = new Intent(this, MediaTrackerService.class);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                if (intent.getAction().equals(MediaTrackerService.ACTION_NAME)) {
                    int count = intent.getIntExtra("insert", 0);
                    if (count > 0) {
                        PhotosFragment photosFragment = adapter.getPhotosFragment();
                        if (photosFragment != null)
                            photosFragment.updateAll();
                    }
                    Log.d("test", count + " added");
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(MediaTrackerService.ACTION_NAME));
        startService(serviceIntent);

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
    protected void onStop() {
        super.onStop();
        if (serviceIntent != null)
            stopService(serviceIntent);
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

    private void initUI() {
        mainViewPager = findViewById(R.id.viewpager_main);
        bottomBar = findViewById(R.id.bottom_navigation_view);
        bottomBar.setBackground(null);
        cameraButton = findViewById(R.id.cameraButton);

        Configuration config = new Configuration();
        switch (SharedPreferencesUtils.getStringData(this, KEY_LANGUAGE)) {
            case LANGUAGE_OPTION_1:
                config.setLocale(new Locale("en"));
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                break;
            case LANGUAGE_OPTION_2:
                config.setLocale(new Locale("vi"));
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                break;
            default:
                break;
        }
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

    private void getUserAlbums() {
        // todo: this code has high complexity, can make it decrease by list all media in Db first
        AlbumsDatabase db = AlbumsDatabase.getInstance(this);
        List<AlbumWithMedias> albumWithMedias = db.albumDao().getAllAlbumWithMedias();
        AlbumHolder userAlbumList = AlbumHolder.getUserAlbumList();

        for (AlbumWithMedias albumWithMedia : albumWithMedias) {
            AlbumEntity albumEntity = albumWithMedia.albumEntity;
            List<MediaEntity> mediaEntities = albumWithMedia.mediaEntities;
            AlbumModel album = new AlbumModel(albumEntity.albumName, String.valueOf(albumEntity.albumId));
            userAlbumList.addAlbum(album);
            mediaEntities.stream()
                    .map(mediaEntity -> MediaHolder.sTotalAlbum.findMediaById(mediaEntity.mediaId))
                    .forEach(album::add);
        }
    }
}