package com.hcmus.picbox.models;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to hold data of the app
 * Created on 21/11/2022 by Phong Le
 */
public class DataHolder {

    private static final List<PhotoModel> sAllMediaList = new ArrayList<>();
    private static final List<AlbumModel> sDeviceAlbumList = new ArrayList<>();
    private static final List<AlbumModel> sUserAlbumList = new ArrayList<>();
    private static final List<PhotoModel> sFavouriteAlbum = new ArrayList<>();
    private static final List<PhotoModel> sDeletedAlbum = new ArrayList<>();
    private static final List<PhotoModel> sSecretAlbum = new ArrayList<>();

    public static void addMedias(List<PhotoModel> list) {
        sAllMediaList.addAll(list);
    }

    public static List<PhotoModel> getAllMediaList() {
        return sAllMediaList;
    }

    public static void addDeviceAlbums(List<AlbumModel> list) {
        sDeviceAlbumList.addAll(list);
    }

    public static List<AlbumModel> getDeviceAlbumList() {
        return sDeviceAlbumList;
    }

    public static void addUserAlbums(List<AlbumModel> list) {
        sUserAlbumList.addAll(list);
    }

    public static List<AlbumModel> getUserAlbumList() {
        return sUserAlbumList;
    }

    public static void addFavouriteMedias(List<PhotoModel> list) {
        sFavouriteAlbum.addAll(list);
    }

    public static List<PhotoModel> getFavouriteAlbum() {
        return sFavouriteAlbum;
    }

    public static void addDeletedMedias(List<PhotoModel> list) {
        sDeletedAlbum.addAll(list);
    }

    public static List<PhotoModel> getDeletedAlbum() {
        return sDeletedAlbum;
    }

    public static void addSecretMedias(List<PhotoModel> list) {
        sSecretAlbum.addAll(list);
    }

    public static List<PhotoModel> getSecretAlbum() {
        return sSecretAlbum;
    }
}
