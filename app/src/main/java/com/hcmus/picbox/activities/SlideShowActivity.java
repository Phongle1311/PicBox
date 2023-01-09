package com.hcmus.picbox.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;
import technolifestyle.com.imageslider.pagetransformers.DepthPageTransformer;
import technolifestyle.com.imageslider.pagetransformers.ZoomOutPageTransformer;

public class SlideShowActivity extends AppCompatActivity {
    private final String[] speeds = {"0.25x", "0.5x", "1x", "2x"};
    private ImageView btnBack;
    private ImageView btnReplay;
    private FlipperLayout flipperLayout;
    private ImageView btnPlayPause;
    private boolean isPlaying = true;
    private int scrollTime = 2;
    private Spinner spinnerChooseSpeed;
    private MediaPlayer player;
    private ImageView btnChooseMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        bindUI();
        btnBack.setOnClickListener(v -> finish());

        Bundle bundle = getIntent().getBundleExtra("media_selected_list");
        String albumId = bundle.getString("album_id");
        ArrayList<Integer> selected_media_id = bundle.getIntegerArrayList("selected_media_id");

        AlbumModel album = AlbumHolder.sGetAlbumById(albumId);
        List<MediaModel> medias = new ArrayList<>();
        for (Integer id : selected_media_id)
            medias.add(album.findMediaById(id));

        for (MediaModel media : medias) {
            FlipperView view = new FlipperView(SlideShowActivity.this);
            try {
                view.setImage(R.drawable.placeholder_color, (imageView, o) -> {
                    Glide.with(SlideShowActivity.this).load(media.getFile()).placeholder(R.drawable.placeholder_color).error(R.drawable.placeholder_color).into(imageView);
                    return Unit.INSTANCE;
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            view.setDescriptionBackgroundAlpha(0f);
            flipperLayout.addFlipperView(view);
        }

        flipperLayout.setScrollTimeInSec(scrollTime);
        setListener();
    }

    private void bindUI() {
        btnBack = findViewById(R.id.img_slider_back);
        btnReplay = findViewById(R.id.img_slider_replay);
        flipperLayout = (FlipperLayout) findViewById(R.id.flipper_layout);
        btnPlayPause = findViewById(R.id.img_slider_play_pause);
        spinnerChooseSpeed = (Spinner) findViewById(R.id.spinner_choose_speed);
        btnChooseMusic = findViewById(R.id.img_choose_music);
    }

    private void setListener() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, speeds);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChooseSpeed.setAdapter(spinnerArrayAdapter);
        spinnerChooseSpeed.setSelection(2);
        btnReplay.setOnClickListener(v -> {
            flipperLayout.onCurrentPageChanged(0);
            player.seekTo(0);
            player.start();
        });
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    flipperLayout.removeAutoCycle();
                    isPlaying = false;
                    player.pause();
                    btnPlayPause.setImageResource(R.drawable.ic_baseline_play_24);
                } else {
                    flipperLayout.startAutoCycle(scrollTime);
                    player.start();
                    isPlaying = true;
                    btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
                }

            }
        });
        spinnerChooseSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scrollTime = (1 << (3 - position));
                flipperLayout.setScrollTimeInSec(scrollTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnChooseMusic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            fileChooseActivity.launch(intent);
        });
    }

    ActivityResultLauncher<Intent> fileChooseActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent resultData = result.getData();
                Uri uri = null;
                if (resultData != null) {
                    uri = resultData.getData();
                    player = MediaPlayer.create(getBaseContext(), uri);
                    player.setLooping(true);
                    player.start();
                }
            }
        }
    });
}