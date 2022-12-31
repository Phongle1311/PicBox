package com.hcmus.picbox.database.album;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"album_id", "media_id"})
public class AlbumMediaCrossRef {

    @ColumnInfo(name = "media_id")
    public int mediaId;

    @ColumnInfo(name = "album_id")
    public int albumId;

    public AlbumMediaCrossRef(int albumId, int mediaId) {
        this.albumId = albumId;
        this.mediaId = mediaId;
    }
}
