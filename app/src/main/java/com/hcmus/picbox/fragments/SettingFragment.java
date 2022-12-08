package com.hcmus.picbox.fragments;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.hcmus.picbox.R;
import com.hcmus.picbox.utils.ArrayUtils;
import com.hcmus.picbox.utils.PermissionUtils;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

import java.util.Arrays;

import java.util.Objects;

public class SettingFragment extends Fragment {

    private Context context;

    private MaterialButton cameraPermissionButton;
    private MaterialButton galleyPermissionButton;

    private SwitchCompat darkThemeSwitch;
    private SwitchCompat floatingButtonSwitch;
    private LinearLayout multiColumnLayout;
    private TextView multiColumnTextView;
    private LinearLayout languageLayout;
    private TextView languageTextView;
    private LinearLayout groupModeLayout;
    private TextView groupModeTextView;
    private LinearLayout gridModeLayout;
    private TextView gridModeTextView;

    private SwitchCompat rotationSwitch;
    private LinearLayout passwordImageLayout;
    private SwitchCompat passwordImageSwitch;

    @SuppressLint("NonConstantResourceId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        context = view.getContext();
        declareUI(view);
        initUI();

        cameraPermissionButton.setOnClickListener(view15 -> {
            //go to app setting
            Intent appToSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + requireActivity().getPackageName()));
            appToSettingIntent.addCategory(Intent.CATEGORY_DEFAULT);
            appToSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(appToSettingIntent);
        });

        galleyPermissionButton.setOnClickListener(view16 -> {
            //go to app setting
            Intent appToSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + requireActivity().getPackageName()));
            appToSettingIntent.addCategory(Intent.CATEGORY_DEFAULT);
            appToSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(appToSettingIntent);
        });

        darkThemeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

        });

        floatingButtonSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

        });

        multiColumnLayout.setOnClickListener(view1 -> {
            Dialog multiColumnDialog = new Dialog(context);

            multiColumnDialog.setContentView(R.layout.dialog_multicolumn_setting);

            //UI number picker
            NumberPicker columnsPerRowNumberPicker = multiColumnDialog.findViewById(R.id.numberPicker);
            columnsPerRowNumberPicker.setMinValue(2);
            columnsPerRowNumberPicker.setMaxValue(5);
            columnsPerRowNumberPicker.setValue(Integer.parseInt(multiColumnTextView.getText().toString()));

            //Button Dialog
            MaterialButton cancelButton = multiColumnDialog.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(view2 -> multiColumnDialog.dismiss());

            MaterialButton confirmButton = multiColumnDialog.findViewById(R.id.confirmButton);
            confirmButton.setOnClickListener(view22 -> {
                multiColumnTextView.setText(String.valueOf(columnsPerRowNumberPicker.getValue()));
                SharedPreferencesUtils.saveData(context, "num_columns_of_row", columnsPerRowNumberPicker.getValue());
                multiColumnDialog.dismiss();
            });

            multiColumnDialog.show();
        });

        languageLayout.setOnClickListener(view12 -> {
            Dialog languageSettingDialog = new Dialog(context);
            languageSettingDialog.setContentView(R.layout.dialog_language_setting);

            RadioButton englishRadioButton = languageSettingDialog.findViewById(R.id.englishRadioButton);
            RadioButton vietnameseRadioButton = languageSettingDialog.findViewById(R.id.vietnamseRadioButton);
            switch (SharedPreferencesUtils.getStringData(context, "language")){
                case "english":
                    englishRadioButton.setChecked(true);
                    SharedPreferencesUtils.saveData(context, "language_setting_choosing", "english");
                    break;
                case "vietnamese":
                    vietnameseRadioButton.setChecked(true);
                    SharedPreferencesUtils.saveData(context, "language_setting_choosing", "vietnamese");
                    break;
            }

            RadioGroup languageSettingRadioGroup = languageSettingDialog.findViewById(R.id.languageSettingRadioGroup);
            languageSettingRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                switch (checkedId){
                    case R.id.englishRadioButton:
                        SharedPreferencesUtils.saveData(context, "language_setting_choosing", "english");
                        break;
                    case R.id.vietnamseRadioButton:
                        SharedPreferencesUtils.saveData(context, "language_setting_choosing", "vietnamese");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + checkedId);
                }
            });

            MaterialButton confirmButton = languageSettingDialog.findViewById(R.id.confirmButton);
            confirmButton.setOnClickListener(view17 -> {
                final String languageChosen = SharedPreferencesUtils.getStringData(context, "language_setting_choosing");
                SharedPreferencesUtils.saveData(context, "language", languageChosen);
                switch (languageChosen) {
                    case "english":
                        languageTextView.setText("English");
                        break;
                    case "vietnamese":
                        languageTextView.setText("Tiếng Việt");
                        break;
                }
                SharedPreferencesUtils.removeData(context, "language_setting_choosing");
                languageSettingDialog.dismiss();
            });

            MaterialButton cancelButton = languageSettingDialog.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(view18 -> {
                SharedPreferencesUtils.removeData(context, "language_setting_choosing");
                languageSettingDialog.dismiss();
            });

            languageSettingDialog.show();
        });

        groupModeLayout.setOnClickListener(view17 -> {
            AlertDialog.Builder groupModeSettingDialogBuilder = new AlertDialog.Builder(context);
            groupModeSettingDialogBuilder.setTitle(R.string.group_mode_dialog_title);
            String[] items = {getResources().getString(R.string.day), getResources().getString(R.string.month), getResources().getString(R.string.year), getResources().getString(R.string.none)};

            int checkedItem = ArrayUtils.indexOf(items, SharedPreferencesUtils.getStringData(context, "group_mode"));
            groupModeSettingDialogBuilder.setSingleChoiceItems(items, checkedItem, (dialog, which) -> {
                SharedPreferencesUtils.saveData(context, "group_mode_choosing", items[which]);
            });

            groupModeSettingDialogBuilder.setPositiveButton(R.string.confirm, (dialog, id) -> {
                        final String groupModeChosen = SharedPreferencesUtils.getStringData(context, "group_mode_choosing");
                        SharedPreferencesUtils.saveData(context, "group_mode", groupModeChosen);
                        groupModeTextView.setText(groupModeChosen);
                        SharedPreferencesUtils.removeData(context, "group_mode_choosing");
                        dialog.cancel();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> {
                        SharedPreferencesUtils.removeData(context, "group_mode_choosing");
                        dialog.cancel();
                    });

            AlertDialog groupModeSettingDialog = groupModeSettingDialogBuilder.create();
            groupModeSettingDialog.setCanceledOnTouchOutside(true);
            groupModeSettingDialog.show();
        });

        gridModeLayout.setOnClickListener(view13 ->

        {
            //TODO: show dialog choose grid mode
            //setGridModeTextView.text(....);
        });

        rotationSwitch.setOnCheckedChangeListener((compoundButton, b) ->

        {

        });

        passwordImageLayout.setOnClickListener(view14 ->

        {
            //TODO: show dialog edit password image
        });

        passwordImageSwitch.setOnCheckedChangeListener((compoundButton, b) ->

        {
            //TODO: enable password for image

        });

        return view;
    }

    @Override
    public void onResume() {
        // TODO: change UI when data change, no need to update on resume
        super.onResume();
        initUI();
    }

    private void declareUI(View view) {
        cameraPermissionButton = view.findViewById(R.id.cameraPermissionButton);
        galleyPermissionButton = view.findViewById(R.id.galleryPermissionButton);

        darkThemeSwitch = view.findViewById(R.id.darkThemeSwitch);
        floatingButtonSwitch = view.findViewById(R.id.floatingButtonSwitch);

        multiColumnLayout = view.findViewById(R.id.multi_column_layout);
        multiColumnTextView = view.findViewById(R.id.multi_column_textview);

        languageLayout = view.findViewById(R.id.languageLayout);
        languageTextView = view.findViewById(R.id.languageTextView);

        groupModeLayout = view.findViewById(R.id.groupModeLayout);
        groupModeTextView = view.findViewById(R.id.groupModeTextView);

        gridModeLayout = view.findViewById(R.id.gridModeLayout);
        gridModeTextView = view.findViewById(R.id.gridModeTextView);

        rotationSwitch = view.findViewById(R.id.rotationSwitch);

        passwordImageLayout = view.findViewById(R.id.passwordImageLayout);
        passwordImageSwitch = view.findViewById(R.id.passwordImageSwitch);
    }

    private void initUI() {
        if (PermissionUtils.checkPermissions(context, CAMERA)) {
            cameraPermissionButton.setIconResource(R.drawable.ic_baseline_check_24);
            cameraPermissionButton.setIconTintResource(R.color.green);
        }

        if (PermissionUtils.checkPermissions(context, READ_EXTERNAL_STORAGE)) {
            galleyPermissionButton.setIconResource(R.drawable.ic_baseline_check_24);
            galleyPermissionButton.setIconTintResource(R.color.green);
        }

        multiColumnTextView.setText(String.valueOf(SharedPreferencesUtils.getIntData(context, "num_columns_of_row")));

        switch (String.valueOf(SharedPreferencesUtils.getStringData(context, "language"))) {
            case "english":
                languageTextView.setText("English");
                break;
            case "vietnamese":
                languageTextView.setText("Tiếng Việt");
                break;
        }

        groupModeTextView.setText(String.valueOf(SharedPreferencesUtils.getStringData(context, "group_mode")));
    }
}
