package com.hcmus.picbox.models;

import java.io.File;
import java.io.Serializable;

/**
 * Created on 21/11/2022 by Phong Le
 */
public class AlbumModel extends ModelList implements Serializable {

    private String displayName;
    private final String id;
    private String path;

    public AlbumModel(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

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

    public File getCover() {
        return lastItem != null ? lastItem.getFile() : null;
    }
}
