package com.hcmus.picbox.fragments;

import static com.hcmus.picbox.activities.CreateAlbumActivity.KEY_ALBUM_NAME;
import static com.hcmus.picbox.activities.CreateAlbumActivity.KEY_CREATE_ALBUM_RESULT;
import static com.hcmus.picbox.activities.PickMediaActivity.KEY_SELECTED_ITEMS;
import static com.hcmus.picbox.works.CopyFileFromExternalToInternalWorker.KEY_INPUT_PATH;
import static com.hcmus.picbox.works.CopyFileFromExternalToInternalWorker.KEY_OUTPUT_DIR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.print.PrintHelper;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.hcmus.picbox.BuildConfig;
import com.hcmus.picbox.R;
import com.hcmus.picbox.activities.CreateAlbumActivity;
import com.hcmus.picbox.adapters.ScreenSlidePagerAdapter;
import com.hcmus.picbox.components.ChooseAlbumDialog;
import com.hcmus.picbox.database.album.AlbumEntity;
import com.hcmus.picbox.database.album.AlbumMediaCrossRef;
import com.hcmus.picbox.database.album.AlbumsDatabase;
import com.hcmus.picbox.database.album.MediaEntity;
import com.hcmus.picbox.database.favorite.FavoriteEntity;
import com.hcmus.picbox.database.favorite.FavouritesDatabase;
import com.hcmus.picbox.database.note.NoteDatabase;
import com.hcmus.picbox.database.note.NoteEntity;
import com.hcmus.picbox.interfaces.IOnClickDetailBackButton;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.utils.SharedPreferencesUtils;
import com.hcmus.picbox.works.CopyFileFromExternalToInternalWorker;
import com.hcmus.picbox.works.DeleteHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * This is fragment for showing detail of media <br/>
 * Created on 27/11/2022 by Phong Le
 */
public class DisplayMediaFragment extends Fragment implements ExoPlayer.Listener, OnMapReadyCallback {

    private Context context;
    private MediaModel model;
    private final ActivityResultLauncher<Intent> createAlbumActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Add new album to albumHolder and database

