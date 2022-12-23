package com.hcmus.picbox.interfaces;

import com.hcmus.picbox.models.MediaModel;

import java.util.List;

/**
 * Created on 23/12/2022 by Phong Le
 */
public interface IOnDeleteMediaInDetailFragment {
    void onDeleteMedia(List<MediaModel> model);
}
