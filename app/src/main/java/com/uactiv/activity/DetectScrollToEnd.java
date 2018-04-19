package com.uactiv.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by pratikb on 03-11-2017.
 */
public abstract class DetectScrollToEnd extends RecyclerView.OnScrollListener {
    private final LinearLayoutManager layoutManager;
    private final int mThreshold;
    /**
     * true: scroll down
     */
    private boolean isScrollDown;

    /**
     * trigger when scroll to bottom, depend on threshold number (when have enough item to scroll)
     */
    protected abstract void onLoadMore();

    /**
     * detect scroll to bottom of recycle view
     *
     * @param layoutManager current layout manager
     * @param threshold     should 2 >= threshold < 5
     */
    public DetectScrollToEnd(LinearLayoutManager layoutManager, int threshold) {
        this.layoutManager = layoutManager;
        mThreshold = threshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        isScrollDown = dy >= 0;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        // scroll end animation and visible item count < total item count (in case total item count not enought to scroll)
        if (newState == RecyclerView.SCROLL_STATE_IDLE && visibleItemCount < totalItemCount) {
            if (isScrollDown && firstVisibleItemPosition + visibleItemCount + mThreshold >= totalItemCount)
                onLoadMore();
        }
    }
}