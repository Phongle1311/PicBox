package com.hcmus.picbox.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.hcmus.picbox.interfaces.IOnItemRangeInserted;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.models.PhotoModel;

import java.io.File;

public final class StorageUtils {

    private static IOnItemRangeInserted allMediaListener;
    private static IOnItemRangeInserted deviceAlbumListener;
    private static IOnItemRangeInserted userAlbumListener;

    public static void setAllMediaListener(IOnItemRangeInserted listener) {
        allMediaListener = listener;
    }

    public static void setDeviceAlbumListener(IOnItemRangeInserted listener) {
        deviceAlbumListener = listener;
    }

    public static void setUserAlbumListener(IOnItemRangeInserted listener) {
        userAlbumListener = listener;
    }

    public static void getAllPhotoFromStorage(Context context) {
        AlbumModel totalAlbum = MediaHolder.sTotalAlbum;
        AlbumHolder deviceAlbumList = AlbumHolder.getDeviceAlbumList();
        AlbumHolder userAlbumList = AlbumHolder.getUserAlbumList(); // tạm thời để đây, cái này không xài ở đây

        // Check device has SDCard or not
        if (android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED)) {

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PhotoModel.sProjection,
                    null, null, PhotoModel.sOrderBy + PhotoModel.sOrderDirection);

            if (cursor == null) return;

            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);

                // add media to allMediaList
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String data = cursor.getString(dataColumnIndex);
                File file = new File(data);

                PhotoModel media = new PhotoModel(file);
                totalAlbum.add(media);

                // add media to album or add new album to albumList
                // special case: all media in DCIM is belong to Camera album
                // such as: .../DCIM/Facebook/... or .../DCIM/Camera/....
                AlbumModel album;
                String albumName, albumID;

                if (data.contains("DCIM")) {
                    albumName = AlbumHolder.DCIM_DISPLAY_NAME;
                    albumID = AlbumHolder.DCIM_ID;
                }
                else {
                    dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    albumName = cursor.getString(dataColumnIndex);
                    dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                    albumID = cursor.getString(dataColumnIndex);
                }

                album = deviceAlbumList.getAlbumById(albumID);
                if (album == null) {
                    album = new AlbumModel(albumName, albumID, file.getParent());
                    deviceAlbumList.addAlbum(album);
                }

                album.add(media);
            }

            cursor.close();
        }

        if (allMediaListener != null)
            allMediaListener.onItemRangeInserted(0, totalAlbum.getCount());
        if (deviceAlbumListener != null)
            deviceAlbumListener.onItemRangeInserted(0, deviceAlbumList.size());
        if (userAlbumListener != null)
            userAlbumListener.onItemRangeInserted(0, userAlbumList.size());
    }
}
