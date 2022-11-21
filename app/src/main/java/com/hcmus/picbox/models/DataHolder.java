package com.hcmus.picbox.models;

import android.util.Log;

import com.hcmus.picbox.interfaces.IOnLoadFinish;
import com.hcmus.picbox.interfaces.IOnMediaListChanged;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to hold data of the app
 * Created on 21/11/2022 by Phong Le
 */
public class DataHolder {

    public static final String DCIM_ID = "00";
    public static final String DCIM_DISPLAY_NAME = "Camera";
    private static final List<PhotoModel> sAllMediaList = new ArrayList<>();
    private static final List<AlbumModel> sDeviceAlbumList = new ArrayList<>();
    private static final List<AlbumModel> sUserAlbumList = new ArrayList<>();
    private static final List<PhotoModel> sFavouriteAlbum = new ArrayList<>();
    private static final List<PhotoModel> sDeletedAlbum = new ArrayList<>();
    private static final List<PhotoModel> sSecretAlbum = new ArrayList<>();

    private static IOnMediaListChanged onMediaListChangedListener;
    private static IOnLoadFinish onLoadFinishListener;

    public static void setOnMediaListChangeListener(IOnMediaListChanged listener) {
        onMediaListChangedListener = listener;
    }

    public static void setOnLoadFinishListener(IOnLoadFinish listener) {
        onLoadFinishListener = listener;
    }

    public static void onLoadFinish() {
        if (onLoadFinishListener != null)
            onLoadFinishListener.onLoadFinish();
    }

    public static void addMedias(List<PhotoModel> list) {
//        int oldSize = sAllMediaList.size();
        sAllMediaList.addAll(list);
//        if (onMediaListChangedListener != null)
//            onMediaListChangedListener.onMediaListChanged(oldSize, list.size());
    }

    public static void addMedia(PhotoModel media) {
        sAllMediaList.add(media);
//        if (onMediaListChangedListener != null)
//            onMediaListChangedListener.onMediaListChanged(sAllMediaList.size() - 1, 1);
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

    public static void addMediaToDeviceAlbumById(PhotoModel media, String albumId) {
        for (AlbumModel album : sDeviceAlbumList)
            if (album.getId().equals(albumId)) {
                album.addMedia(media);
                return;
            }
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
}
