package com.hcmus.picbox.models;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * These classes are used to create a LocalDate object from milliseconds.
 */
public class PhotoModel {
    private File mImageFile;
    private LocalDate mLastModifiedDate;
    private String mName;
    private String thumb;

    public PhotoModel(String path) {
        this.mImageFile = new File(path);
        long date = mImageFile.lastModified();
        this.mLastModifiedDate = Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault()).toLocalDate();
        this.mName = mImageFile.getName();
    }

    public PhotoModel(File file) {
        this.mImageFile = file;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
