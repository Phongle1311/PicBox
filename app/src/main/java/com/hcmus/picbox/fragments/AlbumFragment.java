package com.hcmus.picbox.fragments;

import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PASSWORD;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.hcmus.picbox.activities.ImagePasswordActivity;
import com.hcmus.picbox.activities.MainActivity;
import com.hcmus.picbox.adapters.AlbumAdapter;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.works.LoadStorageHelper;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

import java.util.List;
import java.util.Objects;

public class AlbumFragment extends Fragment {

    private final List<AlbumModel> deviceAlbumList = AlbumHolder.getDeviceAlbumList().getList();
    private final List<AlbumModel> userAlbumList = AlbumHolder.getUserAlbumList().getList();
    private Context context;
    private AlbumAdapter deviceAlbumAdapter;
    private AlbumAdapter userAlbumAdapter;
    private ImageView albumBackground;
    private RecyclerView rcvUserAlbums;
    private RecyclerView rcvDeviceAlbum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        context = view.getContext();

        initUI(view);
        prepareRecyclerView();
        LoadStorageHelper.setDeviceAlbumListener(this::onDeviceAlbumRangeInserted);
        LoadStorageHelper.setUserAlbumListener(this::onUserAlbumRangeInserted);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUI(View view) {
        view.findViewById(R.id.secretLayout).setOnClickListener(v->{
            if (!SharedPreferencesUtils.checkKeyExist(context, KEY_PASSWORD)){
                Toast.makeText(context, getResources().getString(R.string.require_password_for_view_image), Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(getActivity(), ImagePasswordActivity.class);
            ((MainActivity) requireActivity()).startActivity(intent);
        });

        rcvUserAlbums = view.findViewById(R.id.rcv_user_album);
        rcvDeviceAlbum = view.findViewById(R.id.rcv_device_album);
        albumBackground = view.findViewById(R.id.fragment_album_layout);
        String backgroundPath = SharedPreferencesUtils.getStringData(context, SharedPreferencesUtils.KEY_BACKGROUND_IMAGE);
        if (!Objects.equals(backgroundPath, "")) {
            Bitmap decodedBitmap = PhotoModel.getBitMap(context, backgroundPath);
            if (decodedBitmap != null) {
                Drawable ob = new BitmapDrawable(getResources(), decodedBitmap);
                albumBackground.setImageDrawable(ob);
            }
        }
        view.findViewById(R.id.card_view_favorite).setOnClickListener(v -> {
            // TODO: open album
        });

        view.findViewById(R.id.card_view_secret).setOnClickListener(v -> {
            // TODO: open album
        });

        view.findViewById(R.id.card_view_creativity).setOnClickListener(v -> {
            // TODO: open album
        });

        view.findViewById(R.id.card_view_trash).setOnClickListener(v -> {
            // TODO: open album
        });
    }

    private void prepareRecyclerView() {
        deviceAlbumAdapter = new AlbumAdapter(context, deviceAlbumList);
        rcvDeviceAlbum.setAdapter(deviceAlbumAdapter);
        GridLayoutManager manager = new GridLayoutManager(context, 3);
        rcvDeviceAlbum.setLayoutManager(manager);

        userAlbumAdapter = new AlbumAdapter(context, userAlbumList);
        rcvUserAlbums.setAdapter(userAlbumAdapter);
        LinearLayoutManager linearManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rcvUserAlbums.setLayoutManager(linearManager);
    }

    public void onDeviceAlbumRangeInserted(int positionStart, int itemCount) {
        deviceAlbumAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    public void onUserAlbumRangeInserted(int positionStart, int itemCount) {
        userAlbumAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }
}
