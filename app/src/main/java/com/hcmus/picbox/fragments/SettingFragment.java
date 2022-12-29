package com.hcmus.picbox.fragments;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_FOOD_QUESTION;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_GROUP_MODE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_LANGUAGE;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PASSWORD;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PET_QUESTION;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_SPAN_COUNT;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.LANGUAGE_OPTION_1;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.LANGUAGE_OPTION_2;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.hcmus.picbox.activities.ImagePasswordActivity;
import com.hcmus.picbox.activities.MainActivity;
import com.hcmus.picbox.activities.RegisterPasswordActivity;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.utils.PermissionUtils;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

import java.util.Arrays;

public class SettingFragment extends Fragment {

    private final int IMAGE_PASSWORD_REQUEST_CODE = 1298912;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        context = view.getContext();
        declareUI(view);
        initUI();

        // Camera permission setting
        cameraPermissionButton.setOnClickListener(view15 -> {
            //go to app setting
            Intent appToSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + requireActivity().getPackageName()));
            appToSettingIntent.addCategory(Intent.CATEGORY_DEFAULT);
            appToSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(appToSettingIntent);
        });

        // Storage permission setting
        galleyPermissionButton.setOnClickListener(view16 -> {
            //go to app setting
            Intent appToSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + requireActivity().getPackageName()));
            appToSettingIntent.addCategory(Intent.CATEGORY_DEFAULT);
            appToSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(appToSettingIntent);
        });

        // Themes setting
        darkThemeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            // TODO: theme day - night
        });

        // Floating button setting
        floatingButtonSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            // TODO: hide FAB, show
        });

        // Span count setting
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
                SharedPreferencesUtils.saveData(context, KEY_SPAN_COUNT, columnsPerRowNumberPicker.getValue());
                multiColumnDialog.dismiss();
            });

            multiColumnDialog.show();
        });

        // Language setting
        languageLayout.setOnClickListener(view12 -> {
            Dialog languageSettingDialog = new Dialog(context);
            languageSettingDialog.setContentView(R.layout.dialog_language_setting);

            // Set current value
            RadioButton englishRadioButton = languageSettingDialog.findViewById(R.id.englishRadioButton);
            RadioButton vietnameseRadioButton = languageSettingDialog.findViewById(R.id.vietnamseRadioButton);
            switch (SharedPreferencesUtils.getStringData(context, KEY_LANGUAGE)) {
                case LANGUAGE_OPTION_1:
                    englishRadioButton.setChecked(true);
                    break;
                case LANGUAGE_OPTION_2:
                    vietnameseRadioButton.setChecked(true);
                    break;
                default:
                    break;
            }

            // Confirm button
            languageSettingDialog.findViewById(R.id.confirmButton).setOnClickListener(view17 -> {
                RadioGroup languageSettingRadioGroup = languageSettingDialog.findViewById(R.id.languageSettingRadioGroup);
                int selectedId = languageSettingRadioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.englishRadioButton) {
                    languageTextView.setText(R.string.language_english);
                    SharedPreferencesUtils.saveData(context, KEY_LANGUAGE, LANGUAGE_OPTION_1);
                } else if (selectedId == R.id.vietnamseRadioButton) {
                    languageTextView.setText(R.string.language_vietnamese);
                    SharedPreferencesUtils.saveData(context, KEY_LANGUAGE, LANGUAGE_OPTION_2);
                }
                languageSettingDialog.dismiss();
            });

            // Cancel button
            languageSettingDialog.findViewById(R.id.cancelButton).setOnClickListener(view18 ->
                    languageSettingDialog.dismiss());

            languageSettingDialog.show();
        });

        // Group mode setting
        groupModeLayout.setOnClickListener(view17 -> {
            AlertDialog.Builder groupModeSettingDialogBuilder = new AlertDialog.Builder(context);
            groupModeSettingDialogBuilder.setTitle(R.string.group_mode_dialog_title);
            String[] saveValues =
                    {AbstractModel.GROUP_MODE_OPTION_2,
                            AbstractModel.GROUP_MODE_OPTION_3,
                            AbstractModel.GROUP_MODE_OPTION_4,
                            AbstractModel.GROUP_MODE_OPTION_1};

            String[] items =
                    {getResources().getString(R.string.day),
                            getResources().getString(R.string.month),
                            getResources().getString(R.string.year),
                            getResources().getString(R.string.none)};

            int checkedItem = Arrays.asList(saveValues).indexOf(
                    SharedPreferencesUtils.getStringData(context, KEY_GROUP_MODE));
            groupModeSettingDialogBuilder
                    .setSingleChoiceItems(items, checkedItem, null)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        int groupModeChosenIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        SharedPreferencesUtils.saveData(context, KEY_GROUP_MODE, saveValues[groupModeChosenIndex]);
                        groupModeTextView.setText(items[groupModeChosenIndex]);
                        dialog.cancel();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

            AlertDialog groupModeSettingDialog = groupModeSettingDialogBuilder.create();
            groupModeSettingDialog.setCanceledOnTouchOutside(true);
            groupModeSettingDialog.show();
        });

        // Grid mode setting
        gridModeLayout.setOnClickListener(view13 ->
        {
            //TODO: show dialog choose grid mode
            //setGridModeTextView.text(....);
        });

        // Lock rotation setting
        rotationSwitch.setOnCheckedChangeListener((compoundButton, b) ->
        {

        });

        // Secret setting
        passwordImageLayout.setOnClickListener(view14 ->
        {
            //TODO: show dialog edit password image
        });

        passwordImageSwitch.setOnCheckedChangeListener((compoundButton, b) ->
        {
            if (passwordImageSwitch.isChecked()
                    && !SharedPreferencesUtils.checkKeyExist(context, KEY_PASSWORD)) {
                Intent intent = new Intent(context, RegisterPasswordActivity.class);
                context.startActivity(intent);
            }
            if (!passwordImageSwitch.isChecked()
                    && SharedPreferencesUtils.checkKeyExist(context, KEY_PASSWORD)) {
                passwordImageSwitch.setChecked(true);
                Intent intent = new Intent(context, ImagePasswordActivity.class);
                startActivityForResult(intent, IMAGE_PASSWORD_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        // TODO: change UI when data change, no need to update on resume
        super.onResume();
        initUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PASSWORD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                boolean result = data.getBooleanExtra("accept_password", false);
                if (result) {
                    SharedPreferencesUtils.removeData(context, KEY_PASSWORD);
                    SharedPreferencesUtils.removeData(context, KEY_PET_QUESTION);
                    SharedPreferencesUtils.removeData(context, KEY_FOOD_QUESTION);
                    passwordImageSwitch.setChecked(false);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
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

        multiColumnTextView.setText(String.valueOf(SharedPreferencesUtils.getIntData(context, KEY_SPAN_COUNT)));

        switch (String.valueOf(SharedPreferencesUtils.getStringData(context, KEY_LANGUAGE))) {
            case LANGUAGE_OPTION_1:
                languageTextView.setText(R.string.language_english);
                break;
            case LANGUAGE_OPTION_2:
                languageTextView.setText(R.string.language_vietnamese);
                break;
        }

        switch (String.valueOf(SharedPreferencesUtils.getStringData(context, KEY_GROUP_MODE))) {
            case AbstractModel.GROUP_MODE_OPTION_2:
                groupModeTextView.setText(R.string.day);
                break;
            case AbstractModel.GROUP_MODE_OPTION_3:
                groupModeTextView.setText(R.string.month);
                break;
            case AbstractModel.GROUP_MODE_OPTION_4:
                groupModeTextView.setText(R.string.year);
                break;
            default:
                groupModeTextView.setText(R.string.none);
                break;
        }

        if (SharedPreferencesUtils.checkKeyExist(context, KEY_PASSWORD)) {
            passwordImageSwitch.setChecked(true);
        } else {
            passwordImageSwitch.setChecked(false);
        }
    }
}