                            Intent data = result.getData();
                            if (data == null) return;
                            Bundle bundle = data.getBundleExtra(KEY_CREATE_ALBUM_RESULT);
                            String albumName = bundle.getString(KEY_ALBUM_NAME);
                            boolean[] selected = bundle.getBooleanArray(KEY_SELECTED_ITEMS);
                            if (albumName == null) {
                                Toast.makeText(context, R.string.toast_error_create_album,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            AlbumsDatabase db = AlbumsDatabase.getInstance(context);

                            // Add new album to database and data holder
                            long albumId = db.albumDao().insertAlbum(new AlbumEntity(albumName));
                            AlbumModel newAlbum = new AlbumModel(albumName, String.valueOf(albumId));
                            AlbumHolder.getUserAlbumList().addAlbum(newAlbum);

                            // Add media to database
                            newAlbum.add(model);
                            db.albumDao().insertMedia(
                                    new MediaEntity(model.getMediaId(), model.getPath())
                            );

                            // Add cross-ref to database
                            db.albumDao().insertAlbumMediaCrossRef(
                                    new AlbumMediaCrossRef((int) albumId, model.getMediaId())
                            );

                            if (selected == null) return;
                            // O(N^2) ??
                            for (int i = 0; i < selected.length; i++) {
                                if (!selected[i]) continue;
                                AbstractModel model = MediaHolder.sTotalAlbum.getModelList().get(i);
                                if (model.getType() == AbstractModel.TYPE_DATE) continue;
                                MediaModel media = (MediaModel) model;
                                if (media.getMediaId() == this.model.getMediaId()) continue;
                                // Add media to data holder
                                newAlbum.add(media);

                                // Add media to database
                                db.albumDao().insertMedia(
                                        new MediaEntity(media.getMediaId(), media.getPath())
                                );

                                // Add cross-ref
                                db.albumDao().insertAlbumMediaCrossRef(
                                        new AlbumMediaCrossRef((int) albumId, media.getMediaId())
                                );
                            }

                            Toast.makeText(context, "Add this file to album " +
                                            newAlbum.getDisplayName() + " successfully!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
    private long playbackPosition = 0;
    private float mScaleFactor = 1.0f;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private IOnClickDetailBackButton backListener;
    private int pos;
    private MediaMetadataRetriever retriever;
    private TextView btnUseFor;
    private ImageView imageView;
    private StyledPlayerView playerView;
    private NoteDatabase noteDB;
    private ImageView edit_note_icon;
    private ExoPlayer player;
    private EditText edit_note;
    private TextView goToMap;
    private TextView showLocation;
    private TextView tvMediaPath;
    private Bitmap decodedBitmap;
    private ProgressBar pbPlayer;
    private ImageView btnEditMediaName;
    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomBar;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private String original_note = "";
    private SupportMapFragment map;
    private LatLng position;
    private double[] latLong;
    private TextView btnPrint;
    private TextView btnAddToAlbum;

    public DisplayMediaFragment() {
    }

    public DisplayMediaFragment(MediaModel model, IOnClickDetailBackButton backListener,
                                int position) {
        this.model = model;
        this.backListener = backListener;
        this.pos = position;
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

        setTopAppBarListener();
        setBottomAppBarListener();
        setBottomSheetActionsListener();

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
        map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (latLong != null && map != null) {
            position = new LatLng(latLong[0], latLong[1]);
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
        showLocation = view.findViewById(R.id.tv_location);
        tvMediaPath = view.findViewById(R.id.tv_media_path);
        pbPlayer = view.findViewById(R.id.pb_player);
        goToMap = view.findViewById(R.id.tv_go_to_map);
        btnUseFor = view.findViewById(R.id.action_use_for);
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.layout_detail_bottom_sheet));
        scaleGestureDetector = new ScaleGestureDetector(context, new DisplayMediaFragment.CustomizeScaleListener());
        gestureDetector = new GestureDetector(context, new CustomizeSwipeGestureListener());
        map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        edit_note = view.findViewById(R.id.tv_add_note);
        edit_note_icon = view.findViewById(R.id.icon_edit_note);
        noteDB = NoteDatabase.getInstance(context);
        btnPrint = view.findViewById(R.id.action_print);
        retriever = new MediaMetadataRetriever();
        btnEditMediaName = view.findViewById(R.id.img_edit_file_name);
        btnAddToAlbum = view.findViewById(R.id.action_add_to_album);
        int type = model.getType();
        if (type != AbstractModel.TYPE_PHOTO) {
            btnUseFor.setVisibility(View.GONE);
            btnPrint.setVisibility(View.GONE);
        }
        if (model.isFavorite()) {
            topAppBar.getMenu().getItem(0).setIcon(R.drawable.ic_baseline_star_24);
        }
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
            // Add/remove model to/from database
            if (item.getItemId() == R.id.ic_favourite) {
                // Add/remove media to/from favourites
                if (model.isFavorite()) {
                    item.setIcon(R.drawable.ic_baseline_star_border_24);
                    model.setFavorite(false);
                    MediaHolder.sFavouriteAlbum.remove(model);
                    FavouritesDatabase.getInstance(context)
                            .favoriteDao()
                            .delete(new FavoriteEntity(model.getMediaId(), model.getPath()));
                } else {
                    item.setIcon(R.drawable.ic_baseline_star_24);
                    model.setFavorite(true);
                    MediaHolder.sFavouriteAlbum.insert(model);
                    FavouritesDatabase.getInstance(context)
                            .favoriteDao()
                            .insert(new FavoriteEntity(model.getMediaId(), model.getPath()));
                }
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
        bottomBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.share_display_image) {
                return true;
            } else if (itemId == R.id.edit_display_image) {
                return true;
            } else if (itemId == R.id.delete_display_image) {
                DeleteHelper.delete(context, model);
                ScreenSlidePagerAdapter.deletePosition = pos;
                return true;
            } else if (itemId == R.id.secret_display_image) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm to make it secret");
                builder.setMessage("This action will delete this file in your storage.\n If you delete this app, this file will be deleted permanently!")
                        .setCancelable(true)
                        .setPositiveButton("Confirm", (dialog, id) -> {
                            dialog.dismiss();
                            // Copy media file from external to dir in internal storage
                            String outputPath = context.getDir("secret_photos", Context.MODE_PRIVATE).getPath();
                            OneTimeWorkRequest copyFileRequest =
                                    new OneTimeWorkRequest.Builder(CopyFileFromExternalToInternalWorker.class)
                                            .setInputData(
                                                    new Data.Builder()
                                                            .putString(KEY_INPUT_PATH, model.getPath())
                                                            .putString(KEY_OUTPUT_DIR, outputPath)
                                                            .build()
                                            )
                                            .build();
                            WorkManager.getInstance(context).enqueue(copyFileRequest);

                            WorkManager.getInstance(context)
                                    .getWorkInfoByIdLiveData(copyFileRequest.getId())
                                    .observe((LifecycleOwner) context, info -> {
                                        if (info != null && info.getState() == WorkInfo.State.SUCCEEDED) {
                                            // delete image after copy
                                            DeleteHelper.deleteWithoutDialog(context, model);
                                            ScreenSlidePagerAdapter.deletePosition = pos;
                                        }
                                    });
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
            return false;
        });
    }

    private void setBottomSheetActionsListener() {
        btnEditMediaName.setOnClickListener(v -> {
            if (!Environment.isExternalStorageManager()) {
                Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                                "Permission needed!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", v1 -> {
                            try {
                                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                                startActivity(intent);
                            } catch (Exception ex) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivity(intent);
                            }
                        })
                        .show();
            } else {
                showEditFileNameDialog();
            }
        });

        setActionPrintListener();
        setActionUseForListener();
        setActionAddToAlbumListener();
    }

