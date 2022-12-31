package com.hcmus.picbox.database.album;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "album_table")
public class AlbumEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "album_id")
    public int albumId;

    @ColumnInfo(name = "album_name")
    public String albumName;

    public AlbumEntity(String albumName) {
        this.albumName = albumName;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
}
