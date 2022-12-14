package com.hcmus.picbox.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.AlbumAdapter;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.utils.StorageUtils;

import java.util.List;

public class AlbumFragment extends Fragment {

    private Context context;
    private final List<AlbumModel> deviceAlbumList = AlbumHolder.getDeviceAlbumList().getList();
    private final List<AlbumModel> userAlbumList = AlbumHolder.getUserAlbumList().getList();
    private AlbumAdapter deviceAlbumAdapter;
    private AlbumAdapter userAlbumAdapter;
    private RecyclerView rcvUserAlbums;
    private RecyclerView rcvDeviceAlbum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        context = view.getContext();

        initUI(view);
        prepareRecyclerView();
        StorageUtils.setDeviceAlbumListener(this::onDeviceAlbumRangeInserted);
        StorageUtils.setUserAlbumListener(this::onUserAlbumRangeInserted);

        return view;
    }

    private void initUI(View view) {
        rcvUserAlbums = view.findViewById(R.id.rcv_user_album);
        rcvDeviceAlbum = view.findViewById(R.id.rcv_device_album);

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
