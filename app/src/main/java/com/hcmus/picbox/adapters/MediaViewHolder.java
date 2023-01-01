package com.hcmus.picbox.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;

public class MediaViewHolder extends RecyclerView.ViewHolder {

    public final View imageContainer;
    public final ImageView imageView;
    public final RadioButton rbSelect;
    public final TextView tvFileName;

    public MediaViewHolder(@NonNull View itemView) {
        super(itemView);
        this.imageContainer = itemView.findViewById(R.id.img_container);
        imageView = itemView.findViewById(R.id.img_card);
        rbSelect = itemView.findViewById(R.id.rb_select);
        tvFileName = itemView.findViewById(R.id.tv_file_path);
    }
}