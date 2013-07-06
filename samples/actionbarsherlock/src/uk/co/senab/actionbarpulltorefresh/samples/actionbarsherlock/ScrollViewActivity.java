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

package uk.co.senab.actionbarpulltorefresh.samples.actionbarsherlock;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.DefaultHeaderTransformer;

/**
 * This sample shows how to use ActionBar-PullToRefresh with a
 * {@link android.widget.ScrollView ScrollView}.
 */
public class ScrollViewActivity extends SherlockActivity
        implements PullToRefreshAttacher.OnRefreshListener {

    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);

        ScrollView scrollView = (ScrollView) findViewById(R.id.ptr_scrollview);

        // Create new PullToRefreshAttacher
        mPullToRefreshAttacher = new PullToRefreshAttacher(this);

        // Set Refreshable View to be the ScrollView and the refresh listener to be this.
        mPullToRefreshAttacher.setRefreshableView(scrollView, this);

        // As we haven't set an explicit HeaderTransformer, we can safely cast the result of
        // getHeaderTransformer() to DefaultHeaderTransformer
        DefaultHeaderTransformer ht = (DefaultHeaderTransformer) mPullToRefreshAttacher
                .getHeaderTransformer();

        // As we're using a DefaultHeaderTransformer we can change the text which is displayed.
        // You should load these values from localised resources, but we'll just use static strings.
        ht.setPullText("Swipe Me!!!");
        ht.setRefreshingText("Refreshing :)");
    }

    @Override
    public void onRefreshStarted(View view) {
        /**
         * Simulate Refresh with 4 seconds sleep
         */
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                // Notify PullToRefreshAttacher that the refresh has finished
                mPullToRefreshAttacher.setRefreshComplete();
            }
        }.execute();
    }
}
