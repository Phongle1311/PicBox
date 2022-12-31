package com.hcmus.picbox.components;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.PickAlbumAdapter;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.dataholder.AlbumHolder;

/**
 * Create on 30/12/2022 by Phong Le <br/>
 * This dialog is shown when add media to use album
 */
public class ChooseAlbumDialog extends Dialog {

    public ChooseAlbumDialog(@NonNull Context context, IChooseAlbumDialogCallback callback) {
        super(context);
        setContentView(R.layout.dialog_choose_album);

        findViewById(R.id.btn_back).setOnClickListener(view -> dismiss());

        findViewById(R.id.tv_create_album).setOnClickListener(view -> {
            dismiss();
            callback.onCreateAlbum();
        });

        RecyclerView rcv = findViewById(R.id.rcv);
        rcv.setAdapter(new PickAlbumAdapter(context, AlbumHolder.getUserAlbumList().getList(),
                album -> {
                    dismiss();
                    callback.onSelectAlbum(album);
                }));
        rcv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    public interface IChooseAlbumDialogCallback {
        void onCreateAlbum();

        void onSelectAlbum(AlbumModel album);
    }
}
