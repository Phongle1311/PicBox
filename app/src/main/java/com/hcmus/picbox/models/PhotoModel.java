package com.hcmus.picbox.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

public class PhotoModel extends MediaModel {

    // static
    public static final Uri sCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String[] sProjection = {
            MediaStore.Images.Media._ID, // unique ID in MediaStore
            MediaStore.Images.Media.DATA, // file path
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//            MediaStore.Images.Media.MIME_TYPE,
//            MediaStore.Images.Media.SIZE,
//            MediaStore.Images.Media.ORIENTATION
    }; // what need to load from an image
    public static final Creator<PhotoModel> CREATOR = new Creator<PhotoModel>() {
        @Override
        public PhotoModel createFromParcel(Parcel in) {
            return new PhotoModel(in);
        }

        @Override
        public PhotoModel[] newArray(int size) {
            return new PhotoModel[size];
        }
    };
    public static String sOrderBy = MediaStore.Images.Media._ID;
    public static String sOrderDirection = " DESC";

    // non-static
    public PhotoModel(String path) {
        super(path);
    }

    protected PhotoModel(Parcel in) {
        super(in.readString());
    }

    private static int calculateInSampleSize(
            @NonNull BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }

        return inSampleSize;
    }

    @Nullable
    public static Bitmap getBitMap(Context context, String path) {
        Bitmap decodedBitmap = null;
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels << 1;
            int height = displayMetrics.heightPixels;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            decodedBitmap = BitmapFactory.decodeFile(path, options);
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            int rotation = 0;
            if (orientation == 6) rotation = 90;
            else if (orientation == 3) rotation = 180;
            else if (orientation == 8) rotation = 270;
            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                Bitmap rotated = Bitmap.createBitmap(decodedBitmap, 0, 0, decodedBitmap.getWidth(), decodedBitmap.getHeight(), matrix, true);
                decodedBitmap.recycle();
                decodedBitmap = rotated;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decodedBitmap;
    }

    private boolean isGif() {
        return mFile.getPath().endsWith(".gif");
    }

    @Override
    public int getType() {
        return isGif() ? TYPE_GIF : TYPE_PHOTO;
    }
}
