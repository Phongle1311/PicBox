package com.hcmus.picbox.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.hcmus.picbox.R;

public class SettingFragment extends Fragment {
    MaterialButton cameraPermissionButton;
    MaterialButton galleyPermissionButton;

    SwitchCompat darkThemeSwitch;
    SwitchCompat floatingButtonSwitch;
    LinearLayout multiColumnLayout;
    TextView multiColumnTextView;
    LinearLayout languageLayout;
    TextView languageTextView;
    LinearLayout gridModeLayout;
    TextView gridModeTextView;

    SwitchCompat rotationSwitch;
    LinearLayout passwordImageLayout;
    SwitchCompat passwordImageSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        initUI(view);

        cameraPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to app setting
                Intent appToSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                appToSettingIntent.addCategory(Intent.CATEGORY_DEFAULT);
                appToSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(appToSettingIntent);
            }
        });

        galleyPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to app setting
                Intent appToSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                appToSettingIntent.addCategory(Intent.CATEGORY_DEFAULT);
                appToSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(appToSettingIntent);
            }
        });

        darkThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        floatingButtonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        multiColumnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: show dialog choose number column in a row
                //setMultiColumnTextView.text(....);
            }
        });

        languageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: show dialog choose language
                //setLanguageTextView.text(....);
                final String[] language =
                        {
                                "English",
                                "Vietnamese"
                        };
            }
        });

        gridModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: show dialog choose grid mode
                //setGridModeTextView.text(....);
            }
        });

        rotationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        passwordImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: show dialog edit password image
            }
        });

        passwordImageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //TODO: enable password for image

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI(getView());
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
        multiColumnLayout = view.findViewById(R.id.multicolumnLayout);
        multiColumnTextView = view.findViewById(R.id.multicolumnTextView);
        languageLayout = view.findViewById(R.id.languageLayout);
        languageTextView = view.findViewById(R.id.languageTextView);
        gridModeLayout = view.findViewById(R.id.gridModeLayout);
        gridModeTextView = view.findViewById(R.id.gridModeTextView);

        rotationSwitch = view.findViewById(R.id.rotationSwitch);

        passwordImageLayout = view.findViewById(R.id.passwordImageLayout);
        passwordImageSwitch = view.findViewById(R.id.passwordImageSwitch);
    }
}
