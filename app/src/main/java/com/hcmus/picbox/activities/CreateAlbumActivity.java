package com.hcmus.picbox.activities;

import static com.hcmus.picbox.activities.PickMediaActivity.KEY_SELECTED_ITEMS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.hcmus.picbox.R;

public class CreateAlbumActivity extends AppCompatActivity {

    public static final String KEY_ALBUM_NAME = "album_name";
    public static final String KEY_CREATE_ALBUM_RESULT = "create_album_result";
    private boolean[] selected;

    private final ActivityResultLauncher<Intent> pickMediaActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null)
                                selected = data.getBooleanArrayExtra(KEY_SELECTED_ITEMS);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        findViewById(R.id.btn_back).setOnClickListener(view -> finish());

        findViewById(R.id.btn_add_file).setOnClickListener(view -> {
            Intent intent = new Intent(this, PickMediaActivity.class);
            pickMediaActivityResultLauncher.launch(intent);
        });

        findViewById(R.id.btn_confirm).setOnClickListener(view -> {
            // set result (album's name + selected files) and finish
            String albumName = ((TextView) findViewById(R.id.et_album_name)).getText().toString();
            if (albumName.trim().length() == 0)
                Toast.makeText(this, "Album's name can't be empty!", Toast.LENGTH_SHORT)
                        .show();
            else {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                bundle.putString(KEY_ALBUM_NAME, albumName);
                if (selected != null)
                    bundle.putBooleanArray(KEY_SELECTED_ITEMS, selected);

                intent.putExtra(KEY_CREATE_ALBUM_RESULT, bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}