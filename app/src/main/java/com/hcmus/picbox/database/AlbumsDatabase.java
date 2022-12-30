package com.hcmus.picbox.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MediaEntity.class, AlbumEntity.class}, version = 1)
public abstract class AlbumsDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "albums.db";

    private static AlbumsDatabase instance;

    public static synchronized AlbumsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AlbumsDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract AlbumDao albumDao();
}
