package com.hcmus.picbox.adapters;

import androidx.annotation.NonNull;

import com.hcmus.picbox.models.PhotoModel;

public class PhotoItem extends GridItem {
    private PhotoModel mPhoto;

    public PhotoItem(@NonNull PhotoModel photo) {
        this.mPhoto = photo;
    }

    public PhotoModel getPhoto() {
        return mPhoto;
    }

    @Override
    public int getType() {
        return TYPE_PHOTO;
    }
}