    public void setActionPrintListener() {
        btnPrint.setOnClickListener(v -> {
            PrintHelper photoPrinter = new PrintHelper(context);
            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            Bitmap bitmap = BitmapFactory.decodeFile(model.getFile().getAbsolutePath());
            photoPrinter.printBitmap("droids.jpg", bitmap);
        });
    }

    public void setActionUseForListener() {
        BottomSheetDialog dialogActionUseFor = new BottomSheetDialog(context);

        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_action_use_for, null);
        dialogActionUseFor.setContentView(view);

        TextView set_wallpaper = view.findViewById(R.id.txt_set_as_wallpaper);
        TextView set_background = view.findViewById(R.id.txt_set_as_background);
        decodedBitmap = PhotoModel.getBitMap(context, model.getFile().getAbsolutePath());

        set_wallpaper.setOnClickListener(v -> {
            try {
                if (!("").equals(model.getFile().getAbsolutePath()) && decodedBitmap != null) {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                    wallpaperManager.setBitmap(decodedBitmap);
                    dialogActionUseFor.hide();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        set_background.setOnClickListener(v -> {
            SharedPreferencesUtils.saveData(context, SharedPreferencesUtils.KEY_BACKGROUND_IMAGE,
                    model.getFile().getAbsolutePath());
            dialogActionUseFor.hide();
        });

        btnUseFor.setOnClickListener(v -> dialogActionUseFor.show());
    }

    private void showEditFileNameDialog() {
        Dialog dialogEditFileName = new Dialog(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_file_name, null);
        dialogEditFileName.setContentView(view);

        EditText edit_filename = view.findViewById(R.id.edit_text_file_name);
        TextView txt_extension = view.findViewById(R.id.txt_extension_file);
        Button btn_cancel = view.findViewById(R.id.btn_cancel_file_name);
        Button btn_save = view.findViewById(R.id.btn_save_file_name);

        String fileName = model.getFile().getName();
        int indexExtension = fileName.lastIndexOf(".");
        String extension = "." + fileName.substring(indexExtension + 1);
        if (indexExtension != -1) {
            txt_extension.setText(extension);
            edit_filename.setText(fileName.substring(0, indexExtension));
        } else {
            edit_filename.setText(fileName);
        }

        btn_cancel.setOnClickListener(v -> dialogEditFileName.dismiss());

        btn_save.setOnClickListener(v -> {
            String newFileName = edit_filename.getText().toString();
            if (newFileName.length() == 0) {
                Toast.makeText(context, "Filename can't be empty!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    String newName = newFileName + extension;
                    int type = model.getType();
                    if (type == AbstractModel.TYPE_GIF) {
                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl("file://" + model.getFile().getAbsolutePath());
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                        if (mimeType.equals("image/gif")) {
                            type = AbstractModel.TYPE_PHOTO;
                        } else if (mimeType.equals("video/gif")) {
                            type = AbstractModel.TYPE_VIDEO;
                        }
                    }

                    String oldPath = model.getFile().getAbsolutePath();
                    String newPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1) + newName;

                    if (type == AbstractModel.TYPE_PHOTO) {
                        Uri uri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, model.getMediaId());
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, newName);
                        values.put(MediaStore.Images.Media.TITLE, newFileName);
                        context.getContentResolver().update(uri, values,
                                MediaStore.Images.Media.DATA + "=?", new String[]{oldPath});
                        context.getContentResolver().notifyChange(uri, null);
                    } else if (type == AbstractModel.TYPE_VIDEO) {
                        Uri uri = ContentUris.withAppendedId(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, model.getMediaId());
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Video.Media.DISPLAY_NAME, newName);
                        values.put(MediaStore.Video.Media.TITLE, edit_filename.getText().toString());
                        context.getContentResolver().update(uri, values,
                                MediaStore.Video.Media.DATA + "=?", new String[]{oldPath});
                        context.getContentResolver().notifyChange(uri, null);
                    }

                    // Update UI
                    model.setPath(newPath);
                    tvMediaPath.setText(newPath);

                    Toast.makeText(context, "Rename file successfully.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("ERROR", e.toString());
                    e.printStackTrace();
                    Toast.makeText(context, "Rename file unsuccessfully.", Toast.LENGTH_SHORT).show();
                }
            }
            dialogEditFileName.dismiss();
        });
        dialogEditFileName.show();
    }

