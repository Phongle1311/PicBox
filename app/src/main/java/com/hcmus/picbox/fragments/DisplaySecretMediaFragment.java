package com.hcmus.picbox.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.ScreenSlidePagerAdapter;
import com.hcmus.picbox.interfaces.IOnClickDetailBackButton;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.MediaModel;

/**
 * This is fragment for showing detail of secret media <br/>
 * Created on 6/1/2023 by Phong Le
 */
public class DisplaySecretMediaFragment extends Fragment implements ExoPlayer.Listener {

    private Context context;
    private MediaModel model;
    private IOnClickDetailBackButton backListener;
    private int pos;
    private StyledPlayerView playerView;
    private ImageView imageView;
    private long playbackPosition = 0;
    private float mScaleFactor = 1.0f;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private ExoPlayer player;
    private ProgressBar pbPlayer;
    private MaterialToolbar topAppBar;

    public DisplaySecretMediaFragment() {
    }

    public DisplaySecretMediaFragment(MediaModel model, IOnClickDetailBackButton backListener,
                                      int position) {
        this.model = model;
        this.backListener = backListener;
        this.pos = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_secret_media_slide_page, container, false);
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (model == null && savedInstanceState != null) {
            model = savedInstanceState.getParcelable("model");
        }

        initAttributes(view);
        setTopAppbarListener();

        int type = model.getType();
        switch (type) {
            case AbstractModel.TYPE_PHOTO:
            case AbstractModel.TYPE_GIF:
                playerView.setVisibility(View.GONE);
                displayImage();
                break;
            case AbstractModel.TYPE_VIDEO:
                imageView.setVisibility(View.GONE);
                view.findViewById(R.id.action_repeat_video).setVisibility(View.GONE);
                displayVideo();
                break;
            default:
                throw new IllegalStateException("Unsupported type");
        }
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

    private void initAttributes(@NonNull View view) {
        imageView = view.findViewById(R.id.image_view);
        playerView = view.findViewById(R.id.exoplayer2_view);
        pbPlayer = view.findViewById(R.id.pb_player);
        topAppBar = view.findViewById(R.id.top_app_bar);

        scaleGestureDetector = new ScaleGestureDetector(context, new CustomizeScaleListener());
        gestureDetector = new GestureDetector(context, new CustomizeSwipeGestureListener());
    }

    private void setTopAppbarListener() {
        topAppBar.setNavigationOnClickListener(v -> backListener.onClickDetailBackButton());
        topAppBar.setOnMenuItemClickListener(item -> {
            // Add/remove model to/from database
            if (item.getItemId() == R.id.ic_delete) {
                //todo: delete in internal directory
                ScreenSlidePagerAdapter.deletePosition = pos;
                return true;
            } else if (item.getItemId() == R.id.ic_recover) {
                // todo: recover image to external
                return true;
            }
            return false;
        });
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
                backListener.onClickDetailBackButton();
            }

            // swipe from bottom to top
            if (event1.getY() - event2.getY() >= SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityY) >= SWIPE_VELOCITY_THRESHOLD) {
                backListener.onClickDetailBackButton();
            }

            return true;
        }
    }
}
