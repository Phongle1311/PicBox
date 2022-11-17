package com.hcmus.picbox.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.hcmus.picbox.R;

public class SettingFragment extends Fragment {
    MaterialButton cameraPermissionButton;
    MaterialButton galleyPermissionButton;

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


        return view;
    }

    private void initUI(View view) {
        cameraPermissionButton = view.findViewById(R.id.cameraPermissionButton);
        if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            cameraPermissionButton.setIconResource(R.drawable.ic_baseline_check_circle_24);
            cameraPermissionButton.setIconTintResource(R.color.green);
        }

        galleyPermissionButton = view.findViewById(R.id.galleryPermissionButton);
        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            galleyPermissionButton.setIconResource(R.drawable.ic_baseline_check_circle_24);
            galleyPermissionButton.setIconTintResource(R.color.green);
        }
    }
}
