package com.hcmus.picbox.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hcmus.picbox.fragments.DisplayMediaFragment;
import com.hcmus.picbox.interfaces.IOnClickDetailBackButton;
import com.hcmus.picbox.models.MediaModel;

import java.util.List;

/**
 * Use FragmentStateAdapter instead of FragmentStatePagerAdapter because of deprecation <br/>
 * Created on 27/11/2022 by Phong Le
 */
public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    private final List<MediaModel> models;
    private final IOnClickDetailBackButton backListener;

    public ScreenSlidePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<MediaModel> models, IOnClickDetailBackButton backListen) {
        super(fragmentManager, lifecycle);
        this.models = models;
        this.backListener = backListen;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        MediaModel model = models.get(position);
        return new DisplayMediaFragment(model, backListener, position);
    }

    @Override
    public int getItemCount() {
        if (models == null)
            return 0;
        return models.size();
    }

    public void removeFragment(int position) {
        models.remove(position);
        notifyItemRemoved(position);
    }
}