    private void setActionAddToAlbumListener() {
        btnAddToAlbum.setOnClickListener(view -> {
            Dialog dialog = new ChooseAlbumDialog(context,
                    new ChooseAlbumDialog.IChooseAlbumDialogCallback() {
                        @Override
                        public void onCreateAlbum() {
                            Intent intent = new Intent(context, CreateAlbumActivity.class);
                            createAlbumActivityResultLauncher.launch(intent);
                        }

                        @Override
                        public void onSelectAlbum(AlbumModel album) {
                            AlbumsDatabase db = AlbumsDatabase.getInstance(context);

                            // Insert media to database (if it existed in db, replace it)
                            db.albumDao().insertMedia(
                                    new MediaEntity(model.getMediaId(), model.getPath())
                            );

                            // Insert cross-ref to database
                            db.albumDao().insertAlbumMediaCrossRef(
                                    new AlbumMediaCrossRef(Integer.parseInt(album.getId()),
                                            model.getMediaId())
                            );

                            // Insert to data holder
                            album.add(model);

                            Toast.makeText(context, "Add this file to album " + album.getDisplayName()
                                    + " successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });
            dialog.show();
        });
    }

    private void toggleBottomSheet() {
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
        if (locationString == null) {
            return null;
        } else {
            String[] parts = locationString.split("\\+", -1);
            parts[2] = parts[2].substring(0, parts[2].length() - 1);
            return new double[]{Double.parseDouble(parts[1]), Double.parseDouble(parts[2])};
        }
    }

    private void toggleEditNoteAction() {
        NoteEntity noteEntity = noteDB.getItemDAO().getItemById(model.getMediaId());
        if (noteEntity != null && noteEntity.getNote().length() > 0) {
            original_note = noteEntity.getNote();
            edit_note.setText(original_note);
        }
        edit_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String note = edit_note.getText().toString();
                if (!note.equals(original_note)) {
                    edit_note_icon.setImageResource(R.drawable.ic_baseline_done_24);
                } else {
                    edit_note_icon.setImageResource(R.drawable.ic_baseline_edit_note_24);
                }
            }
        });
        edit_note_icon.setOnClickListener(v -> {
            String note = edit_note.getText().toString();
            if (!note.equals(original_note)) {
                if (noteEntity == null) {
                    noteDB.getItemDAO().insert(new NoteEntity(model.getMediaId(), note));
                } else {
                    noteDB.getItemDAO().update(new NoteEntity(model.getMediaId(), note));
                }
                edit_note_icon.setImageResource(R.drawable.ic_baseline_edit_note_24);
                edit_note.clearFocus();
            } else {
                edit_note.requestFocus();
                edit_note.setSelection(note.length());
            }
        });

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
            toggleEditNoteAction();
            if (datetime != null)
                ((TextView) view.findViewById(R.id.tv_date_time)).setText(datetime);
            tvMediaPath.setText(path);
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

            if (model.getType() == AbstractModel.TYPE_VIDEO) {
                latLong = extractVideoLocation(uri);
            } else {
                latLong = exif.getLatLong();
            }
            if (latLong != null && map != null) {
                position = new LatLng(latLong[0], latLong[1]);
                map.getMapAsync(this);
                goToMap.setOnClickListener(v -> callGoogleMap(position));
                showLocation.setText(getStringFromPosition(position));
            } else {
                view.findViewById(R.id.map).setVisibility(View.GONE);
                goToMap.setVisibility(View.GONE);
                view.findViewById(R.id.txt_location).setVisibility(View.GONE);
                showLocation.setVisibility(View.GONE);
            }
        } catch (FileNotFoundException e) {
            Log.e("test", "loadMetadata -> file not found", e);
        } catch (IOException e) {
            Log.e("test", "loadMetadata -> IOException", e);
        } catch (Exception e) {
            e.printStackTrace();
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

    // Google map method
    @NonNull
    private String getStringFromPosition(@NonNull LatLng position) {
        String latitude = Double.toString((double) Math.round(position.latitude * 100d) / 100d);
        String longitude = Double.toString((double) Math.round(position.longitude * 100d) / 100d);
        String result = latitude + ", " + longitude;

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            if (address != null)
                result = result + "\n" + address;
            else {
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                if (state != null && country != null)
                    result = result + "\n" + state + " " + country;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void callGoogleMap(LatLng position) {
        String latitude = Double.toString(position.latitude);
        String longitude = Double.toString(position.longitude);
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + ","
                + longitude + "(" + getStringFromPosition(position) + ")" + "?z=17");
        Uri gmmIntentUriWeb = Uri.parse("http://maps.google.com/maps?q=" + latitude + "," + longitude);
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
        CameraPosition cp = new CameraPosition.Builder().target(this.position).zoom(12).build();
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
