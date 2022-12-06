package com.hcmus.picbox.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.picbox.R;
import com.hcmus.picbox.fragments.PhotosFragment;

public class AlbumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String albumId = intent.getStringExtra("albumId");

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, new PhotosFragment(albumId))
                    .commit();
        }

        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        topAppBar.setNavigationOnClickListener(v -> finish());
    }
}