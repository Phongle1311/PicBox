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
        Cursor cursor1 = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projections, MediaStore.MediaColumns.DATA + "=?", paths, null);

        Cursor cursor2 = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projections, MediaStore.MediaColumns.DATA + "=?", paths, null);


        long id;
        String[] contentUris = new String[paths.length];
        int i = 0;
        if (cursor1 != null) {
            if (cursor1.moveToFirst() && cursor1.getCount() > 0) {
                do {
                    id = cursor1.getLong(cursor1.getColumnIndexOrThrow(BaseColumns._ID));
                    contentUris[i++] = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            String.valueOf((int) id)).toString();
                } while (cursor1.moveToNext());
            }
            cursor1.close();
        }
        if (cursor2 != null) {
            if (cursor2.moveToFirst() && cursor2.getCount() > 0) {
                do {
                    id = cursor2.getLong(cursor2.getColumnIndexOrThrow(BaseColumns._ID));
                    contentUris[i++] = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            String.valueOf((int) id)).toString();
                } while (cursor2.moveToNext());
            }
            cursor2.close();
        }

        if (i == 0) return Result.failure();

        return Result.success(new Data.Builder()
                .putStringArray(KEY_CONTENT_URI, contentUris)
                .build());
    }
}
