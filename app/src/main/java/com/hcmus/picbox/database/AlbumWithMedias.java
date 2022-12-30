package com.hcmus.picbox.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class AlbumWithMedias {

    @Embedded
    public AlbumEntity albumEntity;

    @Relation(
            parentColumn = "album_id",
            entityColumn = "album_id"
    )
    public List<MediaEntity> mediaEntities;

    public AlbumWithMedias(AlbumEntity albumEntity, List<MediaEntity> mediaEntities) {
        this.albumEntity = albumEntity;
        this.mediaEntities = mediaEntities;
    }
}
