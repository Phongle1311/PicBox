package com.hcmus.picbox.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.R;
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
        AlbumModel album = (AlbumModel) items.get(position);
        AlbumAdapter.AlbumViewHolder viewHolder = (AlbumAdapter.AlbumViewHolder) holder;
        Glide.with(context)
                .load(album.getCover())
                .placeholder(R.drawable.placeholder_color)
                .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                .into(viewHolder.imageView);
        String albumName = album.getDisplayName();
        String albumSize = Integer.toString(album.getSize());
        if (albumName.length() + albumSize.length() > 14) {
            int lengthGet = 14 - albumSize.length();
            lengthGet--;
            if (lengthGet <= 2) {
                albumName = "";
                for (int i = 0; i < lengthGet; i++) {
                    albumName += ".";
                }
            } else {
                String textShowed = albumName.substring(0, lengthGet - 2);
                textShowed += "...";
                albumName = textShowed;
            }
        }
        viewHolder.album_name.setText(albumName);
        viewHolder.album_size.setText(albumSize);
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
