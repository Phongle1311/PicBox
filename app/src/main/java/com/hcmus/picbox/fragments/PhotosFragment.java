package com.hcmus.picbox.fragments;

import static com.hcmus.picbox.activities.CreateAlbumActivity.KEY_ALBUM_NAME;
import static com.hcmus.picbox.activities.CreateAlbumActivity.KEY_CREATE_ALBUM_RESULT;
import static com.hcmus.picbox.activities.CreateAlbumActivity.KEY_HIDE_BUTTON_ADD_FILES;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmus.picbox.R;
import com.hcmus.picbox.activities.CreateAlbumActivity;
import com.hcmus.picbox.adapters.CustomActionModeCallback;
import com.hcmus.picbox.adapters.MediaAdapter;
import com.hcmus.picbox.components.ChooseAlbumDialog;
import com.hcmus.picbox.database.album.AlbumEntity;
import com.hcmus.picbox.database.album.AlbumMediaCrossRef;
import com.hcmus.picbox.database.album.AlbumsDatabase;
import com.hcmus.picbox.database.album.MediaEntity;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.utils.SharedPreferencesUtils;
import com.hcmus.picbox.works.LoadStorageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotosFragment extends Fragment {

    private final AlbumModel album;
    private Context context;
    private int mSpanCount;
    private int fabClicked = 0;
    private RecyclerView mGallery;
    private MediaAdapter mediaAdapter;
    private final ActivityResultLauncher<Intent> createAlbumActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Add new album to albumHolder and database
                            // In this case, user is prevented to add other files when create album

                            Intent data = result.getData();
                            if (data == null) return;
                            Bundle bundle = data.getBundleExtra(KEY_CREATE_ALBUM_RESULT);
                            String albumName = bundle.getString(KEY_ALBUM_NAME);
                            if (albumName == null) {
                                Toast.makeText(context, R.string.toast_error_create_album,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Add new album to database and data holder
                            long albumId = AlbumsDatabase.getInstance(context)
                                    .albumDao()
                                    .insertAlbum(new AlbumEntity(albumName));
                            AlbumModel newAlbum = new AlbumModel(albumName, String.valueOf(albumId));
                            AlbumHolder.getUserAlbumList().addAlbum(newAlbum);

                            addSelectedFilesToAlbum(newAlbum);
                        }
                    });
    private CustomActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private FloatingActionButton fabMain, fabSearch, fabSecret, fabSortBy, fabChangeLayout;

    public PhotosFragment(String albumId) {
        album = AlbumHolder.sGetAlbumById(albumId);
        LoadStorageHelper.setMediasListener(albumId, this::onItemRangeInserted);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        context = view.getContext();

        initUI(view);
        prepareRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int newSpanCount = SharedPreferencesUtils.getIntData(context, SharedPreferencesUtils.KEY_SPAN_COUNT);
        String newGroupMode = SharedPreferencesUtils.getStringData(context, SharedPreferencesUtils.KEY_GROUP_MODE);
//        if (newSpanCount != mSpanCount) {
        if (mediaAdapter != null) {
            mSpanCount = newSpanCount;
            AbstractModel.groupMode = newGroupMode;
            mediaAdapter.updateAll();
        }
    }

    private void initUI(View v) {
        mGallery = v.findViewById(R.id.rcv_images);
        fabMain = v.findViewById(R.id.fab_main);
        fabSearch = v.findViewById(R.id.fab_search);
        fabSecret = v.findViewById(R.id.fab_secret_media);
        fabChangeLayout = v.findViewById(R.id.fab_change_layout);
        fabSortBy = v.findViewById(R.id.fab_sort_by);
        ImageView photoBackground = v.findViewById(R.id.fragment_photo_layout);
        fabMain.setOnClickListener(view -> {
            if (fabClicked == 0) {
                showMiniFABs();
            } else {
                hideMiniFABs();
            }
            fabClicked = ~fabClicked;
        });
        String backgroundPath = SharedPreferencesUtils.getStringData(context, SharedPreferencesUtils.KEY_BACKGROUND_IMAGE);
        if (!Objects.equals(backgroundPath, "")) {
            Bitmap decodedBitmap = PhotoModel.getBitMap(context, backgroundPath);
            if (decodedBitmap != null) {
                Drawable ob = new BitmapDrawable(getResources(), decodedBitmap);
                photoBackground.setImageBitmap(decodedBitmap);
            }
        }
    }

    private void showMiniFABs() {
        fabSearch.show();
        fabSecret.show();
        fabChangeLayout.show();
        fabSortBy.show();
    }

    private void hideMiniFABs() {
        fabSearch.hide();
        fabSecret.hide();
        fabChangeLayout.hide();
        fabSortBy.hide();
    }

    private void prepareRecyclerView() {
        mediaAdapter = new MediaAdapter(context, album, () ->
                actionMode = ((Activity) context).startActionMode(actionModeCallback));
        actionModeCallback = new CustomActionModeCallback(context, mediaAdapter, this::onAddingToAlbum);
        mediaAdapter.setActionModeCallback(actionModeCallback);
        mSpanCount = SharedPreferencesUtils.getIntData(context, SharedPreferencesUtils.KEY_SPAN_COUNT);
        GridLayoutManager manager = new GridLayoutManager(context, mSpanCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mediaAdapter.getItemViewType(position)) {
                    case AbstractModel.TYPE_DATE:
                        return manager.getSpanCount();
                    case AbstractModel.TYPE_PHOTO:
                    case AbstractModel.TYPE_GIF:
                    case AbstractModel.TYPE_VIDEO:
                        return 1;
                    default:
                        return -1;

                }
            }
        });
        mGallery.setLayoutManager(manager);
        mGallery.setAdapter(mediaAdapter);
        mGallery.setOnTouchListener((view, motionEvent) -> {
            hideMiniFABs();
            fabClicked = 0;
            return false;
        });
        mGallery.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (fabMain.isShown()) {
                        hideMiniFABs();
                        fabMain.hide();
                        fabClicked = 0;
                    }
                } else if (dy < -50) {
                    if (!fabMain.isShown()) {
                        fabMain.show();
                        fabClicked = 0;
                    }
                } else if (!recyclerView.canScrollVertically(-1)) {
                    if (!fabMain.isShown()) {
                        fabMain.show();
                        fabClicked = 0;
                    }
                }
            }
        });
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
        mediaAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    public void finishDelete() {
        mediaAdapter.endSelection();
        actionMode.finish();
        Toast.makeText(context, this.getString(R.string.toast_delete_successfully)
                , Toast.LENGTH_SHORT).show();
    }

    public void onAddingToAlbum() {
        Dialog dialog = new ChooseAlbumDialog(context,
                new ChooseAlbumDialog.IChooseAlbumDialogCallback() {
                    @Override
                    public void onCreateAlbum() {
                        Intent intent = new Intent(context, CreateAlbumActivity.class);
                        intent.putExtra(KEY_HIDE_BUTTON_ADD_FILES, true);
                        createAlbumActivityResultLauncher.launch(intent);
                    }

                    @Override
                    public void onSelectAlbum(AlbumModel album) {
                        addSelectedFilesToAlbum(album);
                    }
                });
        dialog.show();
    }

    private void addSelectedFilesToAlbum(AlbumModel album) {
        // Get medias that haven't been along to album yet
        List<MediaModel> models = new ArrayList<>();
        List<MediaEntity> entities = new ArrayList<>();
        List<MediaModel> selectedMedia = mediaAdapter.selectedMedia;
        for (int i = 0, selectedMediaSize = selectedMedia.size(); i < selectedMediaSize; i++) {
            MediaModel model = selectedMedia.get(i);
            if (!album.getMediaList().contains(model)) {
                models.add(model);
                entities.add(new MediaEntity(model.getMediaId(), model.getPath()));
            }
        }

        if (models.size() == 0) return;

        AlbumsDatabase db = AlbumsDatabase.getInstance(context);

        // Insert medias to database (if it existed in db, replace it)
        db.albumDao().insertMedias(entities);

        // Insert cross-ref to database
        for (int i = 0, entitiesSize = entities.size(); i < entitiesSize; i++) {
            MediaEntity entity = entities.get(i);
            db.albumDao().insertAlbumMediaCrossRef(
                    new AlbumMediaCrossRef(Integer.parseInt(album.getId()),
                            entity.mediaId)
            );
        }

        // Insert to data holder
        album.addAll(models);
        mediaAdapter.endSelection();

        Toast.makeText(context, String.format(this.getString(R.string.toast_add_file_to_album),
                album.getDisplayName()), Toast.LENGTH_SHORT).show();
    }
}
