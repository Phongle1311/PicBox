package com.hcmus.picbox.models;

import java.util.ArrayList;
import java.util.List;

/**
 * This is custom list, used to stored grid item and having date time items <br/>
 * Created on 23/11/2022 by Phong Le
 */
public class ModelList {

    private final List<AbstractModel> mList;

    public ModelList() {
        mList = new ArrayList<>();
    }

    public List<AbstractModel> getList() {
        return mList;
    }

    public void add(PhotoModel model) {

    }

    public void addAll(List<PhotoModel> list) {

    }

    public void insert(PhotoModel model) {

    }
}
