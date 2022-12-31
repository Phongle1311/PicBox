package com.hcmus.picbox.database.favorite;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavoriteEntity.class}, version = 1)
public abstract class FavouritesDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "favourites.db";

    private static FavouritesDatabase instance;

    public static synchronized FavouritesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            FavouritesDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract FavoriteDao favoriteDao();
}
