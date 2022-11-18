package com.hcmus.picbox.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.hcmus.picbox.models.PhotoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class StorageUtils {

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

    private static final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");
    // All Secondary SD-CARDs (all exclude primary) separated by File.pathSeparator, i.e: ":", ";"
    private static final String SECONDARY_STORAGES = System.getenv("SECONDARY_STORAGE");
    // Primary emulated SD-CARD
    private static final String EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET");
    // PhysicalPaths based on phone model
    @SuppressLint("SdCardPath")
    @SuppressWarnings("SpellCheckingInspection")
    private static final String[] KNOWN_PHYSICAL_PATHS = new String[]{
            "/storage/sdcard0",
            "/storage/sdcard1",                 //Motorola Xoom
            "/storage/extsdcard",               //Samsung SGS3
            "/storage/sdcard0/external_sdcard", //User request
            "/mnt/extsdcard",
            "/mnt/sdcard/external_sd",          //Samsung galaxy family
            "/mnt/sdcard/ext_sd",
            "/mnt/external_sd",
            "/mnt/media_rw/sdcard1",            //4.4.2 on CyanogenMod S3
            "/removable/microsd",               //Asus transformer prime
            "/mnt/emmc",
            "/storage/external_SD",             //LG
            "/storage/ext_sd",                  //HTC One Max
            "/storage/removable/sdcard1",       //Sony Xperia Z1
            "/data/sdext",
            "/data/sdext2",
            "/data/sdext3",
            "/data/sdext4",
            "/sdcard1",                         //Sony Xperia Z
            "/sdcard2",                         //HTC One M8s
            "/storage/microsd"                  //ASUS ZenFone 2
    };

    /**
     * Returns all available storages in the system (include emulated)
     * <p/>
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standard way to get it.
     *
     * @return paths to all available storages in the system (include emulated)
     */
    public static String[] getStorageDirectories(Context context) {
        // Final set of paths
        final Set<String> availableDirectoriesSet = new HashSet<>();

        if (!TextUtils.isEmpty(EMULATED_STORAGE_TARGET)) {
            // Device has an emulated storage
            availableDirectoriesSet.add(getEmulatedStorageTarget());
        } else {
            // Device doesn't have an emulated storage
            availableDirectoriesSet.addAll(getExternalStorage(context));
        }

        // Add all secondary storages
        Collections.addAll(availableDirectoriesSet, getAllSecondaryStorages());

        String[] storagesArray = new String[availableDirectoriesSet.size()];
        return availableDirectoriesSet.toArray(storagesArray);
    }

    private static Set<String> getExternalStorage(Context context) {
        final Set<String> availableDirectoriesSet = new HashSet<>();
        // Solution of empty raw emulated storage for android version >= marshmallow
        // because the EXTERNAL_STORAGE become something like: "/Storage/A5F9-15F4",
        // so we can't access it directly
        File[] files = getExternalFilesDirs(context, null);
        for (File file : files) {
            if (file != null) {
                String applicationSpecificAbsolutePath = file.getAbsolutePath();
                String rootPath = applicationSpecificAbsolutePath.substring(
                        0,
                        applicationSpecificAbsolutePath.indexOf("Android/data")
                );
                availableDirectoriesSet.add(rootPath);
            }
        }

        return availableDirectoriesSet;
    }

    private static String getEmulatedStorageTarget() {
        String rawStorageId = "";
        // External storage paths should have storageId in the last segment
        // i.e: "/storage/emulated/storageId" where storageId is 0, 1, 2, ...
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String[] folders = path.split(File.separator);
        final String lastSegment = folders[folders.length - 1];
        if (!TextUtils.isEmpty(lastSegment) && TextUtils.isDigitsOnly(lastSegment)) {
            rawStorageId = lastSegment;
        }

        if (TextUtils.isEmpty(rawStorageId)) {
            return EMULATED_STORAGE_TARGET;
        } else {
            return EMULATED_STORAGE_TARGET + File.separator + rawStorageId;
        }
    }

    private static String[] getAllSecondaryStorages() {
        if (!TextUtils.isEmpty(SECONDARY_STORAGES)) {
            // All Secondary SD-CARDs split into array
            assert SECONDARY_STORAGES != null;
            return SECONDARY_STORAGES.split(File.pathSeparator);
        }
        return new String[0];
    }

    /**
     * Filter available physical paths from known physical paths
     *
     * @return List of available physical paths from current device
     */
    private static List<String> getAvailablePhysicalPaths() {
        List<String> availablePhysicalPaths = new ArrayList<>();
        for (String physicalPath : KNOWN_PHYSICAL_PATHS) {
            File file = new File(physicalPath);
            if (file.exists()) {
                availablePhysicalPaths.add(physicalPath);
            }
        }
        return availablePhysicalPaths;
    }

    /**
     * Returns absolute paths to application-specific directories on all
     * external storage devices where the application can place persistent files
     * it owns. These files are internal to the application, and not typically
     * visible to the user as media.
     * <p>
     * This is like {@link Context#getFilesDir()} in that these files will be
     * deleted when the application is uninstalled, however there are some
     * important differences:
     * <ul>
     * <li>External files are not always available: they will disappear if the
     * user mounts the external storage on a computer or removes it.
     * <li>There is no security enforced with these files.
     * </ul>
     * <p>
     * External storage devices returned here are considered a permanent part of
     * the device, including both emulated external storage and physical media
     * slots, such as SD cards in a battery compartment. The returned paths do
     * not include transient devices, such as USB flash drives.
     * <p>
     * An application may store data on any or all of the returned devices. For
     * example, an app may choose to store large files on the device with the
     * most available space, as measured by {@link android.os.StatFs}.
     * <p>
     * Starting in {@link android.os.Build.VERSION_CODES#KITKAT}, no permissions
     * are required to write to the returned paths; they're always accessible to
     * the calling app. Before then,
     * {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE} is required to
     * write. Write access outside of these paths on secondary external storage
     * devices is not available. To request external storage access in a
     * backwards compatible way, consider using {@code android:maxSdkVersion}
     * like this:
     *
     * <pre class="prettyprint">&lt;uses-permission
     *     android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     *     android:maxSdkVersion="18" /&gt;</pre>
     * <p>
     * The first path returned is the same as
     * {@link Context#getExternalFilesDir(String)}. Returned paths may be
     * {@code null} if a storage device is unavailable.
     *
     * @see Context#getExternalFilesDir(String)
     */
    private static File[] getExternalFilesDirs(Context context, String type) {
        return context.getExternalFilesDirs(type);
    }
}
