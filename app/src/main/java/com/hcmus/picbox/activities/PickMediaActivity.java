package com.hcmus.picbox.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.PickerMediaAdapter;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.dataholder.MediaHolder;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Created on 30/12/2022 by Phong Le
 * <br/> This activity is use to picking media file in secret, user album, slide show, make GIF, ...
 */
public class PickMediaActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RadioButton rbSelectAll;
    private TextView tvSelectedCount;
    private ImageView btnConfirm;
    private PickerMediaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_media);

        initUI();
        setListener();
        prepareRecyclerView();

    }

    private void initUI() {
        btnBack = findViewById(R.id.btn_back);
        rbSelectAll = findViewById(R.id.rb_select_all);
        tvSelectedCount = findViewById(R.id.tv_selected_count);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    private void setListener() {
        btnBack.setOnClickListener(view -> finish());

        rbSelectAll.setOnClickListener(view -> {
            if (adapter == null) return;

            if (rbSelectAll.isChecked()) adapter.selectAll();
            else adapter.deselectAll();
        });

        btnConfirm.setOnClickListener(view -> {
            if (adapter == null) return;

            Intent intent = new Intent();
            SparseBooleanArray selectedIndex = adapter.getSelectedIndex();
            int size = MediaHolder.sTotalAlbum.getMediaList().size();
            boolean[] selected = new boolean[size];
            IntStream.range(0, size).forEach(i -> selected[i] = selectedIndex.get(i));

            intent.putExtra("selected_items", selected);
            finish();
        });
    }

    private void prepareRecyclerView() {
        adapter = new PickerMediaAdapter(PickMediaActivity.this, MediaHolder.sTotalAlbum,
                this::updateHeader);
        int spanCount = SharedPreferencesUtils.getIntData(PickMediaActivity.this,
                SharedPreferencesUtils.KEY_SPAN_COUNT);
        GridLayoutManager manager = new GridLayoutManager(PickMediaActivity.this, spanCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == AbstractModel.TYPE_DATE)
                    return spanCount;
                return 1;
            }
        });

        RecyclerView rcv = findViewById(R.id.rcv_pick_items);
        rcv.setLayoutManager(manager);
        rcv.setAdapter(adapter);
    }

    private void updateHeader(int selectedCount) {
        if (selectedCount == 0) tvSelectedCount.setText(R.string.pick_media_files);
        else tvSelectedCount.setText(String.format(Locale.getDefault(),
                "%d %s", selectedCount, getString(R.string.header_selected)));
    }
}
