package com.hcmus.picbox;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateItem extends GridItem{
    private String mLastModifiedTime;
    private final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("'Tháng 'MM 'năm 'yyyy");
    public DateItem(@NonNull String LastModifiedTime){
        this.mLastModifiedTime=LastModifiedTime;
    }
    public DateItem(LocalDate date){
        this.mLastModifiedTime=date.format(formatter);
    }
    public String getDate(){
        return mLastModifiedTime;
    }
    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
