package com.hcmus.picbox.adapters;

import android.content.Context;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hcmus.picbox.R;

import java.text.MessageFormat;

public class CustomActionModeCallback implements ActionMode.Callback {

    private final Context context;
    private final MediaAdapter mediaAdapter;
    private TextView tvTitle;
    private CheckBox cbSelectAll;
    private ImageView actionDelete;

    public CustomActionModeCallback(Context context, MediaAdapter mediaAdapter) {
        this.context = context;
        this.mediaAdapter = mediaAdapter;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.layout_select_multiple_action_mode_header, null);
        actionMode.setCustomView(customView);
        tvTitle = customView.findViewById(R.id.tv_title);
        cbSelectAll = customView.findViewById(R.id.action_select_all);
        actionDelete = customView.findViewById(R.id.action_delete);
        cbSelectAll.setOnClickListener(view -> {
            mediaAdapter.selectAll();
            updateActionModeTitle();
        });
        actionDelete.setOnClickListener(view -> {
            Toast.makeText(context, "Selected images deleted", Toast.LENGTH_SHORT).show();
        });
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_select_all) {
            mediaAdapter.selectAll();
            updateActionModeTitle();
            return true;
        } else if (itemId == R.id.action_deselect_all) {
            mediaAdapter.deselectAll();
            updateActionModeTitle();
            return true;
        } else if (itemId == R.id.action_delete) {
            Toast.makeText(context, "Selected images deleted", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mediaAdapter.endSelection();
    }

    public void updateActionModeTitle() {
        tvTitle.setText(MessageFormat.format("{0} items selected", mediaAdapter.selectedMedia.size()));
    }
}
