package com.hcmus.picbox.database.favorite;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    void insert(FavoriteEntity model);

    @Insert
    void insertAll(List<FavoriteEntity> models);

    @Delete
    void delete(FavoriteEntity model);

    @Query("SELECT * FROM favourite_items")
    List<FavoriteEntity> getAllFavorites();
}
