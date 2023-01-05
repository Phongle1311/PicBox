package com.hcmus.picbox.adapters;

import android.content.Context;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hcmus.picbox.R;

import java.text.MessageFormat;

/**
 * Created by 5/1/2023 by Phong Le
 */
public class SecretActionModeCallback implements ActionMode.Callback {

    private final Context ctx;
    private final SecretMediaAdapter adapter;
    private TextView tvTitle;

    public SecretActionModeCallback(Context ctx, SecretMediaAdapter adapter) {
        this.ctx = ctx;
        this.adapter = adapter;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View customView = inflater.inflate(R.layout.layout_select_multiple_secret_media_action_mode_callback, null);
        actionMode.setCustomView(customView);

        // Init UI
        tvTitle = customView.findViewById(R.id.tv_title);
        CheckBox cbSelectAll = customView.findViewById(R.id.action_select_all);

        // Select/Deselect all
        cbSelectAll.setOnClickListener(view -> {
            if (cbSelectAll.isChecked()) {
                adapter.selectAll();
            } else {
                adapter.deselectAll();
            }
            updateActionModeTitle();
        });

        // Delete listener
        customView.findViewById(R.id.action_delete).setOnClickListener(view -> {
            // todo: delete in internal
        });

        // Recover listener
        customView.findViewById(R.id.action_recovery).setOnClickListener(view -> {
            // todo: recover media
        });

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        adapter.endSelection();
    }

    public void updateActionModeTitle() {
        tvTitle.setText(MessageFormat.format("{0} items selected", adapter.selectedMedia.size()));
    }
}