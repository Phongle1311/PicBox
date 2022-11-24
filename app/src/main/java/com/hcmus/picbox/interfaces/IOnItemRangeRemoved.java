package com.hcmus.picbox.interfaces;

public interface IOnItemRangeRemoved {
    /**
     * Notify any registered observers that the <code>itemCount</code> items starting at
     * position <code>positionStart</code> have been removed.
     *
     * <p>This is a callback used to noticed adapter in <code>PhotoFragment.java</code>
     * and <code>AlbumFragment.java</code></p>
     *
     * @param positionStart Position of the first item that has changed
     * @param itemCount Number of items that have changed
     */
    void onItemRangeRemoved(int positionStart, int itemCount);
}
