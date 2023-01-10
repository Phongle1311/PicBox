package com.hcmus.picbox.activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.FAB_BUTTON_DEFAULT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.GROUP_MODE_DEFAULT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_FAB_BUTTON;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_FOOD_QUESTION;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_GROUP_MODE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_LANGUAGE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_LAYOUT_MODE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_SPAN_COUNT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.LANGUAGE_DEFAULT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.LAYOUT_MODE_DEFAULT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.SPAN_COUNT_DEFAULT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hcmus.picbox.R;
import com.hcmus.picbox.database.album.AlbumEntity;
import com.hcmus.picbox.database.album.AlbumWithMedias;
import com.hcmus.picbox.database.album.AlbumsDatabase;
import com.hcmus.picbox.database.album.MediaEntity;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.models.VideoModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.utils.FileUtils;
import com.hcmus.picbox.utils.PermissionUtils;
import com.hcmus.picbox.utils.SharedPreferencesUtils;
import com.hcmus.picbox.works.LoadStorageHelper;

import java.io.File;
import java.util.List;

/**
 * Created on 9/1/2023 by Phong Le
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // init default config
        new Thread(this::initSharedPreferencesDefault).start();

        // get secret album from db
        new Thread(this::getSecretAlbum).start();

        // get medias from device and then get all user albums from db
        if (PermissionUtils.checkPermissions(this, READ_EXTERNAL_STORAGE)) {
            LoadStorageHelper.getAllMediaFromStorage(this);
            new Thread(this::getUserAlbums).start(); // must be after getAllMediaFromStorage
        }

        // count down for main activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void initSharedPreferencesDefault() {
        for (String key : SharedPreferencesUtils.SharedPreferencesKeys) {
            if (SharedPreferencesUtils.checkKeyExist(this, key)) continue;
            switch (key) {
                case KEY_SPAN_COUNT:
                    SharedPreferencesUtils.saveData(this, KEY_SPAN_COUNT, SPAN_COUNT_DEFAULT);
                    break;
                case KEY_FAB_BUTTON:
                    SharedPreferencesUtils.saveData(this, KEY_FAB_BUTTON, FAB_BUTTON_DEFAULT);
                    break;
                case KEY_GROUP_MODE:
                    SharedPreferencesUtils.saveData(this, KEY_GROUP_MODE, GROUP_MODE_DEFAULT);
                    break;
                case KEY_LANGUAGE:
                    SharedPreferencesUtils.saveData(this, KEY_LANGUAGE, LANGUAGE_DEFAULT);
                    break;
                case KEY_LAYOUT_MODE:
                    SharedPreferencesUtils.saveData(this, KEY_LAYOUT_MODE, LAYOUT_MODE_DEFAULT);
                    break;
            }
        }
    }

    private void getSecretAlbum() {
        // Load all media from secret internal directory
        MediaHolder.sSecretAlbum.setPath(getDir("secret_photos", Context.MODE_PRIVATE).getPath());
        File secretDir = new File(MediaHolder.sSecretAlbum.getPath());
        if (!secretDir.exists()) {
            Toast.makeText(this, "Secret directory doesn't exist", Toast.LENGTH_SHORT).show();
        } else {
            File[] list = secretDir.listFiles();
            if (list != null && list.length > 0)
                for (File file : list) {
                    if (FileUtils.isVideoFile(file.getPath()))
                        MediaHolder.sSecretAlbum.add(new VideoModel(file.getPath()));
                    else if (FileUtils.isPhotoFile(file.getPath()))
                        MediaHolder.sSecretAlbum.add(new PhotoModel(file.getPath()));
                }
        }
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