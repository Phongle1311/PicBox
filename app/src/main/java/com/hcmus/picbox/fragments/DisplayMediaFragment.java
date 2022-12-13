package com.hcmus.picbox.fragments;

import static android.Manifest.permission.ACCESS_MEDIA_LOCATION;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hcmus.picbox.R;
import com.hcmus.picbox.interfaces.IOnClickDetailBackButton;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.utils.PermissionUtils;
import com.hcmus.picbox.utils.PermissionUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is fragment for showing detail of media <br/>
 * Created on 27/11/2022 by Phong Le
 */
public class DisplayMediaFragment extends Fragment implements ExoPlayer.Listener, OnMapReadyCallback {
    // TODO hiện tại file này còn đang dang dở, sẽ thay đổi nhiều, nếu có làm liên quan đến file này thì nhớ hỏi
    private Context context;
    private MediaModel model;
    private long playbackPosition = 0;
    private float mScaleFactor = 1.0f;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private IOnClickDetailBackButton backListener;
    private MediaMetadataRetriever retriever;
    private ImageView imageView;
    private StyledPlayerView playerView;
    private ExoPlayer player;
    private TextView goToMap;
    private ProgressBar pbPlayer;
    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomBar;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private double[] latLong;
    private SupportMapFragment map;
    private LatLng position;
    public DisplayMediaFragment() {
    }

    public DisplayMediaFragment(MediaModel model, IOnClickDetailBackButton backListener) {
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

        initAttrs(view);

        if (model.getType() == AbstractModel.TYPE_PHOTO) {
            playerView.setVisibility(View.GONE);
            displayImage();
        } else if (model.getType() == AbstractModel.TYPE_VIDEO) {
            imageView.setVisibility(View.GONE);
            view.findViewById(R.id.action_repeat_video).setVisibility(View.GONE);
            displayVideo();
        }
        setTopAppBarListener();
        setBottomAppBarListener();
        loadExif(view);
    }

    // Need when change device configuration, such as when user rotates his phone
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("model", model);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (playerView != null)
            playerView.onResume();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (playerView != null)
            playerView.onResume();
        if (latLong != null) {
            position = new LatLng(latLong[0], latLong[1]);
            map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            map.getMapAsync(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (player != null)
            player.stop();
        if (playerView != null)
            playerView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            player.release();
        }
    }

    @Override
    public void onDestroy() {
        // release player
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        if (playerView != null) {
            playerView.setPlayer(null);
        }

        super.onDestroy();
    }

    private void initAttrs(@NonNull View view) {
        topAppBar = view.findViewById(R.id.top_app_bar);
        bottomBar = view.findViewById(R.id.bottom_navigation_view_display_image);
        imageView = view.findViewById(R.id.image_view);
        playerView = view.findViewById(R.id.exoplayer2_view);
        pbPlayer = view.findViewById(R.id.pb_player);
        goToMap = view.findViewById(R.id.tv_go_to_map);
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.layout_detail_bottom_sheet));
        scaleGestureDetector = new ScaleGestureDetector(context, new DisplayMediaFragment.CustomizeScaleListener());
        gestureDetector = new GestureDetector(context, new CustomizeSwipeGestureListener());
    }

    private void displayImage() {
        imageView.setOnTouchListener((v, motionEvent) -> {
            scaleGestureDetector.onTouchEvent(motionEvent);
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        });

        if (model.checkExists()) {
            Glide
                    .with(context)
                    .load(model.getFile())
                    .placeholder(R.drawable.placeholder_color)
                    .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                    .into(imageView);
        }
    }

    private void displayVideo() {
        player = new ExoPlayer.Builder(context)
                .setSeekBackIncrementMs(5000)
                .setSeekForwardIncrementMs(5000)
                .build();
        MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(model.getFile()));
        player.setMediaItem(mediaItem);
        player.addListener(this);
        player.setPlayWhenReady(true);
        playerView.setPlayer(player);
        playerView.setShowPreviousButton(false);
        playerView.setShowNextButton(false);
        player.seekTo(playbackPosition);
        player.prepare();
        player.play();
    }

    private void setTopAppBarListener() {
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
    }

    private void setBottomAppBarListener() {
        bottomBar.setOnItemSelectedListener(item ->
                item.getItemId() == R.id.favourite_display_image ||
                        item.getItemId() == R.id.edit_display_image ||
                        item.getItemId() == R.id.delete_display_image ||
                        item.getItemId() == R.id.secret_display_image);
    }

    public void toggleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    @Nullable
    private double[] extractVideoLocation(Uri videoUri) {
        try {
            retriever.setDataSource(context, videoUri);
        } catch (RuntimeException e) {
            Log.e("ERROR", "Cannot retrieve video file", e);
        }
        String locationString = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_LOCATION);
        if(locationString==null){
           return null;
        }
        else {
            String[] parts = locationString.split("\\+", -1);
            Log.d("HELLO", parts[1]);
            Log.d("HELLO", parts[2]);
            parts[2]=parts[2].substring(0,parts[2].length()-1);
            return new double[]{Double.parseDouble(parts[1]),Double.parseDouble(parts[2])};
        }
    }
    // TODO: when and where should we load metadata? not here
    private void loadExif(View view) {
        Uri uri = Uri.fromFile(model.getFile());
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
            map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            retriever = new MediaMetadataRetriever();
            if(model.getType() == AbstractModel.TYPE_VIDEO){
                latLong=extractVideoLocation(uri);
            }
            else{
                latLong = exif.getLatLong();
            }
            if (latLong != null&&map!=null) {
                position = new LatLng(latLong[0], latLong[1]);
                map.getMapAsync(this);
                goToMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callGoogleMap(position);
                    }
                });

            } else {
                view.findViewById(R.id.map).setVisibility(View.GONE);
                view.findViewById(R.id.tv_go_to_map).setVisibility(View.GONE);
                view.findViewById(R.id.txt_location).setVisibility(View.GONE);
            }
        } catch (FileNotFoundException e) {
            Log.e("test", "loadMetadata -> file not found", e);
        } catch (IOException e) {
            Log.e("test", "loadMetadata -> IOException", e);
        }
    }

    // Player listener
    @Override
    public void onPlayerError(@NonNull PlaybackException error) {
        Player.Listener.super.onPlayerError(error);
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        Player.Listener.super.onPlaybackStateChanged(playbackState);
        if (playbackState == Player.STATE_BUFFERING) {
            pbPlayer.setVisibility(View.VISIBLE);
            playerView.hideController();
        } else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED) {
            pbPlayer.setVisibility(View.INVISIBLE);
            playerView.showController();
        }

    }

    public void callGoogleMap(LatLng position) {
        String latitude = Double.toString(position.latitude);
        String longtitude = Double.toString(position.longitude);
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longtitude + "?q=" + latitude + "," + longtitude + "(" +
                latitude.substring(0, Math.min(latitude.length(), 9)) + ", " + longtitude.substring(0, Math.min(latitude.length(), 9)) + ")" + "?z=17");
        Uri gmmIntentUriWeb = Uri.parse("http://maps.google.com/maps?q=" + latitude + "," + longtitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUriWeb);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            startActivity(webIntent);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.addMarker(new MarkerOptions().position(this.position));
        CameraPosition cp = new CameraPosition.Builder().target(this.position).zoom(14).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    /**
     * Listen and handle scale event
     */
    private class CustomizeScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
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
