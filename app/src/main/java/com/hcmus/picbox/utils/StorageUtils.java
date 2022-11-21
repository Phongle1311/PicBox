package com.hcmus.picbox.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.DataHolder;
import com.hcmus.picbox.models.PhotoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class StorageUtils {

    public static void getAllPhotoPathFromStorage(Context context) {
        List<PhotoModel> allMediaList = DataHolder.getAllMediaList();
        List<AlbumModel> deviceAlbumList = DataHolder.getDeviceAlbumList();

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
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                File file = new File(cursor.getString(dataColumnIndex));
                allMediaList.add(new PhotoModel(file));
            }

            cursor.close();
        }
    }

    public static ArrayList<String> getImageDirectories(Context context) {
        ArrayList<String> directories = new ArrayList<>();

        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Images.Media.DATA
        };

        String includeImages = MediaStore.Images.Media.MIME_TYPE + " LIKE 'image/%' ";
        String excludeGif = " AND " + MediaStore.Images.Media.MIME_TYPE + " != 'image/gif' " + " AND " + MediaStore.Images.Media.MIME_TYPE + " != 'image/giff' ";
        String selection = includeImages + excludeGif;

        Cursor cursor = context.getContentResolver().query(queryUri, projection, selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int index = cursor.getColumnIndex(projection[0]);
                String photoUri = cursor.getString(index);
                if (!directories.contains(new File(photoUri).getParent()))
                    directories.add(new File(photoUri).getParent());
            }
            while (cursor.moveToNext());

            cursor.close();
        }


        return directories;
    }
}
