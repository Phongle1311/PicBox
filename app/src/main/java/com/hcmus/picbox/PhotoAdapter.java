package com.hcmus.picbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private List<GridItem> items= Collections.emptyList();
    public PhotoAdapter(Context context,List<GridItem> items){
        this.context=context;
        this.items=items;
    }
    public static class PhotoViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.img_card);
        }
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder{
        private final TextView txt_date;
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_date=(TextView) itemView.findViewById(R.id.txt_date);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        if(viewType==GridItem.TYPE_DATE){
            View view=inflater.inflate(R.layout.date_layout,parent,false);
            return new DateViewHolder(view);
        }
        else if(viewType==GridItem.TYPE_PHOTO){
            View view = inflater.inflate(R.layout.photo_card_layout, parent, false);
            return new PhotoViewHolder(view);
        }
        else{
            throw new IllegalStateException("unsupported type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type=getItemViewType(position);
        if(type==GridItem.TYPE_DATE){
            DateItem date=(DateItem) items.get(position);
            DateViewHolder viewHolder=(DateViewHolder) holder;
            viewHolder.txt_date.setText(date.getDate());
        }
        else if(type==GridItem.TYPE_PHOTO){
            PhotoItem photo=(PhotoItem) items.get(position);
            PhotoViewHolder viewHolder=(PhotoViewHolder) holder;
            Picasso.get().load(photo.getPhoto().getFile()).placeholder(R.drawable.ic_launcher_background).into(viewHolder.imageView);
        }
        else{
            throw new IllegalStateException("unsupported type");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }
}
