package com.hcmus.picbox.fragments;

import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_GROUP_MODE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_SPAN_COUNT;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.SecretActionModeCallback;
import com.hcmus.picbox.adapters.SecretMediaAdapter;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

import java.util.Objects;

public class SecretPhotoFragment extends Fragment {

    private Context context;
    private int mSpanCount;
    private RecyclerView mGallery;
    private SecretMediaAdapter mediaAdapter;
    private SecretActionModeCallback actionModeCallback;
    private ActionMode actionMode;

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

        int newSpanCount = SharedPreferencesUtils.getIntData(context, KEY_SPAN_COUNT);
        String newGroupMode = SharedPreferencesUtils.getStringData(context, KEY_GROUP_MODE);
        if (mediaAdapter != null) {
            mSpanCount = newSpanCount;
            AbstractModel.groupMode = newGroupMode;
            mediaAdapter.updateAll();
        }
    }

    private void initUI(View v) {
        mGallery = v.findViewById(R.id.rcv_images);
        v.findViewById(R.id.fab_main).setVisibility(View.GONE);
        v.findViewById(R.id.fab_search).setVisibility(View.GONE);
        v.findViewById(R.id.fab_secret_media).setVisibility(View.GONE);
        v.findViewById(R.id.fab_change_layout).setVisibility(View.GONE);
        v.findViewById(R.id.fab_sort_by).setVisibility(View.GONE);

        ImageView photoBackground = v.findViewById(R.id.fragment_photo_layout);
        String backgroundPath = SharedPreferencesUtils.getStringData(context, SharedPreferencesUtils.KEY_BACKGROUND_IMAGE);
        if (!Objects.equals(backgroundPath, "")) {
            Bitmap decodedBitmap = PhotoModel.getBitMap(context, backgroundPath);
            if (decodedBitmap != null) {
                photoBackground.setImageBitmap(decodedBitmap);
            }
        }
    }

    private void prepareRecyclerView() {
        // init adapter
        mediaAdapter = new SecretMediaAdapter(context,
                () -> actionMode = ((Activity) context).startActionMode(actionModeCallback));
        actionModeCallback = new SecretActionModeCallback(context, mediaAdapter);
        mediaAdapter.setActionModeCallback(actionModeCallback);
        mGallery.setAdapter(mediaAdapter);

        // init layout manager
        mSpanCount = SharedPreferencesUtils.getIntData(context, KEY_SPAN_COUNT);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, mSpanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mediaAdapter.getItemViewType(position)) {
                    case AbstractModel.TYPE_DATE:
                        return gridLayoutManager.getSpanCount();
                    case AbstractModel.TYPE_PHOTO:
                    case AbstractModel.TYPE_GIF:
                    case AbstractModel.TYPE_VIDEO:
                        return 1;
                    default:
                        return -1;

                }
            }
        });
        mGallery.setLayoutManager(gridLayoutManager);
    }

    public void finishDelete() {
        mediaAdapter.endSelection();
        actionMode.finish();
        Toast.makeText(context, this.getString(R.string.toast_delete_successfully)
                , Toast.LENGTH_SHORT).show();
    }
}
