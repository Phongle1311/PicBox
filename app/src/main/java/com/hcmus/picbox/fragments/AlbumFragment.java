package com.hcmus.picbox.fragments;

import static com.hcmus.picbox.activities.CreateAlbumActivity.KEY_ALBUM_NAME;
import static com.hcmus.picbox.activities.CreateAlbumActivity.KEY_CREATE_ALBUM_RESULT;
import static com.hcmus.picbox.activities.ImagePasswordActivity.KEY_ENTER_PASSWORD_RESPONSE;
import static com.hcmus.picbox.activities.PickMediaActivity.KEY_SELECTED_ITEMS;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PASSWORD;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.hcmus.picbox.activities.AlbumActivity;
import com.hcmus.picbox.activities.CreateAlbumActivity;
import com.hcmus.picbox.activities.ImagePasswordActivity;
import com.hcmus.picbox.adapters.AlbumAdapter;
import com.hcmus.picbox.database.album.AlbumEntity;
import com.hcmus.picbox.database.album.AlbumMediaCrossRef;
import com.hcmus.picbox.database.album.AlbumsDatabase;
import com.hcmus.picbox.database.album.MediaEntity;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.utils.SharedPreferencesUtils;
import com.hcmus.picbox.works.LoadStorageHelper;

import java.util.List;
import java.util.Objects;

public class AlbumFragment extends Fragment {

    private final List<AlbumModel> deviceAlbumList = AlbumHolder.getDeviceAlbumList().getList();
    private final List<AlbumModel> userAlbumList = AlbumHolder.getUserAlbumList().getList();
    private Context context;
    private final ActivityResultLauncher<Intent> imagePasswordActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null &&
                            data.getBooleanExtra(KEY_ENTER_PASSWORD_RESPONSE, false)) {
                        Intent intent = new Intent(context, AlbumActivity.class);
                        intent.putExtra("albumId", MediaHolder.sSecretAlbum.getId());
                        context.startActivity(intent);
                    }
                }
            });
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

                            // Add selected media to album
                            if (selected == null) return;
                            // O(N^2) ??
                            for (int i = 0; i < selected.length; i++) {
                                if (!selected[i]) continue;
                                AbstractModel model = MediaHolder.sTotalAlbum.getModelList().get(i);
                                if (model.getType() == AbstractModel.TYPE_DATE) continue;
                                MediaModel media = (MediaModel) model;
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
                    }
            );
    private AlbumAdapter deviceAlbumAdapter;
    private AlbumAdapter userAlbumAdapter;
    private RecyclerView rcvUserAlbums;
    private RecyclerView rcvDeviceAlbum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        context = view.getContext();

        bindUI(view);
        setListener(view);
        prepareRecyclerView();

        LoadStorageHelper.setDeviceAlbumListener(this::onDeviceAlbumRangeInserted);
        LoadStorageHelper.setUserAlbumListener(this::onUserAlbumRangeInserted);

        setWallpaper(view);

        return view;
    }

    @Override
    public void onResume() {
        // Todo: check if delete image (high complexity ...)
        userAlbumAdapter.notifyItemRangeChanged(0, userAlbumList.size());
        super.onResume();
    }

    private void bindUI(View view) {
        rcvUserAlbums = view.findViewById(R.id.rcv_user_album);
        rcvDeviceAlbum = view.findViewById(R.id.rcv_device_album);


    }

    private void setWallpaper(View view) {
        ImageView albumBackground = view.findViewById(R.id.fragment_album_layout);
        String backgroundPath = SharedPreferencesUtils.getStringData(context, SharedPreferencesUtils.KEY_BACKGROUND_IMAGE);
        if (!Objects.equals(backgroundPath, "")) {
            Bitmap decodedBitmap = PhotoModel.getBitMap(context, backgroundPath);
            if (decodedBitmap != null) {
                Drawable ob = new BitmapDrawable(getResources(), decodedBitmap);
                albumBackground.setImageDrawable(ob);
            }
        }
    }

    private void setListener(View view) {
        // open favourite album
        view.findViewById(R.id.card_view_favorite).setOnClickListener(v -> {
            Intent intent = new Intent(context, AlbumActivity.class);
            intent.putExtra("albumId", MediaHolder.sFavouriteAlbum.getId());
            context.startActivity(intent);
        });

        // open secret album
        view.findViewById(R.id.card_view_secret).setOnClickListener(v -> {
            if (!SharedPreferencesUtils.checkKeyExist(context, KEY_PASSWORD)) {
                Toast.makeText(context, R.string.toast_have_not_create_password, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(context, ImagePasswordActivity.class);
                imagePasswordActivityResultLauncher.launch(intent);
            }
        });

        view.findViewById(R.id.card_view_creativity).setOnClickListener(v -> {
            // TODO: open album
        });

        view.findViewById(R.id.card_view_trash).setOnClickListener(v -> {
            // TODO: open album
        });

        view.findViewById(R.id.card_view_add_album).setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateAlbumActivity.class);
            createAlbumActivityResultLauncher.launch(intent);
        });
    }

    private void prepareRecyclerView() {
        deviceAlbumAdapter = new AlbumAdapter(context, deviceAlbumList);
        rcvDeviceAlbum.setAdapter(deviceAlbumAdapter);
        GridLayoutManager manager = new GridLayoutManager(context, 3);
        rcvDeviceAlbum.setLayoutManager(manager);

        userAlbumAdapter = new AlbumAdapter(context, userAlbumList);
        rcvUserAlbums.setAdapter(userAlbumAdapter);
        LinearLayoutManager linearManager = new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false);
        rcvUserAlbums.setLayoutManager(linearManager);
    }

    public void onDeviceAlbumRangeInserted(int positionStart, int itemCount) {
        deviceAlbumAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    public void onUserAlbumRangeInserted(int positionStart, int itemCount) {
        userAlbumAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }
}
