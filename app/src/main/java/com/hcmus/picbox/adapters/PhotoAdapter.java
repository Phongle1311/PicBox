package com.hcmus.picbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.activities.DisplayMediaActivity;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.DateModel;
import com.hcmus.picbox.models.PhotoModel;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<AbstractModel> items;

    public PhotoAdapter(Context context, List<AbstractModel> items) {
        this.context = context;
        this.items = items;
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
            return new PhotoViewHolder(view);
        } else {
            throw new IllegalStateException("unsupported type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == AbstractModel.TYPE_DATE) {
            DateModel date = (DateModel) items.get(position);
            DateViewHolder viewHolder = (DateViewHolder) holder;
            viewHolder.txt_date.setText(date.getStringLastModifiedTime());
        } else if (type == AbstractModel.TYPE_PHOTO) {
            PhotoModel photo = (PhotoModel) items.get(position);
            PhotoViewHolder viewHolder = (PhotoViewHolder) holder;

            Glide.with(context)
                    .load(photo.getFile())
                    .placeholder(R.drawable.placeholder_color)
                    .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                    .into(viewHolder.imageView);

            ((PhotoViewHolder) holder).imageView.setOnClickListener(view -> {
                Intent i = new Intent(context, DisplayMediaActivity.class);
                i.putExtra("model", photo);
                context.startActivity(i);
            });
        } else {
            throw new IllegalStateException("Unsupported type");
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || items == null || position >= items.size()) {
            throw new IllegalStateException("the position is invalid");
        }
        return items.get(position).getType();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
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