package com.hcmus.picbox.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;

public class PhotoModel extends AbstractModel implements Parcelable {

    // what need to load from an image
    public static final String[] sProjection = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//            MediaStore.Images.Media.DATE_TAKEN,
//            MediaStore.Images.Media.MIME_TYPE,
//            MediaStore.Images.Media.SIZE,
//            MediaStore.Images.Media.ORIENTATION
    };
    public static String sOrderBy = MediaStore.Images.Media._ID;
    public static String sOrderDirection = " DESC";

    private File mImageFile;
    private String mName;

    public PhotoModel(File file) {
        this.mImageFile = file;
        long date = mImageFile.lastModified();
        this.mLastModifiedTime = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
        this.mName = mImageFile.getName();
    }

    public boolean checkExists() {
        return mImageFile.exists();
    }

    public File getFile() {
        return mImageFile;
    }

    @Override
    public int getType() {
        return TYPE_PHOTO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
    }

    protected PhotoModel(Parcel in) {
        mName = in.readString();
    }

    public static final Creator<PhotoModel> CREATOR = new Creator<PhotoModel>() {
        @Override
        public PhotoModel createFromParcel(Parcel in) {
            return new PhotoModel(in);
        }

        @Override
        public PhotoModel[] newArray(int size) {
            return new PhotoModel[size];
        }
    };
}
