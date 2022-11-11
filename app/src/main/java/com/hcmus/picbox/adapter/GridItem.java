package com.hcmus.picbox.adapter;

public abstract class GridItem {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_PHOTO = 1;

    abstract public int getType();
}
