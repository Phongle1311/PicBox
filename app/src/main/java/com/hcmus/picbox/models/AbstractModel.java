package com.hcmus.picbox.models;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class AbstractModel {

    public static final int TYPE_DATE = 0;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_GIF = 3;

    public static int GROUP_BY = 2; // 0: none, 1: day, 2: month, 3: year

    protected LocalDate mLastModifiedTime;

    public LocalDate getLastModifiedTime() {
        return mLastModifiedTime;
    }

    @NonNull
    public String getStringLastModifiedTime() {
        DateTimeFormatter formatter;
        switch(GROUP_BY) {
            case 1:
                formatter = DateTimeFormatter.ofPattern("'Ngày 'dd' tháng 'MM' năm 'yyyy");
                break;
            case 3:
                formatter = DateTimeFormatter.ofPattern("'Năm 'yyyy");
                break;
            default:
                formatter = DateTimeFormatter.ofPattern("'Tháng 'MM' năm 'yyyy");
        }
        return mLastModifiedTime.format(formatter);
    }

    abstract public int getType();

    public boolean isTimeEqual(AbstractModel model) {
        LocalDate otherDate = model.getLastModifiedTime();
        return mLastModifiedTime.getYear() == otherDate.getYear() &&
                (GROUP_BY == 3 || mLastModifiedTime.getMonth() == otherDate.getMonth()) &&
                (GROUP_BY != 1 || mLastModifiedTime.getDayOfMonth() == otherDate.getDayOfMonth());
    }
}
