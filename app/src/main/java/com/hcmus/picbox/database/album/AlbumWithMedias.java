package com.hcmus.picbox.database.album;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class AlbumWithMedias {

    @Embedded
    public AlbumEntity albumEntity;

    @Relation(
            parentColumn = "album_id",
            entityColumn = "media_id",
            associateBy = @Junction(AlbumMediaCrossRef.class)
    )
    public List<MediaEntity> mediaEntities;
}