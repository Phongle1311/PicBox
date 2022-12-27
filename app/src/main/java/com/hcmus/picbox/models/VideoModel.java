package com.hcmus.picbox.models;

import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;

/**
 * Created by Phong Le on 7/12/2022
 */
public class VideoModel extends MediaModel {

    // static
    public static final Uri sCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public static final String[] sProjection = {
            MediaStore.Video.Media._ID, // unique ID in MediaStore
            MediaStore.Video.Thumbnails.DATA, // file path of thumbnail
            MediaStore.Video.Media.DATA, // file path of video itself
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
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
    public  VideoModel(String path) {
        super(path);
    }

    protected VideoModel(Parcel in) {
        super(in);
    }

    @Override
    public int getType() {
        return TYPE_VIDEO;
    }
}
