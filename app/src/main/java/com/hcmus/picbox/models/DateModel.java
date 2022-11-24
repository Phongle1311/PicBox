package com.hcmus.picbox.models;

import java.time.LocalDate;

public class DateModel extends AbstractModel {

    public DateModel(LocalDate date) {
        mLastModifiedTime = date;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
