package com.hcmus.picbox.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.picbox.R;
import com.hcmus.picbox.fragments.PhotosFragment;
import com.hcmus.picbox.works.DeleteHelper;

public class AlbumActivity extends AppCompatActivity {

    private PhotosFragment photosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String albumId = intent.getStringExtra("albumId");

            photosFragment = new PhotosFragment(albumId);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, photosFragment)
                    .commit();
        }

        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        topAppBar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DeleteHelper.DELETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (photosFragment != null)
                photosFragment.finishDelete();
        }
    }
}