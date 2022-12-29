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

        String[] contentUris = new String[paths.length];
        int i = 0;
        for (String path : paths) {
            String[] projections = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA};
            String[] selectionArgs = new String[]{path};

            Cursor cursor1 = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections,
                    MediaStore.MediaColumns.DATA + "=?", selectionArgs, null);

            Cursor cursor2 = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projections,
                    MediaStore.MediaColumns.DATA + "=?", selectionArgs, null);

            long id;
            if (cursor1 != null) {
                int idColumn = cursor1.getColumnIndexOrThrow(BaseColumns._ID);
                if (cursor1.moveToFirst() && cursor1.getCount() > 0) {
                    id = cursor1.getLong(idColumn);
                    contentUris[i++] = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            String.valueOf((int) id)).toString();
                }
                cursor1.close();
            }

            if (cursor2 != null) {
                int idColumn = cursor2.getColumnIndexOrThrow(BaseColumns._ID);
                if (cursor2.moveToFirst() && cursor2.getCount() > 0) {
                    id = cursor2.getLong(idColumn);
                    contentUris[i++] = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            String.valueOf((int) id)).toString();
                }
                cursor2.close();
            }
        }

        if (i == 0) return Result.failure();

        return Result.success(new Data.Builder()
                .putStringArray(KEY_CONTENT_URI, contentUris)
                .build());
    }
}
