package com.hcmus.picbox.works;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class GetContentUriIdWorker extends Worker {

    public static final String KEY_PATHS = "file_uri";
    public static final String KEY_CONTENT_URI = "content_uri";

    private final Context context;

    public GetContentUriIdWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String[] paths = getInputData().getStringArray(KEY_PATHS);
        if (paths == null)
            return Result.failure();

        String[] projections = {MediaStore.MediaColumns._ID};
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projections, MediaStore.MediaColumns.DATA + "=?", paths, null);


        long id;
        String[] contentUris = new String[paths.length];
        int i = 0;
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
                contentUris[i++] = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        String.valueOf((int) id)).toString();
            } while (cursor.moveToNext());
            cursor.close();
        }
        else return Result.failure();

        return Result.success(new Data.Builder()
                .putStringArray(KEY_CONTENT_URI, contentUris)
                .build());
    }
}
