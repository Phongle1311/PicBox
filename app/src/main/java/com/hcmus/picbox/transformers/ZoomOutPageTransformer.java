package com.hcmus.picbox.transformers;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.95f;
    private static final float MIN_ALPHA = 0.8f;

    public void transformPage(View view, float position) {
        int width = view.getWidth();
        int height = view.getHeight();

        if (position < -1)
            view.setAlpha(0f);
        else if (position <= 1) {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float verticalMargin = height * (1 - scaleFactor) / 2;
            float horizontalMargin = width * (1 - scaleFactor) / 2;

            if (position < 0)
                view.setTranslationX(horizontalMargin - verticalMargin / 2);
            else
                view.setTranslationX(-horizontalMargin + verticalMargin / 2);

            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        }
        else
            view.setAlpha(0f);
    }
}