package uk.co.senab.actionbarpulltorefresh.library.delegate;

import android.view.View;
import android.webkit.WebView;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class WebViewDelegate extends PullToRefreshAttacher.Delegate {

    public static final Class SUPPORTED_VIEW_CLASS = WebView.class;

    @Override
    public boolean isScrolledToTop(View view) {
        return view.getScrollY() <= 0;
    }
}

