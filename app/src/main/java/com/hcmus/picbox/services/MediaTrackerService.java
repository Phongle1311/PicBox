package com.hcmus.picbox.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.models.VideoModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.models.dataholder.MediaHolder;

import java.io.File;

public class MediaTrackerService extends Service {

    public static final String ACTION_NAME = "com.hcmus.picbox.action.mediatracker";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // images
        getContentResolver().registerContentObserver(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true, new ContentObserver(new Handler()) {

                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);

                        MediaModel media = readFromMediaStore(getApplicationContext(),
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        Log.d("test", "path: " + media.getFile().getPath());
                        if (MediaHolder.sTotalAlbum.findMediaById(media.getMediaId()) == null) {
                            MediaHolder.sTotalAlbum.insert(media);
                            Intent intent = new Intent(ACTION_NAME);
                            intent.putExtra("add", 1);
                            sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        if (uri != null && !selfChange) {
                            Intent intent = new Intent(ACTION_NAME);
                            intent.putExtra("path", uri.getPath());
                            sendBroadcast(intent);
                        }
                        else
                            Log.d("test", "uri null");
                        super.onChange(selfChange, uri);
                    }
                });
        return START_STICKY;
    }

    private MediaModel readFromMediaStore(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, "date_added DESC");
        MediaModel media = null;
        if (cursor.moveToNext()) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int bucketDisplayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

            String filePath = cursor.getString(dataColumn);
            media = new PhotoModel(filePath);
            media.setMediaId(cursor.getInt(idColumn));

            if (filePath.contains("DCIM")) {
                media.setAlbumName(AlbumHolder.DCIM_DISPLAY_NAME);
                media.setAlbumId(AlbumHolder.DCIM_ID);
            } else {
                media.setAlbumName(cursor.getString(bucketDisplayNameColumn));
                media.setAlbumId(cursor.getString(bucketIdColumn));
            }
        }
        cursor.close();
        return media;
    }
}