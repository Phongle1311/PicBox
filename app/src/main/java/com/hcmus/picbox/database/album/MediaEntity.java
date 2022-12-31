package com.hcmus.picbox.database.album;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_table")
public class MediaEntity {

    @PrimaryKey
    @ColumnInfo(name = "media_id")
    public int mediaId;

    public String path;

    public MediaEntity(int mediaId, String path) {
        this.mediaId = mediaId;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

