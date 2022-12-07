package com.hcmus.picbox.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.File;

public class PhotoModel extends MediaModel implements Parcelable {

    // static
    public static final Uri sCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String[] sProjection = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//            MediaStore.Images.Media.MIME_TYPE,
//            MediaStore.Images.Media.SIZE,
//            MediaStore.Images.Media.ORIENTATION
    }; // what need to load from an image
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
    public static String sOrderBy = MediaStore.Images.Media._ID;
    public static String sOrderDirection = " DESC";

    // non-static
    public PhotoModel(File file) {
        super(file);
    }

    protected PhotoModel(Parcel in) {
        super(new File(in.readString()));
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
        parcel.writeString(mFile.getPath());
    }
}
