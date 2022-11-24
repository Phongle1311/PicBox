package com.hcmus.picbox.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.hcmus.picbox.interfaces.IOnItemRangeInserted;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.DataHolder;
import com.hcmus.picbox.models.PhotoModel;

import java.io.File;

public final class StorageUtils {

    private static IOnItemRangeInserted photoFragmentListener, albumFragmentListener;

    public static void setPhotoFragmentListener(IOnItemRangeInserted listener) {
        photoFragmentListener = listener;
    }

    public static void setAlbumFragmentListener(IOnItemRangeInserted listener) {
        albumFragmentListener = listener;
    }

    public static void getAllPhotoPathFromStorage(Context context) {
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

                // add to allMediaList
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String data = cursor.getString(dataColumnIndex);
                File file = new File(data);
                PhotoModel media = new PhotoModel(file);
                DataHolder.addMedia(media);

                // add to album or add new album to albumList
                dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                String albumName = cursor.getString(dataColumnIndex);
                dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                String albumID = cursor.getString(dataColumnIndex);

                // special case: all media in DCIM is belong to Camera album
                // such as: .../DCIM/Facebook/... or .../DCIM/Camera/....
                if (data.contains("DCIM")) {
                    if (DataHolder.containDeviceAlbumID(DataHolder.DCIM_ID)) {
                        DataHolder.addMediaToDeviceAlbumById(media, DataHolder.DCIM_ID);
                    }
                    else {
                        AlbumModel album = new AlbumModel(DataHolder.DCIM_DISPLAY_NAME, DataHolder.DCIM_ID, file.getParent());
                        album.addMedia(media);
                        DataHolder.addDeviceAlbum(album);
                    }
                }
                else if (DataHolder.containDeviceAlbumID(albumID)) {
                    DataHolder.addMediaToDeviceAlbumById(media, albumID);
                }
                else {
                    AlbumModel album = new AlbumModel(albumName, albumID, file.getParent());
                    album.addMedia(media);
                    DataHolder.addDeviceAlbum(album);
                }
            }

            cursor.close();
        }

        if (photoFragmentListener != null)
            photoFragmentListener.onItemRangeInserted(0, DataHolder.getAllMediaList().size());
        if (albumFragmentListener != null)
            albumFragmentListener.onItemRangeInserted(0, DataHolder.getDeviceAlbumList().size());
    }
}
