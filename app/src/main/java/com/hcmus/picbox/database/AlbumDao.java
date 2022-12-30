package com.hcmus.picbox.database;


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
    void insertAlbum(AlbumEntity albumEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMedias(List<MediaEntity> mediaEntities);

    @Update
    void update(AlbumEntity albumEntity);

    @Transaction
    @Query("SELECT * FROM albums")
    List<AlbumWithMedias> getAllAlbumWithMedias();
}
