package com.hcmus.picbox.models;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class AbstractModel {

    public static final int TYPE_DATE = 0;
    public static final int TYPE_PHOTO = 1;

    public static int GROUP_BY = 1; // 0: day, 1: month, 2: year

    protected LocalDate mLastModifiedTime;

    public LocalDate getLastModifiedTime() {
        return mLastModifiedTime;
    }

    @NonNull
    public String getStringLastModifiedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Tháng 'MM 'năm 'yyyy");
        return mLastModifiedTime.format(formatter);
    }

    abstract public int getType();

    public boolean isTimeEqual(AbstractModel model) {
        LocalDate otherDate = model.getLastModifiedTime();
        return mLastModifiedTime.getYear() == otherDate.getYear() &&
                (GROUP_BY == 2 || mLastModifiedTime.getMonth() == otherDate.getMonth()) &&
                (GROUP_BY != 0 || mLastModifiedTime.getDayOfMonth() == otherDate.getDayOfMonth());
    }
}
