package com.hcmus.picbox.models;

public abstract class AbstractModel {

    public static final int TYPE_DATE = 0;
    public static final int TYPE_PHOTO = 1;

    abstract public int getType();
}
