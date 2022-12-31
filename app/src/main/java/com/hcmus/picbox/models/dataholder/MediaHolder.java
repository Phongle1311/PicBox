package com.hcmus.picbox.models.dataholder;

import com.hcmus.picbox.models.AlbumModel;

/**
 * This class contains and holds albums (an album is a list of medias) of the app<br/>
 * Include All Media <br/>
 * Created on 21/11/2022 by Phong Le
 */
public class MediaHolder {

    public static final String KEY_TOTAL_ALBUM = "total_album";
    public static final String KEY_FAVOURITE_ALBUM = "favourite_album";
    public static final String KEY_DELETED_ALBUM = "deleted_album";
    public static final String KEY_SECRET_ALBUM = "secret_album";

    // the special album contains all medias
    public static final AlbumModel sTotalAlbum = new AlbumModel("Total", KEY_TOTAL_ALBUM);

    // the special album contains favourite medias
    public static final AlbumModel sFavouriteAlbum = new AlbumModel("Favourite", KEY_FAVOURITE_ALBUM);

    // the special album contains deleted medias
    public static final AlbumModel sDeletedAlbum = new AlbumModel("Trash", KEY_DELETED_ALBUM);

    // the special album contains secret medias
    public static final AlbumModel sSecretAlbum = new AlbumModel("Secret", KEY_SECRET_ALBUM);
}