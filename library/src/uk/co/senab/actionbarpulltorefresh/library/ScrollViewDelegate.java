package uk.co.senab.actionbarpulltorefresh.library;

import android.widget.ScrollView;

public class ScrollViewDelegate implements PullToRefreshHelper.ViewDelegate<ScrollView> {

    @Override
    public boolean isViewScrolledToTop(ScrollView scrollView) {
        return scrollView.getScrollY() <= 0;
    }
}
