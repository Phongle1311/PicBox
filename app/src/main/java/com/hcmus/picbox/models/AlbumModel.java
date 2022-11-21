package com.hcmus.picbox.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 21/11/2022 by Phong Le
 */
public class AlbumModel implements Serializable {

    private String displayName;
    private String id;
    private String path;
    private PhotoModel coverImage;
    private int count;
    private final List<PhotoModel> mediaList = new ArrayList<>();

    public AlbumModel(String displayName, String id, String Path) {
        this.displayName = displayName;
        this.id = id;
        this.path = Path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public List<PhotoModel> getMediaList() {
        return mediaList;
    }

    public void addMedia(PhotoModel media) {
        mediaList.add(media);
    }
}