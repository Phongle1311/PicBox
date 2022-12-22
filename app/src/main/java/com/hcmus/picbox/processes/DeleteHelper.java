package com.hcmus.picbox.processes;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.MediaModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * <h3>Delete media(s) (on other threads)</h3>
 * <ul>
 *     There are 3 way of deleting:
 *     <li>Move to trash bin, auto delete after 30 days by system (for API >= 30),
 *     media(s) can be recovered</li>
 *     <li>Delete permanently, media(s) can't be recovered</li>
 *     <li>Delete deeply, overwrite to can't be recovered by other apps</li>
 * </ul>
 * Created on 22/12/2022 by Phong Le
 */
public class DeleteHelper {

    public static final int DELETE_REQUEST_CODE = 1;

    public static void delete(List<MediaModel> mediaList, Context context) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(context);
        builder.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout dialogLayout = (LinearLayout) inflater.inflate(R.layout.dialog_delete_image_preview, null);
        builder.setView(dialogLayout);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((TextView) dialogLayout.findViewById(R.id.tv_header)).setText(String.format(Locale.getDefault(),
                "Do you want to delete %d file%s", mediaList.size(), mediaList.size() > 1 ? "s?" : "?"));
        dialogLayout.findViewById(R.id.btn_cancel).setOnClickListener(view -> alertDialog.dismiss());
        dialogLayout.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            List<Uri> uriList = new ArrayList<>();
            for (MediaModel media : mediaList) {
                Uri uri = getContentUriId(Uri.fromFile(media.getFile()), context);
                Collections.addAll(uriList, uri);
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    deleteAPI30(uriList, context);
                } else {
                    deleteAPI28(uriList, context);
                    Toast.makeText(context, "Image Deleted successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (IntentSender.SendIntentException e1) {
                e1.printStackTrace();
            }

            alertDialog.dismiss();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private static void deleteAPI30(List<Uri> uriList, Context context) throws
            IntentSender.SendIntentException {
        ContentResolver contentResolver = context.getContentResolver();
        PendingIntent pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
        ((Activity) context).startIntentSenderForResult(pendingIntent.getIntentSender(),
                DELETE_REQUEST_CODE, null, 0,
                0, 0, null);
    }

    private static void deleteAPI28(List<Uri> uriList, @NonNull Context context) {
        ContentResolver resolver = context.getContentResolver();
        for (Uri uri : uriList)
            resolver.delete(uri, null, null);
    }

    private static Uri getContentUriId(@NonNull Uri imageUri, @NonNull Context context) {
        String[] projections = {MediaStore.MediaColumns._ID};
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projections, MediaStore.MediaColumns.DATA + "=?",
                new String[]{imageUri.getPath()}, null);

        long id = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            }
            cursor.close();
        }
        return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                String.valueOf((int) id));
    }
}
