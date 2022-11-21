package com.hcmus.picbox.interfaces;

/**
 * Created on 21/11/2022 by Phong Le
 */
public interface IOnMediaListChanged {
    /**
     * Notify any registered observers that the <code>itemCount</code> items starting at
     * position <code>positionStart</code> have changed.
     *
     * <p>This is a callback used to noticed adapter in <code>PhotoFragment.java</code></p>
     *
     * @param positionStart Position of the first item that has changed
     * @param itemCount Number of items that have changed
     */
    void onMediaListChanged(int positionStart, int itemCount);
}
