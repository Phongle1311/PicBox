package com.hcmus.picbox.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourites")
public class MediaEntity {

    @PrimaryKey
    public int mediaId;
    public String path;

    public MediaEntity(int mediaId, String path) {
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
