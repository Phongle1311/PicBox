package com.hcmus.picbox.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;
import com.hcmus.picbox.R;
import com.hcmus.picbox.interfaces.IOnClickDetailBackButton;
import com.hcmus.picbox.models.PhotoModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is fragment for showing detail of media <br/>
 * Created on 27/11/2022 by Phong Le
 */
public class DisplayMediaFragment extends Fragment {

    // TODO hiện tại file này còn đang dang dở, sẽ thay đổi nhiều, nếu có làm liên quan đến file này thì nhớ hỏi
    private Context context;
    private PhotoModel model;
    private ImageView mImageView;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private IOnClickDetailBackButton backListener;
    private BottomNavigationView bottomBar;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private float mScaleFactor = 1.0f;

    public DisplayMediaFragment() {
    }

    public DisplayMediaFragment(PhotoModel model, IOnClickDetailBackButton backListener) {
        this.model = model;
        this.backListener = backListener;
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
            model = savedInstanceState.getParcelable("model");
        }

        // TODO: if model is photomodel (not video), set gone action repeat video

        bottomSheetBehavior = BottomSheetBehavior.from(
                view.findViewById(R.id.layout_detail_bottom_sheet));

        mImageView = view.findViewById(R.id.image_view);
        mImageView.setOnTouchListener((v, motionEvent) -> {
            scaleGestureDetector.onTouchEvent(motionEvent);
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        });
        bottomBar = view.findViewById(R.id.bottom_navigation_view_display_image);
        if (model.checkExists()) {
            Glide
                    .with(context)
                    .load(model.getFile())
                    .placeholder(R.drawable.placeholder_color)
                    .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                    .into(mImageView);
        }

        scaleGestureDetector = new ScaleGestureDetector(context, new DisplayMediaFragment.CustomizeScaleListener());
        gestureDetector = new GestureDetector(context, new CustomizeSwipeGestureListener());

        // Top app bar listener
        MaterialToolbar topAppBar = view.findViewById(R.id.top_app_bar);
        topAppBar.setNavigationOnClickListener(v -> backListener.onClickDetailBackButton());
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.ic_favourite) {
                // TODO: add/remove image to/from favourite album
                return true;
            } else if (item.getItemId() == R.id.ic_more) {
                // Show/hide bottom sheet
                toggleBottomSheet();
                return true;
            }
            return false;
        });

        bottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.favourite_display_image) {
                    return true;
                } else if (item.getItemId() == R.id.edit_display_image) {
                    return true;
                } else if (item.getItemId() == R.id.delete_display_image) {
                    return true;
                } else if (item.getItemId() == R.id.secret_display_image) {
                    return true;
                }
                return false;
            }
        });
        load(Uri.fromFile(model.getFile()), view);
    }

    // Need when change device configuration, such as when user rotates his phone
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("model", model);
        super.onSaveInstanceState(outState);
    }

    public void toggleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    // TODO: when and where should we load metadata? not here
    private void load(Uri uri, View view) {
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);
            if (in == null)
                return;

            ExifInterface exif = new ExifInterface(in);
            String datetime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
            String path = model.getFile().getPath();
            String size;
            float file_length = model.getFile().length();
            if (file_length >= 1024f) {
                file_length /= 1024f;
                if (file_length >= 1024f) {
                    file_length /= 1024f;
                    size = Math.round(file_length) + " MB";
                } else
                    size = Math.round(file_length) + " kB";
            } else
                size = Math.round(file_length) + " B";
            String dimensionX = exif.getAttribute(ExifInterface.TAG_PIXEL_X_DIMENSION);
            String dimensionY = exif.getAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION);
            String resolutionX = exif.getAttribute(ExifInterface.TAG_X_RESOLUTION);
            String resolutionY = exif.getAttribute(ExifInterface.TAG_Y_RESOLUTION);
            String resolutionUnit = exif.getAttribute(ExifInterface.TAG_RESOLUTION_UNIT);
            String deviceModel = exif.getAttribute(ExifInterface.TAG_MODEL);
            // TODO: get location -> map

            if (datetime != null)
                ((TextView) view.findViewById(R.id.tv_date_time)).setText(datetime);
            ((TextView) view.findViewById(R.id.tv_media_path)).setText(path);
            ((TextView) view.findViewById(R.id.tv_file_length)).setText(size);
            if (dimensionX != null && dimensionY != null)
                ((TextView) view.findViewById(R.id.tv_dimension)).setText(String.format("%s x %s",
                        dimensionX, dimensionY));
            if (resolutionX != null && resolutionY != null && resolutionUnit != null) {
                resolutionUnit = resolutionUnit.equals("2") ? "inches" : "cm";
                if (resolutionX.contains("/1"))
                    resolutionX = resolutionX.substring(0, resolutionX.length() - 2);
                if (resolutionY.contains("/1"))
                    resolutionY = resolutionY.substring(0, resolutionY.length() - 2);
                ((TextView) view.findViewById(R.id.tv_resolution)).setText(
                        String.format("%s x %s %s", resolutionX, resolutionY, resolutionUnit));
            }
            if (deviceModel != null && !deviceModel.equals("")) {
                String shutterSpeed = exif.getAttribute(ExifInterface.TAG_SHUTTER_SPEED_VALUE);
                String fNumber = exif.getAttribute(ExifInterface.TAG_F_NUMBER);
                String iso = exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY);

                ((TextView) view.findViewById(R.id.tv_device_model)).setText(deviceModel);
                if (shutterSpeed != null)
                    ((TextView) view.findViewById(R.id.tv_shutter_speed)).setText(shutterSpeed);
                if (fNumber != null)
                    ((TextView) view.findViewById(R.id.tv_focal_distance)).setText(fNumber);
                if (iso != null)
                    ((TextView) view.findViewById(R.id.tv_iso)).setText(String.format("ISO%s", iso));
            } else {
                view.findViewById(R.id.device_detail).setVisibility(View.GONE);
            }
        } catch (FileNotFoundException e) {
            Log.e("test", "loadMetadata -> file not found", e);
        } catch (IOException e) {
            Log.e("test", "loadMetadata -> IOException", e);
        }
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
//            return true;
            return super.onScale(scaleGestureDetector);
        }
    }

    /**
     * Listen and handle swipe event
     */
    private class CustomizeSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final float SWIPE_VELOCITY_THRESHOLD = 100f;

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
                               float velocityY) {
            // swipe from top to bottom
            if (event2.getY() - event1.getY() >= SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityY) >= SWIPE_VELOCITY_THRESHOLD) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                else
                    backListener.onClickDetailBackButton();
            }

            // swipe from bottom to top
            if (event1.getY() - event2.getY() >= SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityY) >= SWIPE_VELOCITY_THRESHOLD) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else
                    backListener.onClickDetailBackButton();
            }

            return true;
        }
    }
}
