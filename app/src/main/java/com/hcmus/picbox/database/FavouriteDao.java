package com.hcmus.picbox.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavouriteDao {
    @Insert
    void insert(MediaEntity model);

    @Insert
    void insertAll(List<MediaEntity> models);

    @Delete
    void delete(MediaEntity model);

    @Query("SELECT * FROM favourites")
    List<MediaEntity> getAllFavorites();
}
