package com.hcmus.picbox.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.PhotoModel;

/**
 * This is fragment of showing detail of media <br/>
 * Created on 27/11/2022 by Phong Le
 */
public class MediaSlidePageFragment extends Fragment {

    private Context context;
    private PhotoModel model;
    private ImageView mImageView;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;

    public MediaSlidePageFragment() {
    }

    public MediaSlidePageFragment(PhotoModel model) {
        this.model = model;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_slide_page, container, false);
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (model == null && savedInstanceState != null) {
            model = (PhotoModel) savedInstanceState.getParcelable("model");
        }

        mImageView = view.findViewById(R.id.image_view);
        mImageView.setOnTouchListener((v, motionEvent) -> {
            scaleGestureDetector.onTouchEvent(motionEvent);
            return true;
        });

        if (model.checkExists()) {
            Glide
                    .with(context)
                    .load(model.getFile())
                    .placeholder(R.drawable.placeholder_color)
                    .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                    .into(mImageView);
        }

        scaleGestureDetector = new ScaleGestureDetector(context, new MediaSlidePageFragment.CustomizeScaleListener());
    }

    // Need when change device configuration, such as when user rotates his phone
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("model", model);
        super.onSaveInstanceState(outState);
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
