package com.hcmus.picbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.R;
import com.hcmus.picbox.activities.DisplayMediaActivity;
import com.hcmus.picbox.interfaces.IMediaAdapterCallback;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.DateModel;
import com.hcmus.picbox.models.MediaModel;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final AlbumModel album;
    public List<MediaModel> selectedMedia = new ArrayList<>();
    private boolean isSelecting = false;
    private IMediaAdapterCallback listener;
    private CustomActionModeCallback actionModeCallback;

    private static final float SCALE_X = 0.7f;
    private static final float SCALE_Y = 0.7f;

    public MediaAdapter(Context context, AlbumModel album) {
        this.context = context;
        this.album = album;
    }

    public void setCallback(IMediaAdapterCallback listener) {
        this.listener = listener;
    }

    public void setActionModeCallback(CustomActionModeCallback actionModeCallback) {
        this.actionModeCallback = actionModeCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case AbstractModel.TYPE_DATE: {
                View view = inflater.inflate(R.layout.date_layout, parent, false);
                return new DateViewHolder(view);
            }
            case AbstractModel.TYPE_PHOTO: {
                View view = inflater.inflate(R.layout.photo_card_layout, parent, false);
                return new MediaViewHolder(view);
            }
            case AbstractModel.TYPE_VIDEO: {
                View view = inflater.inflate(R.layout.video_card_layout, parent, false);
                return new MediaViewHolder(view);
            }
            case AbstractModel.TYPE_GIF: {
                View view = inflater.inflate(R.layout.gif_card_layout, parent, false);
                return new MediaViewHolder(view);
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
                break;
            }
            case AbstractModel.TYPE_GIF:
            case AbstractModel.TYPE_PHOTO:
            case AbstractModel.TYPE_VIDEO: {
                MediaModel model = (MediaModel) album.getModelList().get(position);
                MediaViewHolder viewHolder = (MediaViewHolder) holder;

                if (selectedMedia.contains(model)) {
                    viewHolder.imageView.setScaleX(SCALE_X);
                    viewHolder.imageView.setScaleY(SCALE_Y);
                    viewHolder.rbSelect.setChecked(true);
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
                viewHolder.itemView.setOnClickListener(view -> {
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

                viewHolder.itemView.setOnLongClickListener(view -> {
                    if (!isSelecting) {
                        isSelecting = true;
                        listener.onStartSelectMultiple();
                        onClickItem(viewHolder, model);
                        notifyDataSetChanged();
                        return true;
                    }
                    return false;
                });
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
        return album.getModelList().get(position).getType();
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
        deselectAll();
        isSelecting = false;
    }

    public void updateAll() {
        album.updateMediaList();
        album.updateModelList();
        notifyDataSetChanged();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final ImageView imageView;
        private final RadioButton rbSelect;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.img_card);
            rbSelect = itemView.findViewById(R.id.rb_select);
        }
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {

        private final TextView txt_date;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.tv_date);
        }
    }
}