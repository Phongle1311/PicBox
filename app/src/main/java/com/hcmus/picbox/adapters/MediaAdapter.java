package com.hcmus.picbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.R;
import com.hcmus.picbox.activities.DisplayMediaActivity;
import com.hcmus.picbox.database.favorite.FavoriteEntity;
import com.hcmus.picbox.database.favorite.FavouritesDatabase;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.DateModel;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.works.DeleteHelper;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LAYOUT_MODE_1 = 1; // grid (default)
    public static final int LAYOUT_MODE_2 = 2; // list
    public static final int LAYOUT_MODE_3 = 3; // staggered
    private static final float SCALE_X = 0.7f;
    private static final float SCALE_Y = 0.7f;
    private static final int TYPE_ITEM_LIST = -1;
    private static final int TYPE_ITEM_STAGGERED_GRID = -2;
    private static final int TYPE_EMPTY = -3;
    private final Context context;
    private final AlbumModel album;
    private final IMediaAdapterCallback listener;
    public List<MediaModel> selectedMedia = new ArrayList<>();
    private boolean isSelecting = false;
    private CustomActionModeCallback actionModeCallback;
    private int layoutMode = LAYOUT_MODE_1;

    public MediaAdapter(Context context, AlbumModel album, int layoutMode, IMediaAdapterCallback listener) {
        this.context = context;
        this.album = album;
        if (layoutMode != 0)
            this.layoutMode = layoutMode;
        this.listener = listener;
    }

    public void setActionModeCallback(CustomActionModeCallback actionModeCallback) {
        this.actionModeCallback = actionModeCallback;
    }

    public void setLayoutMode(int layoutMode) {
        this.layoutMode = layoutMode;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case AbstractModel.TYPE_DATE: {
                View view = inflater.inflate(R.layout.item_date, parent, false);
                return new DateViewHolder(view);
            }
            case AbstractModel.TYPE_PHOTO: {
                View view = inflater.inflate(R.layout.item_grid_photo, parent, false);
                return new MediaViewHolder(view);
            }
            case AbstractModel.TYPE_VIDEO: {
                View view = inflater.inflate(R.layout.item_grid_video, parent, false);
                return new MediaViewHolder(view);
            }
            case AbstractModel.TYPE_GIF: {
                View view = inflater.inflate(R.layout.item_grid_gif, parent, false);
                return new MediaViewHolder(view);
            }
            case TYPE_ITEM_LIST: {
                View view = inflater.inflate(R.layout.item_list, parent, false);
                return new MediaViewHolder(view);
            }
            case TYPE_ITEM_STAGGERED_GRID: {
                View view = inflater.inflate(R.layout.item_staggered, parent, false);
                return new MediaViewHolder(view);
            }
            case TYPE_EMPTY: {
                return new DateViewHolder(new View(context)); // return empty layout
            }
            default:
                throw new IllegalStateException("unsupported type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case AbstractModel.TYPE_DATE: {
                DateModel date = (DateModel) album.getModelList().get(position);
                DateViewHolder viewHolder = (DateViewHolder) holder;
                viewHolder.txt_date.setText(date.getStringLastModifiedTime());
                return;
            }
            case TYPE_EMPTY: {
                DateViewHolder viewHolder = (DateViewHolder) holder;
                viewHolder.itemView.setVisibility(View.GONE);
                return;
            }
            case TYPE_ITEM_LIST:
            case TYPE_ITEM_STAGGERED_GRID:
            case AbstractModel.TYPE_GIF:
            case AbstractModel.TYPE_PHOTO:
            case AbstractModel.TYPE_VIDEO: {
                MediaModel model = (MediaModel) album.getModelList().get(position);
                if (model == null) return;
                MediaViewHolder viewHolder = (MediaViewHolder) holder;

                if (selectedMedia.contains(model)) {
                    viewHolder.imageView.setScaleX(SCALE_X);
                    viewHolder.imageView.setScaleY(SCALE_Y);
                    viewHolder.rbSelect.setChecked(true);
                } else {
                    viewHolder.imageView.setScaleX(1f);
                    viewHolder.imageView.setScaleY(1f);
                    viewHolder.rbSelect.setChecked(false);
                }

                if (isSelecting)
                    viewHolder.rbSelect.setVisibility(View.VISIBLE);
                else
                    viewHolder.rbSelect.setVisibility(View.GONE);

                // Load image by glide library
                Glide.with(context)
                        .load(model.getFile())
                        .placeholder(R.drawable.placeholder_color)
                        .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                        .into(viewHolder.imageView);

                // Set onClick Listener to display media
                viewHolder.imageContainer.setOnClickListener(view -> {
                    if (isSelecting) {
                        onClickItem(viewHolder, model);
                    } else {
                        Intent i = new Intent(context, DisplayMediaActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", album.getMediaList().indexOf(model));
                        bundle.putString("category", album.getId());
                        i.putExtra("model", bundle);
                        context.startActivity(i);
                    }
                });

                viewHolder.imageContainer.setOnLongClickListener(view -> {
                    if (!isSelecting) {
                        isSelecting = true;
                        listener.onStartSelectMultiple();
                        onClickItem(viewHolder, model);
                        notifyDataSetChanged();
                        return true;
                    }
                    return false;
                });

                if (type == TYPE_ITEM_LIST)
                    viewHolder.tvFileName.setText(model.getPath());

                break;
            }
            default:
                throw new IllegalStateException("Unsupported type");
        }
    }

    private void onClickItem(MediaViewHolder viewHolder, MediaModel model) {
        if (selectedMedia.contains(model)) {
            selectedMedia.remove(model);
            viewHolder.imageView.setScaleX(1f);
            viewHolder.imageView.setScaleY(1f);
            viewHolder.rbSelect.setChecked(false);
        } else {
            selectedMedia.add(model);
            viewHolder.imageView.setScaleX(SCALE_X);
            viewHolder.imageView.setScaleY(SCALE_Y);
            viewHolder.rbSelect.setChecked(true);
        }

        if (actionModeCallback != null)
            actionModeCallback.updateActionModeTitle();
    }

    @Override
    public int getItemCount() {
        return album == null ? 0 : album.getModelList().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || album == null || position >= album.getModelList().size()) {
            throw new IllegalStateException("The position is invalid");
        }

        int type = album.getModelList().get(position).getType();
        if (layoutMode == LAYOUT_MODE_1)
            return type;
        if (layoutMode == LAYOUT_MODE_2) {
            if (type == AbstractModel.TYPE_DATE)
                return type;
            return TYPE_ITEM_LIST;
        }
        if (type == AbstractModel.TYPE_DATE)
            return TYPE_EMPTY;
        return TYPE_ITEM_STAGGERED_GRID;
    }

    public void selectAll() {
        int i = 0;
        for (MediaModel media : album.getMediaList()) {
            if (!selectedMedia.contains(media)) {
                selectedMedia.add(media);
                notifyItemChanged(i);
            }
            i++;
        }
    }

    public void deselectAll() {
        selectedMedia.clear();
        notifyDataSetChanged();
    }

    public void endSelection() {
        selectedMedia.clear();
        isSelecting = false;
        deselectAll();
    }

    public void updateAll() {
        album.updateMediaList();
        album.updateModelList();
        notifyDataSetChanged();
    }

    public void addToFavoriteList() {
        List<FavoriteEntity> entities = new ArrayList<>();
        for (MediaModel media : selectedMedia) {
            if (!media.isFavorite()) {
                entities.add(new FavoriteEntity(media.getMediaId(), media.getPath()));
                media.setFavorite(true);
            }
        }
        new Thread(() -> FavouritesDatabase.getInstance(context)
                .favoriteDao().insertAll(entities)).start();
    }

    public void deleteAll() {
        DeleteHelper.delete(context, selectedMedia);
    }

    public interface IMediaAdapterCallback {
        void onStartSelectMultiple();
    }
}