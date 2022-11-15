package com.hcmus.picbox.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * This class is used for check and request one or many permission(s)
 * <p>Created on 14/11/2022</p>
 *
 * @author Phong Le
 */
public final class PermissionUtils {

    private static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        for (String permission : permissions)
            if (!checkPermission(context, permission))
                return false;
        return true;
    }

    public static void requestPermissions(Object o, int permissionId, String... permissions) {
        if (o instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) o, permissions, permissionId);
        }
    }
}