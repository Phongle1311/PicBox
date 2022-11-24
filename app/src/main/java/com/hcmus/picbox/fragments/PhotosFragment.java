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
import com.hcmus.picbox.adapters.PhotoAdapter;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.DataHolder;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PhotosFragment extends Fragment {

    private final List<AbstractModel> itemsList = new ArrayList<>();
    private int mSpanCount;
    private RecyclerView mGallery;
    private PhotoAdapter photoAdapter;
    private Map<LocalDate, ArrayDeque<PhotoModel>> photoByDays = new TreeMap<>(Collections.reverseOrder());
    private Context context;
    private FloatingActionButton fabMain, fabSearch, fabSecret, fabSortBy, fabChangeLayout;
    private int fabClicked = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        context = view.getContext();

        initUI(view);
        prepareRecyclerView();
        getItemsList();

        DataHolder.setOnLoadFinishListener(this::onLoadFinish); // this line is used to avoid bugs, delete it after

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int newSpanCount = SharedPreferencesUtils.getIntData(context, "num_columns_of_row");
        if (newSpanCount != mSpanCount) {
            mSpanCount = newSpanCount;
            photoAdapter.notifyDataSetChanged();
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
        photoAdapter = new PhotoAdapter(context, itemsList);
        mSpanCount = SharedPreferencesUtils.getIntData(context, "num_columns_of_row");
        GridLayoutManager manager = new GridLayoutManager(context, mSpanCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (photoAdapter.getItemViewType(position)) {
                    case AbstractModel.TYPE_DATE:
                        return manager.getSpanCount();
                    case AbstractModel.TYPE_PHOTO:
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

    private void getItemsList() {
        int oldSize = itemsList.size();

        // get photo from data holder
        itemsList.addAll(DataHolder.getAllMediaList());

        // group photo by date - add date items
       /* for (AbstractModel photo : list) {
            LocalDate lastModified = photo.getLastModifiedDate();
            YearMonth month = YearMonth.from(lastModified);
            lastModified = month.atDay(1);
            ArrayDeque<PhotoModel> groupList = photoByDays.computeIfAbsent(lastModified, k -> new ArrayDeque<>());
            groupList.addFirst(photo);
        }

        for (LocalDate date : photoByDays.keySet()) {
            DateModel dateItem = new DateModel(date);
            itemsList.add(dateItem);
            for (PhotoModel photo : Objects.requireNonNull(photoByDays.get(date),
                    "Photo model list must not be null!")) {
                PhotoModel photoItem = new PhotoModel(photo);
                itemsList.add(photoItem);
            }
        }
*/
        int newSize = itemsList.size();
        photoAdapter.notifyItemRangeChanged(oldSize, newSize - oldSize);
//        photoAdapter.notifyDataSetChanged();
    }

    public void onListChanged(int positionStart, int itemCount) {
//        photoAdapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    public void onLoadFinish() {
        getItemsList();
    }
}
