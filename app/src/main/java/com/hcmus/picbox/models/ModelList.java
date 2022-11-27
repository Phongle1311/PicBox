package com.hcmus.picbox.models;

import java.util.ArrayList;
import java.util.List;

/**
 * This is custom list, used to stored grid item and having date time items <br/>
 * Created on 23/11/2022 by Phong Le
 */
public class ModelList {

    private final List<AbstractModel> mList;
    private final List<PhotoModel> mDefaultList;
    private PhotoModel lastItem; // used to optimize add method from O(n) to O(1)

    public ModelList() {
        mList = new ArrayList<>();
        mDefaultList = new ArrayList<>();
    }

    public List<AbstractModel> getList() {
        return mList;
    }

    public List<PhotoModel> getDefaultList() {
        return mDefaultList;
    }

    /**
     * Add a model to the end of the list with O(1), check to add date item if needed
     *
     * @param model the media item want to add
     */
    public void add(PhotoModel model) {
        if (lastItem == null || !lastItem.isTimeEqual(model))
            mList.add(new DateModel(model.getLastModifiedTime()));
        mList.add(model);
        mDefaultList.add(model);
        lastItem = model;
    }

    /**
     * Add many model to the end of the list (which is sorted) with O(list.size()), check to add date item if needed
     *
     * @param list the list of media items want to add
     */
    public void addAll(List<PhotoModel> list) {
        if (list == null || list.size() == 0)
            return;
        for (PhotoModel model : list)
            add(model);
    }

    /**
     * Add a model to any position in the list with O(n), check to add date item if needed<br/>
     * It traverses the list, inserting in the appropriate position
     *
     * @param model the media item want to add
     */
    public void insert(PhotoModel model) {

    }


}
