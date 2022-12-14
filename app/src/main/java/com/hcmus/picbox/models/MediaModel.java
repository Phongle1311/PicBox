package com.hcmus.picbox.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Created by Phong Le on 7/12/2022
 */
public abstract class MediaModel extends AbstractModel implements Parcelable {

    protected File mFile;
    protected String mName;
    private String albumId;
    private String albumName;

    public MediaModel(File file) {
        this.mFile = file;
        long date = mFile.lastModified();
        this.mLastModifiedTime = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
        this.mName = mFile.getName();
    }

    public boolean checkExists() {
        return mFile.exists();
    }

    public File getFile() {
        return mFile;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    protected MediaModel(Parcel in) {
        this(new File(in.readString()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mFile.getPath());
    }
}
