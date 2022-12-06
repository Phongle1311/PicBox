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
import com.hcmus.picbox.R;
import com.hcmus.picbox.activities.AlbumActivity;
import com.hcmus.picbox.models.AlbumModel;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<AlbumModel> items;

    public AlbumAdapter(Context context, List<AlbumModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.album_card_layout, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AlbumModel album = items.get(position);
        AlbumAdapter.AlbumViewHolder viewHolder = (AlbumAdapter.AlbumViewHolder) holder;

        Glide.with(context)
                .load(album.getCover())
                .placeholder(R.drawable.placeholder_color)
                .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                .into(viewHolder.imageView);

        StringBuilder albumName = new StringBuilder(album.getDisplayName());
        String albumSize = Integer.toString(album.getCount());
        if (albumName.length() + albumSize.length() > 14) {
            int lengthGet = 14 - albumSize.length();
            lengthGet--;
            if (lengthGet <= 2) {
                albumName = new StringBuilder();
                for (int i = 0; i < lengthGet; i++) {
                    albumName.append(".");
                }
            } else {
                String textShowed = albumName.substring(0, lengthGet - 2);
                textShowed += "...";
                albumName = new StringBuilder(textShowed);
            }
        }
        viewHolder.album_name.setText(albumName.toString());
        viewHolder.album_size.setText(albumSize);

        ((AlbumViewHolder) holder).imageView.setOnClickListener(view -> {
            Intent intent = new Intent(context, AlbumActivity.class);
            intent.putExtra("albumId", album.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView album_name, album_size;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_album_avatar);
            album_name = itemView.findViewById(R.id.txt_album_name);
            album_size = itemView.findViewById(R.id.txt_album_size);
        }
    }
}
