package com.hcmus.picbox.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.hcmus.picbox.models.PhotoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StorageUtils {

    public static List<PhotoModel> getAllPhotoPathFromStorage(Context context) {
        List<PhotoModel> result = new ArrayList<>();

        // Check device has SDCard or not
        if (android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED)) {

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PhotoModel.sProjection,
                    null, null, PhotoModel.sOrderBy);

            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                File file = new File(cursor.getString(dataColumnIndex));
                result.add(new PhotoModel(file));
            }

            cursor.close();
        }

        return result;
    }
}
