package com.hcmus.picbox.fragments;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.hcmus.picbox.R;
import com.hcmus.picbox.utils.PermissionUtils;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

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
    private LinearLayout gridModeLayout;
    private TextView gridModeTextView;

    private SwitchCompat rotationSwitch;
    private LinearLayout passwordImageLayout;
    private SwitchCompat passwordImageSwitch;

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
            if (Objects.equals(SharedPreferencesUtils.getStringData(context, "language"), getResources().getString(R.string.english))) {
                englishRadioButton.setChecked(true);
            } else {
                vietnameseRadioButton.setChecked(true);
            }


            String[] languageList = {getResources().getString(R.string.english), getResources().getString(R.string.vietnamese)};
            RadioGroup languageSettingRadioGroup = languageSettingDialog.findViewById(R.id.languageSettingRadioGroup);
            languageSettingRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                SharedPreferencesUtils.saveData(context, "language_setting_choosing", languageList[i]);
            });

            MaterialButton confirmButton = languageSettingDialog.findViewById(R.id.confirmButton);
            confirmButton.setOnClickListener(view17 -> {
                final String languageChosen = SharedPreferencesUtils.getStringData(context, "language_setting_choosing");
                SharedPreferencesUtils.saveData(context, "language", languageChosen);
                languageSettingDialog.dismiss();
            });

            MaterialButton cancelButton = languageSettingDialog.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(view18 -> {
                languageSettingDialog.dismiss();
            });


            languageSettingDialog.show();
        });

        gridModeLayout.setOnClickListener(view13 -> {
            //TODO: show dialog choose grid mode
            //setGridModeTextView.text(....);
        });

        rotationSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

        });

        passwordImageLayout.setOnClickListener(view14 -> {
            //TODO: show dialog edit password image
        });

        passwordImageSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
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
    }
}
