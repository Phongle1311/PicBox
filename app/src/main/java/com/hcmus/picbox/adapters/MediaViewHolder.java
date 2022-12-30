package com.hcmus.picbox.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;

public class MediaViewHolder extends RecyclerView.ViewHolder {

    public final View itemView;
    public final ImageView imageView;
    public final RadioButton rbSelect;

    public MediaViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        imageView = itemView.findViewById(R.id.img_card);
        rbSelect = itemView.findViewById(R.id.rb_select);
        rbSelect.setEnabled(false);
    }
}