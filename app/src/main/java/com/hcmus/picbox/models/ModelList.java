package com.hcmus.picbox.models;

import static com.hcmus.picbox.models.AbstractModel.groupMode;

import java.util.ArrayList;
import java.util.List;

/**
 * This is custom list, used to stored grid-items and having datetime-items <br/>
 * Created on 23/11/2022 by Phong Le
 */
public class ModelList {

    private final List<MediaModel> mMediaList;      // Don't have date time items
    protected MediaModel lastItem; // used to optimize add method from O(n) to O(1)
    private List<AbstractModel> mModelList;   // Have date time items

    public ModelList() {
        mModelList = new ArrayList<>();
        mMediaList = new ArrayList<>();
    }

    public List<AbstractModel> getModelList() {
        return mModelList;
    }

    public List<MediaModel> getMediaList() {
        return mMediaList;
    }

    /**
     * Add a model to the end of the list with O(1), check to add date item if needed
     *
     * @param model the media item want to add
     */
    public void add(MediaModel model) {
        if (!AbstractModel.groupMode.equals(AbstractModel.GROUP_MODE_OPTION_1)
                && (lastItem == null || !lastItem.isTimeEqual(model)))
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
    public void addAll(List<MediaModel> list) {
        if (list == null || list.size() == 0)
            return;
        for (MediaModel model : list)
            add(model);
    }

    /**
     * Add a model to any position in the list with O(n), check to add date item if needed<br/>
     * It traverses the list, inserting in the appropriate position
     *
     * @param model the media item want to add
     */
    public void insert(MediaModel model) {
        int i = 0;
        for (; i < mMediaList.size(); i++) {
            if (model.mLastModifiedTime.isAfter(mMediaList.get(i).mLastModifiedTime)) {
                break;
            }
        }
        mMediaList.add(i, model);
        updateModelList();
    }

    /**
     * Remove model from list, also remove datetime-model if needed
     *
     * @param model the media item want to add
     */
    public void remove(MediaModel model) {
        mModelList.remove(model);
        updateModelList();
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
    public void updateModelList() {
        mModelList = new ArrayList<>();
        if (groupMode.equals(AbstractModel.GROUP_MODE_OPTION_1)) {
            mModelList.addAll(mMediaList);
            lastItem = mMediaList.get(mModelList.size() - 1);
        } else {
            lastItem = null;
            for (MediaModel model : mMediaList) {
                if (lastItem == null || !lastItem.isTimeEqual(model))
                    mModelList.add(new DateModel(model.getLastModifiedTime()));
                mModelList.add(model);
                lastItem = model;
            }
        }
    }

    public void updateMediaList() {
        mMediaList.removeIf(model -> !model.checkExists());
    }
}
