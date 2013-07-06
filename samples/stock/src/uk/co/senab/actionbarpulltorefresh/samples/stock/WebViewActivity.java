/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.senab.actionbarpulltorefresh.samples.stock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.samples.stock.R;

/**
 * This sample shows how to use ActionBar-PullToRefresh with a
 * {@link android.webkit.WebView WebView}, and manually creating (and attaching) a
 * {@link uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher} to the view.
 */
public class WebViewActivity extends Activity implements PullToRefreshAttacher.OnRefreshListener {

    private PullToRefreshAttacher mPullToRefreshAttacher;

    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        /**
         * Get ListView and give it an adapter to display the sample items
         */
        mWebView = (WebView) findViewById(R.id.ptr_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new SampleWebViewClient());

        /**
         * Here we create a PullToRefreshAttacher manually without an Options instance.
         * PullToRefreshAttacher will manually create one using default values.
         */
        mPullToRefreshAttacher = new PullToRefreshAttacher(this);

        // Set the Refreshable View to be the ListView and the refresh listener to be this.
        mPullToRefreshAttacher.setRefreshableView(mWebView, this);

        // Finally make the WebView load something...
        mWebView.loadUrl("http://www.google.com");
    }

    @Override
    public void onRefreshStarted(View view) {
        // Here we just reload the webview
        mWebView.reload();
    }

    private class SampleWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Return false so the WebView loads the url
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            // If the PullToRefreshAttacher is refreshing, make it as complete
            if (mPullToRefreshAttacher.isRefreshing()) {
                mPullToRefreshAttacher.setRefreshComplete();
            }
        }
    }
}
