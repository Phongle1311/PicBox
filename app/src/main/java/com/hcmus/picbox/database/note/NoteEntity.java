package com.hcmus.picbox.database.note;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
