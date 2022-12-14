package com.hcmus.picbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.R;
import com.hcmus.picbox.activities.DisplayMediaActivity;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.DateModel;
import com.hcmus.picbox.models.MediaModel;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final AlbumModel album;

    public MediaAdapter(Context context, AlbumModel album) {
        this.context = context;
        this.album = album;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == AbstractModel.TYPE_DATE) {
            View view = inflater.inflate(R.layout.date_layout, parent, false);
            return new DateViewHolder(view);
        } else if (viewType == AbstractModel.TYPE_PHOTO) {
            View view = inflater.inflate(R.layout.photo_card_layout, parent, false);
            return new MediaViewHolder(view);
        } else if (viewType == AbstractModel.TYPE_VIDEO) {
            View view = inflater.inflate(R.layout.video_card_layout, parent, false);
            return new MediaViewHolder(view);
        } else {
            throw new IllegalStateException("unsupported type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == AbstractModel.TYPE_DATE) {
            DateModel date = (DateModel) album.getModelList().get(position);
            DateViewHolder viewHolder = (DateViewHolder) holder;
            viewHolder.txt_date.setText(date.getStringLastModifiedTime());
        } else if (type == AbstractModel.TYPE_PHOTO || type == AbstractModel.TYPE_VIDEO) {
            MediaModel model = (MediaModel) album.getModelList().get(position);
            MediaViewHolder viewHolder = (MediaViewHolder) holder;

            // Load image by glide library
            Glide.with(context)
                    .load(model.getFile()) // todo: model.getThumbnail() in mediaModel
                    .placeholder(R.drawable.placeholder_color)
                    .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                    .into(viewHolder.imageView);

            // Set onClick Listener to display media
            viewHolder.imageView.setOnClickListener(view -> {
                Intent i = new Intent(context, DisplayMediaActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", album.getMediaList().indexOf(model));
                bundle.putString("category", album.getId());
                i.putExtra("model", bundle);
                context.startActivity(i);
            });
        } else {
            throw new IllegalStateException("Unsupported type");
        }
    }

    @Override
    public int getItemCount() {
        return album == null ? 0 : album.getModelList().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || album == null || position >= album.getModelList().size()) {
            throw new IllegalStateException("the position is invalid");
        }
        return album.getModelList().get(position).getType();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_card);
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