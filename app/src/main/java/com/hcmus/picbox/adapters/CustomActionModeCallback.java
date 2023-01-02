package com.hcmus.picbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.hcmus.picbox.R;
import com.hcmus.picbox.activities.SlideShowActivity;
import com.hcmus.picbox.models.MediaModel;

import java.text.MessageFormat;
import java.util.ArrayList;

public class CustomActionModeCallback implements ActionMode.Callback {

    private final Context ctx;
    private final MediaAdapter adapter;
    private TextView tvTitle;
    private final ICustomActionModeCallback callback;

    public interface ICustomActionModeCallback {
        void onAddingToAlbum();
    }

    public CustomActionModeCallback(Context ctx, MediaAdapter adapter, ICustomActionModeCallback callback) {
        this.ctx = ctx;
        this.adapter = adapter;
        this.callback = callback;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View customView = inflater.inflate(R.layout.layout_select_multiple_action_mode_header, null);
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
        customView.findViewById(R.id.action_delete).setOnClickListener(view -> adapter.deleteAll());

        // Show popup: more actions
        customView.findViewById(R.id.show_more_button).setOnClickListener(this::showPopup);

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

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(ctx, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_select_multiple_with_action_mode, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.add_to_album) {
                // Todo: add to album
                callback.onAddingToAlbum();
                return true;
            } else if (itemId == R.id.add_to_favourite) {
                // Add/remove media to/from favourites
                adapter.addToFavoriteList();
                adapter.endSelection();
                updateActionModeTitle(); // todo: need this line?
                Toast.makeText(ctx, "Added to favourites", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.move_to_secret) {
                // Todo: move to secret album
                return true;
            } else if (itemId == R.id.compound_images) {
                // Todo: navigate to fragment 3
                return true;
            } else if (itemId == R.id.slide_show) {
                // Todo: slide show (check if all media is photo, not video and gif)
                ArrayList<String> listAlbum = new ArrayList<>();
                ArrayList<Integer> listMediaID = new ArrayList<>();
                for (MediaModel selected : adapter.selectedMedia) {
                    if (selected.getType() == MediaModel.TYPE_PHOTO) {
                        listAlbum.add(selected.getAlbumId());
                        listMediaID.add(selected.getMediaId());
                    }
                }
                if (listAlbum.size() == 0) {
                    Toast.makeText(ctx, "Not exists supported file type.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(ctx, SlideShowActivity.class);
                Bundle data = new Bundle();
                data.putIntegerArrayList("selected_media_id", listMediaID);
                data.putStringArrayList("selected_album_id", listAlbum);
                intent.putExtra("media_selected_list", data);
                ctx.startActivity(intent);
                return true;
            } else if (itemId == R.id.make_gif) {
                // Todo: navigate to fragment 3
                return true;
            }
            return false;
        });
        popup.show();
    }
}
