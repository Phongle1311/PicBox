package com.hcmus.picbox.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.AlbumAdapter;
import com.hcmus.picbox.adapters.GridItem;
import com.hcmus.picbox.adapters.PhotoAdapter;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.DataHolder;
import com.hcmus.picbox.models.PhotoModel;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AlbumFragment extends Fragment {
    private CardView cv_favorite, cv_secret, cv_creativity, cv_trash;
    private ImageView scrollAlbum;
    private Context context;
    private AlbumAdapter albumAdapter;
    private List<AlbumModel> itemsList = new ArrayList<>();
    private RecyclerView mAlbums, mAlbums_from_device;
    private LinearLayoutManager linearManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        context = view.getContext();
        initUI(view);
        prepareRecyclerView();
        getAlbumListFromDevice();
        return view;
    }

    private void initUI(View view) {
        cv_favorite = view.findViewById(R.id.card_view_favorite);
        cv_secret = view.findViewById(R.id.card_view_secret);
        cv_creativity = view.findViewById(R.id.card_view_creativity);
        cv_trash = view.findViewById(R.id.card_view_trash);
        mAlbums = view.findViewById(R.id.rcv_album);
        mAlbums_from_device = view.findViewById(R.id.rcv_album_from_device);
        scrollAlbum = view.findViewById(R.id.img_scroll_album);
        scrollAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlbums.getLayoutManager().scrollToPosition(linearManager.findLastVisibleItemPosition() + 1);
            }
        });
    }

    private void prepareRecyclerView() {
        albumAdapter = new AlbumAdapter(context, itemsList);
        GridLayoutManager manager = new GridLayoutManager(context, 3);
        linearManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mAlbums_from_device.setLayoutManager(manager);
        mAlbums_from_device.setAdapter(albumAdapter);
        mAlbums.setLayoutManager(linearManager);
        mAlbums.setAdapter(albumAdapter);
    }

    private void getAlbumListFromDevice() {
        List<PhotoModel> list = DataHolder.getAllMediaList();
        Map<File, ArrayDeque<PhotoModel>> photoByFolder = new TreeMap<>();
        for (PhotoModel photo : list) {
            File folder = photo.getFile().getParentFile();
            ArrayDeque<PhotoModel> groupList = photoByFolder.computeIfAbsent(folder, k -> new ArrayDeque<>());
            groupList.addFirst(photo);
        }
        for (File folder : photoByFolder.keySet()) {
            List<PhotoModel> photoList = new ArrayList<>(photoByFolder.get(folder));
            AlbumModel album = new AlbumModel(folder.getName(), "1", folder.getPath());
            for (PhotoModel photo : photoList) {
                album.addMedia(photo);
            }
            itemsList.add(album);
        }
        albumAdapter.notifyDataSetChanged();
    }
}
