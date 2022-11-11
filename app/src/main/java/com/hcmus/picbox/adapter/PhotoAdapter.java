package com.hcmus.picbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GridItem> items;

    public PhotoAdapter(List<GridItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == GridItem.TYPE_DATE) {
            View view = inflater.inflate(R.layout.date_layout, parent, false);
            return new DateViewHolder(view);
        } else if (viewType == GridItem.TYPE_PHOTO) {
            View view = inflater.inflate(R.layout.photo_card_layout, parent, false);
            return new PhotoViewHolder(view);
        } else {
            throw new IllegalStateException("unsupported type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == GridItem.TYPE_DATE) {
            DateItem date = (DateItem) items.get(position);
            DateViewHolder viewHolder = (DateViewHolder) holder;
            viewHolder.txt_date.setText(date.getDate());
        } else if (type == GridItem.TYPE_PHOTO) {
            PhotoItem photo = (PhotoItem) items.get(position);
            PhotoViewHolder viewHolder = (PhotoViewHolder) holder;
            Picasso.get().load(photo.getPhoto().getFile()).placeholder(R.drawable.placeholder_color).into(viewHolder.imageView);
        } else {
            throw new IllegalStateException("unsupported type");
        }
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
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
