package com.hcmus.picbox.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.MediaModel;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class SlideShowActivity extends AppCompatActivity {
    private ArrayList<MediaModel> selected_list;
    private ImageView btnBack;
    private ImageView btnReplay;
    private FlipperLayout flipperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        Bundle bundle = getIntent().getBundleExtra("media_selected_list");
        selected_list = bundle.getParcelableArrayList("SELECTED_LIST");
        btnBack = findViewById(R.id.img_slider_back);
        btnReplay=findViewById(R.id.img_slider_replay);
        btnBack.setOnClickListener(v -> finish());
        flipperLayout = (FlipperLayout) findViewById(R.id.flipper_layout);
        for (MediaModel selected_image : selected_list) {
            FlipperView view = new FlipperView(getBaseContext());
            Bitmap bm = BitmapFactory.decodeFile(selected_image.getPath());
            view.setImageBitmap(bm, new Function2<ImageView, Bitmap, Unit>() {
                @Override
                public Unit invoke(ImageView imageView, Bitmap bitmap) {
                    Glide
                            .with(getBaseContext())
                            .load(selected_image.getFile())
                            .placeholder(R.drawable.placeholder_color)
                            .error(R.drawable.placeholder_color)
                            .into(imageView);
                    return Unit.INSTANCE;
                }
            });
            view.setDescriptionBackgroundAlpha(0f);
            flipperLayout.setScrollTimeInSec(3);
            flipperLayout.addFlipperView(view);
//            btnReplay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    flipperLayout.startAutoCycle();
//                }
//            });
        }
    }
}