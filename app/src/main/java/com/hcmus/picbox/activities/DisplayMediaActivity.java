package com.hcmus.picbox.activities;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.PhotoModel;

/**
 * Created on 16/11/2022 by Phong Le
 */
public class DisplayMediaActivity extends AppCompatActivity {

    private ImageView mImageView;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_media);

        mImageView = findViewById(R.id.image_view);

        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        topAppBar.setNavigationOnClickListener(view -> finish());
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.ic_favourite) {
                // TODO: add/remove image to/from favourite album
                return true;
            } else if (item.getItemId() == R.id.ic_more) {
                // TODO: show bottom drawer (show detail)
                return true;
            }
            return false;
        });

        PhotoModel model = (PhotoModel) getIntent().getSerializableExtra("model");
        if (model.checkExists()) {
            Glide
                    .with(DisplayMediaActivity.this)
                    .load(model.getFile())
                    .placeholder(R.drawable.placeholder_color)
                    .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                    .into(mImageView);
        }

        scaleGestureDetector = new ScaleGestureDetector(this, new CustomizeScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    /**
     * Listen and handle scale event
     */
    private class CustomizeScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);
            return true;
        }
    }
}