package com.hcmus.picbox.adapter;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateItem extends GridItem {
    private final String mLastModifiedTime;

    public DateItem(@NonNull String LastModifiedTime) {
        this.mLastModifiedTime = LastModifiedTime;
    }

    public DateItem(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Tháng 'MM 'năm 'yyyy");
        this.mLastModifiedTime = date.format(formatter);
    }

    public String getDate() {
        return mLastModifiedTime;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
