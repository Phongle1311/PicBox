package com.hcmus.picbox.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;

import java.net.MalformedURLException;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class SlideShowActivity extends AppCompatActivity {

    private ArrayList<String> selected_album_id;
    private ArrayList<Integer> selected_media_id;
    private ImageView btnBack;
    private ImageView btnReplay;
    private FlipperLayout flipperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        Bundle bundle = getIntent().getBundleExtra("media_selected_list");
        selected_album_id = bundle.getStringArrayList("selected_album_id");
        selected_media_id = bundle.getIntegerArrayList("selected_media_id");
        btnBack = findViewById(R.id.img_slider_back);
        btnReplay = findViewById(R.id.img_slider_replay);
        btnBack.setOnClickListener(v -> finish());
        flipperLayout = (FlipperLayout) findViewById(R.id.flipper_layout);

        for (int i = 0; i < selected_album_id.size(); i++) {
            MediaModel selected_image = AlbumHolder.sGetAlbumById(selected_album_id.get(i)).findMediaById(selected_media_id.get(i));
            FlipperView view = new FlipperView(getBaseContext());
            try {
                view.setImage(R.drawable.placeholder_color, new Function2<ImageView, Object, Unit>() {
                    @Override
                    public Unit invoke(ImageView imageView, Object o) {
                        Glide.with(getBaseContext()).load(selected_image.getFile()).placeholder(R.drawable.placeholder_color).error(R.drawable.placeholder_color).into(imageView);
                        return Unit.INSTANCE;
                    }
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
}