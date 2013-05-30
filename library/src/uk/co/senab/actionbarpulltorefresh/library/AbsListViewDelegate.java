package uk.co.senab.actionbarpulltorefresh.library;

import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;

public class AbsListViewDelegate implements PullToRefreshHelper.ViewDelegate<AbsListView> {

    @Override
    public boolean isViewScrolledToTop(AbsListView absListView) {
        final Adapter adapter = absListView.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        } else if (absListView.getFirstVisiblePosition() == 0) {
            final View firstVisibleChild = absListView.getChildAt(0);
            return firstVisibleChild != null && firstVisibleChild.getTop() >= 0;
        }
        return false;
    }
}
