package com.hcmus.picbox.models.dataholder;

import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.ModelList;
import com.hcmus.picbox.models.PhotoModel;

import java.util.List;

/**
 * This class contains and holds albums (an album is a list of medias) of the app<br/>
 * Include All Media <br/>
 * Created on 21/11/2022 by Phong Le
 */
public class MediaHolder {

    // static
    public static final String KEY_TOTAL_ALBUM = "total_album";
    public static final String KEY_FAVOURITE_ALBUM = "favourite_album";
    public static final String KEY_DELETED_ALBUM = "deleted_album";
    public static final String KEY_SECRET_ALBUM = "secret_album";

    private static final MediaHolder sTotalAlbum = new MediaHolder();       // the special album contains all medias
    private static final MediaHolder sFavouriteAlbum = new MediaHolder();   // the special album contains favourite medias
    private static final MediaHolder sDeletedAlbum = new MediaHolder();     // the special album contains deleted medias
    private static final MediaHolder sSecretAlbum = new MediaHolder();      // the special album contains secret medias

    // non-static
    private final ModelList album = new ModelList();

    public static MediaHolder getTotalAlbum() {
        return sTotalAlbum;
    }

    public static MediaHolder getFavouriteAlbum() {
        return sFavouriteAlbum;
    }

    public static MediaHolder getDeletedAlbum() {
        return sDeletedAlbum;
    }

    public static MediaHolder getSecretAlbum() {
        return sSecretAlbum;
    }

    public void addMedia(PhotoModel media) {
        album.add(media);
    }

    public void addAllMedias(List<PhotoModel> list) {
        album.addAll(list);
    }

    public List<AbstractModel> getList() {
        return album.getList();
    }

    public List<PhotoModel> getDefaultList() {
        return album.getDefaultList();
    }

    public int size() {
        return album.getList().size();
    }
}