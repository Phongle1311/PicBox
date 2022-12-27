package com.hcmus.picbox.works;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.hcmus.picbox.database.FavouritesDatabase;
import com.hcmus.picbox.database.MediaEntity;
import com.hcmus.picbox.interfaces.IOnItemRangeInserted;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.MediaModel;
import com.hcmus.picbox.models.PhotoModel;
import com.hcmus.picbox.models.VideoModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;
import com.hcmus.picbox.models.dataholder.MediaHolder;

import java.util.ArrayList;
import java.util.List;

public final class LoadStorageHelper {

    private static IOnItemRangeInserted allMediaListener;
    private static IOnItemRangeInserted deviceAlbumListener;
    private static IOnItemRangeInserted userAlbumListener;

    private static List<MediaEntity> favourites;

    public static void setMediasListener(String category, IOnItemRangeInserted listener) {
        switch (category) {
            case MediaHolder.KEY_TOTAL_ALBUM:
                allMediaListener = listener;
                break;
            default:
                break;
        }
    }

    public static void setDeviceAlbumListener(IOnItemRangeInserted listener) {
        deviceAlbumListener = listener;
    }

    public static void setUserAlbumListener(IOnItemRangeInserted listener) {
        userAlbumListener = listener;
    }

    public static void getAllMediaFromStorage(Context context) {
        // Get favourite mediaIds
        favourites = FavouritesDatabase.getInstance(context).favouriteDao().getAllFavorites();

        // Check device has SDCard or not
        if (android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED)) {

            List<PhotoModel> photoList = getAllPhotoFromStorage(context);
            List<VideoModel> videoList = getAllVideoFromStorage(context);

            AlbumModel totalAlbum = MediaHolder.sTotalAlbum;
            AlbumHolder deviceAlbumList = AlbumHolder.getDeviceAlbumList();
            AlbumHolder userAlbumList = AlbumHolder.getUserAlbumList(); // tạm thời để đây, cái này không xài ở đây

            // Two-pointer: merge two sorted-lists
            int photoIndex = 0;
            int videoIndex = 0;
            while (photoIndex < photoList.size() || videoIndex < videoList.size()) {
                MediaModel media;
                if (photoIndex == photoList.size() || (videoIndex < videoList.size() &&
                        photoList.get(photoIndex).getLastModifiedTime().isBefore(
                                videoList.get(videoIndex).getLastModifiedTime()))) {
                    media = videoList.get(videoIndex);
                    videoIndex++;
                } else {
                    media = photoList.get(photoIndex);
                    photoIndex++;
                }

                // check favourite
                media.setFavorite(isFavourite(media));
                if (media.isFavorite()) MediaHolder.sFavouriteAlbum.add(media);

                // add model to Total list
                totalAlbum.add(media);

                // add model to available album or create new album
                AlbumModel album = deviceAlbumList.getAlbumById(media.getAlbumId());
                if (album == null) {
                    album = new AlbumModel(media.getAlbumName(), media.getAlbumId(), media.getFile().getParent());
                    deviceAlbumList.addAlbum(album);
                }
                album.add(media);
            }


            if (allMediaListener != null)
                allMediaListener.onItemRangeInserted(0, totalAlbum.getCount());
            if (deviceAlbumListener != null)
                deviceAlbumListener.onItemRangeInserted(0, deviceAlbumList.size());
            if (userAlbumListener != null)
                userAlbumListener.onItemRangeInserted(0, userAlbumList.size());
        }
    }

    private static List<PhotoModel> getAllPhotoFromStorage(Context context) {
        List<PhotoModel> result = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                PhotoModel.sCollection, PhotoModel.sProjection,
                null, null, PhotoModel.sOrderBy + PhotoModel.sOrderDirection);

        if (cursor == null) return result;

        int count = cursor.getCount();
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int bucketDisplayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);

            // add media to allMediaList
            String path = cursor.getString(dataColumn);
            PhotoModel media = new PhotoModel(path);
            media.setMediaId(cursor.getInt(idColumn));

            if (path.contains("DCIM")) {
                media.setAlbumName(AlbumHolder.DCIM_DISPLAY_NAME);
                media.setAlbumId(AlbumHolder.DCIM_ID);
            } else {
                media.setAlbumName(cursor.getString(bucketDisplayNameColumn));
                media.setAlbumId(cursor.getString(bucketIdColumn));
            }
            result.add(media);
        }
        cursor.close();
        return result;
    }

    private static List<VideoModel> getAllVideoFromStorage(Context context) {
        List<VideoModel> result = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                VideoModel.sCollection, VideoModel.sProjection,
                null, null, VideoModel.sOrderBy + VideoModel.sOrderDirection);

        if (cursor == null) return result;

        int count = cursor.getCount();
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int bucketDisplayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);

            String path = cursor.getString(dataColumn);
            VideoModel media = new VideoModel(path);
            media.setMediaId(cursor.getInt(idColumn));
            media.setFavorite(MediaHolder.sFavouriteAlbum.getMediaList().contains(media));

            if (path.contains("DCIM")) {
                media.setAlbumName(AlbumHolder.DCIM_DISPLAY_NAME);
                media.setAlbumId(AlbumHolder.DCIM_ID);
            } else {
                media.setAlbumName(cursor.getString(bucketDisplayNameColumn));
                media.setAlbumId(cursor.getString(bucketIdColumn));
            }
            result.add(media);
        }
        cursor.close();
        return result;
    }

    private static boolean isFavourite(MediaModel model) {
        for (MediaEntity media : favourites)
            if (media.getMediaId() == model.getMediaId())
                return true;
        return false;
    }
}
