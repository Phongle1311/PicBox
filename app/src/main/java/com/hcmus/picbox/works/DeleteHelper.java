package com.hcmus.picbox.works;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.picbox.R;
import com.hcmus.picbox.models.MediaModel;

import java.util.ArrayList;
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

    public static void delete(Context context, MediaModel media) {
        List<MediaModel> modelList = new ArrayList<>();
        modelList.add(media);
        delete(context, modelList);
    }

    public static void delete(Context context, List<MediaModel> mediaList) {
        // Declare work request
        String[] paths = new String[mediaList.size()];
        int index = 0;
        for (MediaModel media : mediaList) {
            paths[index++] = Uri.fromFile(media.getFile()).getPath();
        }

        OneTimeWorkRequest getContentUriIdWorkRequest =
                new OneTimeWorkRequest.Builder(GetContentUriIdWorker.class)
                        .setInputData(
                                new Data.Builder()
                                        .putStringArray(GetContentUriIdWorker.KEY_PATHS, paths)
                                        .build()
                        )
                        .build();

        // UI
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(context);
        builder.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout dialogLayout = (LinearLayout) inflater.inflate(R.layout.dialog_delete_image_preview, null);
        builder.setView(dialogLayout);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        RadioGroup rgTypeOfDelete = dialogLayout.findViewById(R.id.rg_type_of_delete);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            rgTypeOfDelete.check(R.id.rb_move_to_trash_bin);
        } else {
            RadioButton rbMoveToTrashBin = dialogLayout.findViewById(R.id.rb_move_to_trash_bin);
            rbMoveToTrashBin.setEnabled(false);
            rgTypeOfDelete.check(R.id.rb_delete_permanently);
        }

        rgTypeOfDelete.setOnCheckedChangeListener((radioGroup, i) -> {
            TextView tvHelper = dialogLayout.findViewById(R.id.tv_helper_text);
            if (i == R.id.rb_move_to_trash_bin)
                tvHelper.setText(R.string.helper_text_move_to_trash_bin);
            else if (i == R.id.rb_delete_permanently)
                tvHelper.setText(R.string.helper_text_delete_permanently);
            else
                tvHelper.setText(R.string.helper_text_delete_deeply);
        });

        ((TextView) dialogLayout.findViewById(R.id.tv_header))
                .setText(String.format(Locale.getDefault(), "Do you want to delete %d file%s",
                        mediaList.size(), mediaList.size() > 1 ? "s?" : "?"));
        dialogLayout.findViewById(R.id.btn_cancel).setOnClickListener(view -> alertDialog.dismiss());
        dialogLayout.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            int selectedId = rgTypeOfDelete.getCheckedRadioButtonId();
            WorkManager.getInstance(context).enqueue(getContentUriIdWorkRequest);

            if (selectedId == R.id.rb_move_to_trash_bin) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                }
//                    moveToTrashBin(uriList, context);
            } else if (selectedId == R.id.rb_delete_permanently) {
                WorkManager.getInstance(context)
                        .getWorkInfoByIdLiveData(getContentUriIdWorkRequest.getId())
                        .observe((LifecycleOwner) context, info -> {
                            if (info != null && info.getState() == WorkInfo.State.SUCCEEDED) {
                                String[] outputData = info.getOutputData().getStringArray(
                                        GetContentUriIdWorker.KEY_CONTENT_URI);
                                if (outputData == null) return;
                                List<Uri> uriList = new ArrayList<>();
                                for (String data : outputData)
                                    uriList.add(Uri.parse(data));

                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                        deleteAPI30(uriList, context);
                                    } else {
                                        deleteAPI28(uriList, context);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("test", e.getMessage());
                                }
                            }
                        });


            } else if (selectedId == R.id.rb_deeply_delete) {
//                deleteDeeply(uriList, context);
            }
            alertDialog.dismiss();
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.R)
//    private static void moveToTrashBin(List<Uri> uriList, Context context) {
//
//    }

//    private static void deleteDeeply(List<Uri> uriList, Context context) {
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private static void deleteAPI30(List<Uri> uriList, Context context) throws
            IntentSender.SendIntentException {
        ContentResolver contentResolver = context.getContentResolver();
        PendingIntent pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
        IntentSender intentSender = pendingIntent.getIntentSender();
        ((Activity) context).startIntentSenderForResult(intentSender,
                DELETE_REQUEST_CODE, null, 0,
                0, 0, null);
    }

    private static void deleteAPI28(List<Uri> uriList, Context context) {
        ContentResolver resolver = context.getContentResolver();
        for (Uri uri : uriList)
            resolver.delete(uri, null, null);
    }
}
