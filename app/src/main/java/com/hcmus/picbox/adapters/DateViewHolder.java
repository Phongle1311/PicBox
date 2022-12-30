package com.hcmus.picbox.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;

public class DateViewHolder extends RecyclerView.ViewHolder {

    public final TextView txt_date;

    public DateViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_date = itemView.findViewById(R.id.tv_date);
    }
}
