package com.hcmus.picbox.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.hcmus.picbox.R;

public class SettingFragment extends Fragment {

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

        initUI(view);

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
            //TODO: show dialog choose number column in a row
            //setMultiColumnTextView.text(....);
        });

        languageLayout.setOnClickListener(view12 -> {
            //TODO: show dialog choose language
            //setLanguageTextView.text(....);
            final String[] language =
                    {
                            "English",
                            "Vietnamese"
                    };
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
        super.onResume();
        initUI(requireView());
    }

    private void initUI(View view) {
        cameraPermissionButton = view.findViewById(R.id.cameraPermissionButton);
        if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            cameraPermissionButton.setIconResource(R.drawable.ic_baseline_check_24);
            cameraPermissionButton.setIconTintResource(R.color.green);
        }

        galleyPermissionButton = view.findViewById(R.id.galleryPermissionButton);
        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            galleyPermissionButton.setIconResource(R.drawable.ic_baseline_check_24);
            galleyPermissionButton.setIconTintResource(R.color.green);
        }


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
}
