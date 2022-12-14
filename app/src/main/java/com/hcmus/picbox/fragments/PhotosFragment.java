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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.MediaAdapter;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.utils.SharedPreferencesUtils;
import com.hcmus.picbox.utils.StorageUtils;

public class PhotosFragment extends Fragment {

    private final AlbumModel album;
    private Context context;
    private int mSpanCount;
    private int fabClicked = 0;
    private RecyclerView mGallery;
    private MediaAdapter photoAdapter;
    private FloatingActionButton fabMain, fabSearch, fabSecret, fabSortBy, fabChangeLayout;

    public PhotosFragment(String albumId) {
        album = AlbumHolder.sGetAlbumById(albumId);
        StorageUtils.setMediasListener(albumId, this::onItemRangeInserted);
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
        int newSpanCount = SharedPreferencesUtils.getIntData(context, "num_columns_of_row");
        if (newSpanCount != mSpanCount) {
            mSpanCount = newSpanCount;
            photoAdapter.notifyAll();
        }
    }

    private void initUI(View v) {
        mGallery = v.findViewById(R.id.rcv_images);
        fabMain = v.findViewById(R.id.fab_main);
        fabSearch = v.findViewById(R.id.fab_search);
        fabSecret = v.findViewById(R.id.fab_secret_media);
        fabChangeLayout = v.findViewById(R.id.fab_change_layout);
        fabSortBy = v.findViewById(R.id.fab_sort_by);

        fabMain.setOnClickListener(view -> {
            if (fabClicked == 0) {
                showMiniFABs();
            } else {
                hideMiniFABs();
            }
            fabClicked = ~fabClicked;
        });
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
        photoAdapter = new MediaAdapter(context, album);
        mSpanCount = SharedPreferencesUtils.getIntData(context, "num_columns_of_row");
        GridLayoutManager manager = new GridLayoutManager(context, mSpanCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (photoAdapter.getItemViewType(position)) {
                    case AbstractModel.TYPE_DATE:
                        return manager.getSpanCount();
                    case AbstractModel.TYPE_PHOTO:
                    case AbstractModel.TYPE_VIDEO:
                        return 1;
                    default:
                        return -1;

                }
            }
        });
        mGallery.setLayoutManager(manager);
        mGallery.setAdapter(photoAdapter);
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
        photoAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }
}
