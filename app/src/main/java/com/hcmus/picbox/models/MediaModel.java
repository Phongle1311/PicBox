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
    private int mediaId;
    private String path;
    private String albumId;
    private String albumName;
    private boolean isFavorite;

    public MediaModel(String path) {
        this.path = path;
        this.mFile = new File(path);
        long date = mFile.lastModified();
        this.mLastModifiedTime = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
        this.mName = mFile.getName();
    }

    protected MediaModel(Parcel in) {
        this(in.readString());
    }

    public boolean checkExists() {
        return mFile.exists();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File mFile) {
        this.mFile = mFile;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
    }

}
