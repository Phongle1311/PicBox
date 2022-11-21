package com.hcmus.picbox.models;

import android.util.Log;

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

    public static void addMedia(PhotoModel media) {
        sAllMediaList.add(media);
    }

    public static List<PhotoModel> getAllMediaList() {
        return sAllMediaList;
    }

    public static void addDeviceAlbums(List<AlbumModel> list) {
        sDeviceAlbumList.addAll(list);
    }

    public static void addDeviceAlbum(AlbumModel album) {
        sDeviceAlbumList.add(album);
    }

    public static List<AlbumModel> getDeviceAlbumList() {
        return sDeviceAlbumList;
    }

    public static boolean containDeviceAlbumID(String albumId) {
        for (AlbumModel album : sDeviceAlbumList)
            if (albumId.equals(album.getId()))
                return true;
        return false;
    }

    public static AlbumModel getDeviceAlbumById(String albumId) {
        AlbumModel result = null;
        for (AlbumModel album : sDeviceAlbumList)
            if (album.getId().equals(albumId)) {
                result = album;
                break;
            }
        return  result;
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

    // for test
    public static void printDeviceAlbums() {
        for (AlbumModel album : sDeviceAlbumList)
            Log.d("test", album.getDisplayName() + " " + album.getId());
    }
}