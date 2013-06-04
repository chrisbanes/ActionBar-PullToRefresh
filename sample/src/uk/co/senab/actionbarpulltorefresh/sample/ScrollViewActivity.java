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

package uk.co.senab.actionbarpulltorefresh.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

/**
 * This sample shows how to use ActionBar-PullToRefresh with a {@link android.widget.ScrollView}. It
 * utilises {@link PullToRefreshLayout} to setup the ScrollView via XML. See the layout resource
 * file for more information.
 * <p />
 * Once inflated, you can retrieve the {@link PullToRefreshAttacher} by calling
 * {@link PullToRefreshLayout#getAttacher(android.app.Activity, int) getAttacher(Activity, int)},
 * passing it the PullToRefreshLayout's ID. From there you can set your
 * {@link PullToRefreshAttacher.OnRefreshListener OnRefreshListener} as usual.
 */
public class ScrollViewActivity extends Activity
        implements PullToRefreshAttacher.OnRefreshListener {

    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);

        // Retrieve PullToRefreshAttacher from PullToRefreshLayout
        mPullToRefreshAttacher = PullToRefreshLayout.getAttacher(this, R.id.ptr_layout);

        // Set Listener to know when a refresh should be started
        mPullToRefreshAttacher.setRefreshListener(this);
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
