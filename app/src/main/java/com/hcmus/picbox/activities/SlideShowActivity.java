package com.hcmus.picbox.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class SlideShowActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView btnReplay;
    private FlipperLayout flipperLayout;

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
                    Glide.with(SlideShowActivity.this)
                            .load(media.getFile())
                            .placeholder(R.drawable.placeholder_color)
                            .error(R.drawable.placeholder_color)
                            .into(imageView);
                    return Unit.INSTANCE;
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            view.setDescriptionBackgroundAlpha(0f);
            flipperLayout.addFlipperView(view);
        }
        flipperLayout.setScrollTimeInSec(3);
        btnReplay.setOnClickListener(v -> flipperLayout.onCurrentPageChanged(0));
    }

    private void bindUI() {
        btnBack = findViewById(R.id.img_slider_back);
        btnReplay = findViewById(R.id.img_slider_replay);
        flipperLayout = (FlipperLayout) findViewById(R.id.flipper_layout);
    }
}