package com.hcmus.picbox.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface NoteDao {
    @Insert
    public void insert(NoteEntity model);

    @Update
    public void update(NoteEntity model);

    @Delete
    void delete(NoteEntity model);

    @Query("SELECT * FROM notes WHERE mediaId = :id")
    public NoteEntity getItemById(int id);
}
