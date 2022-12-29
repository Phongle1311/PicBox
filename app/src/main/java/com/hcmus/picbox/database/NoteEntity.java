package com.hcmus.picbox.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class NoteEntity {

    @PrimaryKey
    public int mediaId;
    @ColumnInfo(defaultValue = "")
    public String note;

    public NoteEntity(int mediaId, String note) {
        this.mediaId = mediaId;
        this.note = note;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
