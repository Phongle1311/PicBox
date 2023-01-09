package com.hcmus.picbox.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.transformers.CubeInPageTransformer;
import com.hcmus.picbox.transformers.CubeOutPageTransformer;
import com.hcmus.picbox.transformers.NoAnimationPageTransformer;
import com.hcmus.picbox.transformers.ZoomInTransformer;

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
import technolifestyle.com.imageslider.pagetransformers.ZoomOutPageTransformerKt;

public class SlideShowActivity extends AppCompatActivity {
    private final String[] speeds = {"0.25x", "0.5x", "1x", "2x"};
    private final String[] animations = {"Zoom out Page Transformer", "Depth page transformer",
            "Zoom in Page Transformer", "Cube in Page Transformer", "Cube out Page Transformer"};
    private ImageView btnBack;
    private ImageView btnReplay;
    private FlipperLayout flipperLayout;
    private ImageView btnPlayPause;
    private ImageView btnChooseAnimation;
    private boolean isPlaying = true;
    private int scrollTime = 2;
    private Spinner spinnerChooseSpeed;
    private MediaPlayer player;
    private ImageView btnChooseMusic;
    private List<MediaModel> medias;

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
        medias = new ArrayList<>();
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
        btnChooseAnimation = findViewById(R.id.img_slider_choose_animation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    private void setListener() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, speeds);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChooseSpeed.setAdapter(spinnerArrayAdapter);
        spinnerChooseSpeed.setSelection(2);
        btnReplay.setOnClickListener(v -> {
            flipperLayout.setScrollTimeInSec(1);
            flipperLayout.onCurrentPageChanged(0);
            flipperLayout.setScrollTimeInSec(scrollTime);
            if(!isPlaying){
                isPlaying = true;
                btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
            }
            if (isPlaying&&player != null) {
                player.seekTo(0);
                player.start();
            }
        });
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    flipperLayout.removeAutoCycle();
                    isPlaying = false;
                    if (player != null) {
                        player.pause();
                    }
                    btnPlayPause.setImageResource(R.drawable.ic_baseline_play_24);
                } else {
                    flipperLayout.startAutoCycle(scrollTime);
                    if (player != null) {
                        player.start();
                    }
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
        btnChooseAnimation.setOnClickListener(v -> showChooseAnimationDialog());
    }

    private void showChooseAnimationDialog() {
        Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_animation, null);
        dialog.setContentView(view);
        ListView listView = view.findViewById(R.id.list_view_choose_animation);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, animations);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int current = flipperLayout.getCurrentPagePosition();
                flipperLayout.removeAllFlipperViews();
                for (MediaModel media : medias) {
                    FlipperView v = new FlipperView(SlideShowActivity.this);
                    try {
                        v.setImage(R.drawable.placeholder_color, (imageView, o) -> {
                            Glide.with(SlideShowActivity.this).load(media.getFile()).placeholder(R.drawable.placeholder_color).error(R.drawable.placeholder_color).into(imageView);
                            return Unit.INSTANCE;
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    v.setDescriptionBackgroundAlpha(0f);
                    flipperLayout.addFlipperView(v);
                }
                flipperLayout.setScrollTimeInSec(scrollTime);
                switch (position) {
                    case 0:
                        flipperLayout.addPageTransformer(false, new ZoomOutPageTransformer());
                        break;
                    case 1:
                        flipperLayout.addPageTransformer(false, new DepthPageTransformer());
                        break;
                    case 2:
                        flipperLayout.addPageTransformer(false, new ZoomInTransformer());
                        break;
                    case 3:
                        flipperLayout.addPageTransformer(false, new CubeInPageTransformer());
                        break;
                    case 4:
                        flipperLayout.addPageTransformer(false, new CubeOutPageTransformer());
                        break;
                    default:
                        break;
                }
                flipperLayout.customizeFlipperPager(new Function1<ViewPager, Unit>() {
                    @Override
                    public Unit invoke(ViewPager viewPager) {
                        viewPager.setCurrentItem(0);
                        viewPager.setCurrentItem(current);
                        return Unit.INSTANCE;
                    }
                });
                flipperLayout.onCurrentPageChanged(current);
                dialog.dismiss();
            }
        });
        dialog.show();
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
                    if(isPlaying) {
                        player.start();
                    }
                }
            }
        }
    });

}