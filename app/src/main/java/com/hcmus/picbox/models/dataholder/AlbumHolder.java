package com.hcmus.picbox.models.dataholder;

import com.hcmus.picbox.models.AlbumModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains and holds albums list (list of albums - NOT album of medias) of the app,
 * include Device Album List and User Album List<br/>
 * Created on 24/11/2022 by Phong Le
 */
public class AlbumHolder {

    // static
    private static final AlbumHolder sDeviceAlbumList = new AlbumHolder();
    private static final AlbumHolder sUserAlbumList = new AlbumHolder();

    public static final String DCIM_ID = "00";
    public static final String DCIM_DISPLAY_NAME = "Camera";

    public static AlbumHolder getDeviceAlbumList() {
        return sDeviceAlbumList;
    }

    public static AlbumHolder getUserAlbumList() {
        return sUserAlbumList;
    }

    // non-static
    private final List<AlbumModel> mAlbumList = new ArrayList<>();

    public void addAlbums(List<AlbumModel> list) {
        mAlbumList.addAll(list);
    }

    public void addAlbum(AlbumModel album) {
        mAlbumList.add(album);
    }

    public List<AlbumModel> getList() {
        return mAlbumList;
    }

    public AlbumModel getAlbumById(String albumId) {
        AlbumModel result = null;
        for (AlbumModel album : mAlbumList)
            if (album.getId().equals(albumId)) {
                result = album;
                break;
            }
        return result;
    }

    public int size() {
        return mAlbumList.size();
    }
}
