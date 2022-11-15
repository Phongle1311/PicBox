package com.hcmus.picbox.fragments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.DateItem;
import com.hcmus.picbox.adapters.GridItem;
import com.hcmus.picbox.adapters.PhotoAdapter;
import com.hcmus.picbox.adapters.PhotoItem;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.utils.PermissionUtils;
import com.hcmus.picbox.utils.StorageUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class PhotosFragment extends Fragment {

    private int mSpanCount = 4;
    private RecyclerView mGallery;
    private PhotoAdapter photoAdapter;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private List<PhotoModel> photoList = new ArrayList<>();
    private Map<LocalDate, List<PhotoModel>> photoByDays = new TreeMap<>(Collections.reverseOrder());
    private List<GridItem> inputItems = new ArrayList<>();
    private Context context;

    // Use registerForActivityResult instead of onRequestPermissionResult because
    // the old method is deprecated
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getPhotoList();
                } else {
                    Toast.makeText(context, "Permissions denied, Permissions are required to use the app..", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        context = view.getContext();
        mGallery = view.findViewById(R.id.rcv_images);

        // check permission
        if (PermissionUtils.checkPermissions(context, READ_EXTERNAL_STORAGE))
            getPhotoList();
        else if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            // TODO: show dialog to educate user and persuade user to grant permission
            Toast.makeText(context, "need to show rationale", Toast.LENGTH_LONG).show();
        } else
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);

        prepareRecyclerView();

        return view;
    }

    private void prepareRecyclerView() {
        photoAdapter = new PhotoAdapter(context, inputItems);

        GridLayoutManager manager = new GridLayoutManager(context, mSpanCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (photoAdapter.getItemViewType(position)) {
                    case GridItem.TYPE_DATE:
                        return manager.getSpanCount();
                    case GridItem.TYPE_PHOTO:
                        return 1;
                    default:
                        return -1;

                }
            }
        });
        mGallery.setLayoutManager(manager);
        mGallery.setAdapter(photoAdapter);
    }

    private void getPhotoList() {
        int oldSize = photoList.size();

        // get photo from storage
        List<PhotoModel> list = StorageUtils.getAllPhotoPathFromStorage(context);
        photoList.addAll(list); // don't assign photoList to anything, it will change adapter

        // group photo by date - add date items
        for (PhotoModel photo : photoList) {
            LocalDate lastModified = photo.getLastModifiedDate();
            YearMonth month = YearMonth.from(lastModified);
            lastModified = month.atDay(1);
            List<PhotoModel> groupList = photoByDays.computeIfAbsent(lastModified, k -> new ArrayList<>());
            groupList.add(photo);
        }

        for (LocalDate date : photoByDays.keySet()) {
            DateItem dateItem = new DateItem(date);
            inputItems.add(dateItem);
            for (PhotoModel photo : Objects.requireNonNull(photoByDays.get(date),
                    "Photo model list must not be null!")) {
                PhotoItem photoItem = new PhotoItem(photo);
                inputItems.add(photoItem);
            }
        }

        int newSize = photoList.size();
        photoAdapter.notifyItemRangeChanged(oldSize, newSize - oldSize);
    }
}
