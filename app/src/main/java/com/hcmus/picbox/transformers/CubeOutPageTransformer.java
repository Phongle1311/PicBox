package com.hcmus.picbox.transformers;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class CubeOutPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float pos ) {
        page.setPivotX( pos < 0f ? page.getWidth() : 0f );
        page.setPivotY( page.getHeight() * 0.5f );
        page.setRotationY( 90f * pos );
    }
}
