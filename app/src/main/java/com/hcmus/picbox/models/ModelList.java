package com.hcmus.picbox.models;

import static com.hcmus.picbox.models.AbstractModel.GROUP_BY;

import java.util.ArrayList;
import java.util.List;

/**
 * This is custom list, used to stored grid-items and having datetime-items <br/>
 * Created on 23/11/2022 by Phong Le
 */
public class ModelList {

    public static List<PhotoModel> mMediaList;      // Don't have date time items
    public static PhotoModel lastItem; // used to optimize add method from O(n) to O(1)
    public static List<AbstractModel> mModelList;   // Have date time items

    public ModelList() {
        mModelList = new ArrayList<>();
        mMediaList = new ArrayList<>();
    }

    public List<AbstractModel> getModelList() {
        return mModelList;
    }

    public List<PhotoModel> getMediaList() {
        return mMediaList;
    }

    /**
     * Add a model to the end of the list with O(1), check to add date item if needed
     *
     * @param model the media item want to add
     */
    public void add(PhotoModel model) {
        if (AbstractModel.GROUP_BY != 0 && (lastItem == null || !lastItem.isTimeEqual(model)))
            mModelList.add(new DateModel(model.getLastModifiedTime()));
        mModelList.add(model);
        mMediaList.add(model);
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

    /**
     * @return the number of medias, not DateModel
     */
    public int getCount() {
        return mMediaList.size();
    }

    /**
     * Update modelList after changing datetime-breaking type
     */
    public static void updateModelList() {
        if (GROUP_BY == 0) {
            mModelList.addAll(mMediaList);
            lastItem = mMediaList.get(mModelList.size() - 1);
        } else {
            mModelList = new ArrayList<>();
            lastItem = null;
            for (PhotoModel model : mMediaList) {
                if (lastItem == null || !lastItem.isTimeEqual(model))
                    mModelList.add(new DateModel(model.getLastModifiedTime()));
                mModelList.add(model);
                lastItem = model;
            }
        }
    }
}
