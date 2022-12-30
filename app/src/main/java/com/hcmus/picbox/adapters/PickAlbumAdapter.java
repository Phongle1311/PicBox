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

public class PickAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<AlbumModel> albums;
    private IOnClickItem listener;

    public PickAlbumAdapter(Context context, List<AlbumModel> albums, IOnClickItem listener) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_album_choosing, parent, false);
        return new ChoosingAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AlbumModel album = albums.get(position);
        ChoosingAlbumViewHolder viewHolder = (ChoosingAlbumViewHolder) holder;

        viewHolder.album_name.setText(album.getDisplayName());

        viewHolder.itemView.setOnClickListener(view -> {
            if (listener != null)
                listener.onClickItem(album);
        });

        Glide.with(context)
                .load(album.getCover())
                .placeholder(R.drawable.placeholder_color)
                .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return albums == null ? 0 : albums.size();
    }

    public interface IOnClickItem {
        void onClickItem(AlbumModel album);
    }

    public static class ChoosingAlbumViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView album_name;
        private final View itemView;

        public ChoosingAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.img_album);
            album_name = itemView.findViewById(R.id.tv_album_name);
        }
    }
}
