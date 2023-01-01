package com.hcmus.picbox.models;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class AbstractModel {

    public static final int TYPE_DATE = 0;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_GIF = 3;
    public static final int COUNT_TYPE = 4;

    public static final String GROUP_MODE_OPTION_1 = "None";
    public static final String GROUP_MODE_OPTION_2 = "Day";
    public static final String GROUP_MODE_OPTION_3 = "Month";
    public static final String GROUP_MODE_OPTION_4 = "Year";
    public static String groupMode = GROUP_MODE_OPTION_3; // 0: none, 1: day, 2: month, 3: year

    protected LocalDate mLastModifiedTime;

    public LocalDate getLastModifiedTime() {
        return mLastModifiedTime;
    }

    @NonNull
    public String getStringLastModifiedTime() {
        DateTimeFormatter formatter;
        switch (groupMode) {
            case GROUP_MODE_OPTION_2:
                formatter = DateTimeFormatter.ofPattern("'Ngày 'dd' tháng 'MM' năm 'yyyy");
                break;
            case GROUP_MODE_OPTION_4:
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
                (groupMode.equals(GROUP_MODE_OPTION_4) || mLastModifiedTime.getMonth() == otherDate.getMonth()) &&
                (!groupMode.equals(GROUP_MODE_OPTION_2) || mLastModifiedTime.getDayOfMonth() == otherDate.getDayOfMonth());
    }
}
