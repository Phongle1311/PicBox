package com.hcmus.picbox.models;

import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Phong Le on 7/12/2022
 */
public class VideoModel extends MediaModel {

    // static
    public static final Uri sCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public static final String[] sProjection = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Thumbnails.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_ID
    }; // what need to load from an image
    public static String sOrderBy = MediaStore.Video.Media._ID;
    public static String sOrderDirection = " DESC";

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    // non-static
    private String thumbnail;
    private int duration = 0;

    public VideoModel(File file) {
        super(file);
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    protected VideoModel(Parcel in) {
        super(in);
        thumbnail = in.readString();
        duration = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(thumbnail);
        parcel.writeInt(duration);
    }

    @Override
    public int getType() {
        return TYPE_VIDEO;
    }
}
