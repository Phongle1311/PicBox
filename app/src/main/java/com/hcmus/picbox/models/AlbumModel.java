package com.hcmus.picbox.models;

import java.io.Serializable;

/**
 * Created on 21/11/2022 by Phong Le
 */
public class AlbumModel implements Serializable {

    private String displayName;
    private String path;
    private PhotoModel coverImage;
    private int count;

}
