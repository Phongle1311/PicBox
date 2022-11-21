package com.hcmus.picbox.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.DataHolder;
import com.hcmus.picbox.models.PhotoModel;

import java.io.File;

public final class StorageUtils {

    public static void getAllPhotoPathFromStorage(Context context) {
        // Check device has SDCard or not
        if (android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED)) {

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PhotoModel.sProjection,
                    null, null, PhotoModel.sOrderBy);

            if (cursor == null) return;

            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);

                // add to allMediaList
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String data = cursor.getString(dataColumnIndex);
                File file = new File(data);
                PhotoModel photoModel = new PhotoModel(file);
                DataHolder.addMedia(photoModel);

                // add to album or add new album to albumList
                dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                String albumName = cursor.getString(dataColumnIndex);
                dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                String albumID = cursor.getString(dataColumnIndex);
                if (DataHolder.containDeviceAlbumID(albumID)) {
                    AlbumModel album = DataHolder.getDeviceAlbumById(albumID);
                    if (album != null)
                        album.addMedia(photoModel);
                } else {
                    AlbumModel album = new AlbumModel(albumName, albumID, file.getParent());
                    album.addMedia(photoModel);
                    DataHolder.addDeviceAlbum(album);
                }
            }

            cursor.close();
        }
    }
}
