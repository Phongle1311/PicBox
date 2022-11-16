package com.hcmus.picbox.models;

import android.provider.MediaStore;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class PhotoModel implements Serializable {

    // what need to load from an image
    public static final String[] sProjection = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
//            MediaStore.Images.Media.DATE_TAKEN,
//            MediaStore.Images.Media.MIME_TYPE,
//            MediaStore.Images.Media.SIZE,
//            MediaStore.Images.Media.ORIENTATION
    };
    public static String sOrderBy = MediaStore.Images.Media._ID;

    private File mImageFile;
    private LocalDate mLastModifiedDate;
    private String mName;

    public PhotoModel(File file) {
        this.mImageFile = file;
        long date = mImageFile.lastModified();
        this.mLastModifiedDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
        this.mName = mImageFile.getName();
    }

    public boolean checkExists() {
        return mImageFile.exists();
    }

    public LocalDate getLastModifiedDate() {
        return mLastModifiedDate;
    }

    public File getFile() {
        return mImageFile;
    }


}
