package com.hcmus.picbox.database.favorite;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_items")
public class FavoriteEntity {

    @PrimaryKey
    @ColumnInfo(name = "media_id")
    public int mediaId;

    public String path;

    public FavoriteEntity(int mediaId, String path) {
        this.mediaId = mediaId;
        this.path = path;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
