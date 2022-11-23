package com.hcmus.picbox.models;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateModel extends AbstractModel {
    private final String mLastModifiedTime;

    public DateModel(@NonNull String LastModifiedTime) {
        this.mLastModifiedTime = LastModifiedTime;
    }

    public DateModel(LocalDate date) {
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
