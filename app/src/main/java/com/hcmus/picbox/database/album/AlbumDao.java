package com.hcmus.picbox.database.album;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAlbum(AlbumEntity albumEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMedia(MediaEntity mediaEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbumMediaCrossRef(AlbumMediaCrossRef ref);

    @Update
    void updateAlbum(AlbumEntity albumEntity);

    @Transaction
    @Query("SELECT * FROM album_table")
    List<AlbumWithMedias> getAllAlbumWithMedias();
}
