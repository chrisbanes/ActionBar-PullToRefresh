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

package uk.co.senab.actionbarpulltorefresh.samples.actionbarcompat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

/**
 * This sample shows how to use ActionBar-PullToRefresh with a {@link android.widget.GridView
 * GridView}, and manually creating (and attaching) a {@link PullToRefreshAttacher} to the view.
 */
public class GridViewActivity extends ActionBarActivity
        implements PullToRefreshAttacher.OnRefreshListener {

    private static String[] ITEMS = {"Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
            "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu",
            "Airag", "Airedale", "Aisy Cendre", "Allgauer Emmentaler", "Abbaye de Belloc",
            "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi", "Acorn", "Adelost",
            "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"};

    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);

        GridView gridView = (GridView) findViewById(R.id.ptr_gridview);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                ITEMS);
        gridView.setAdapter(adapter);

        // As we're modifying some of the options, create an instance of
        // PullToRefreshAttacher.Options
        PullToRefreshAttacher.Options ptrOptions = new PullToRefreshAttacher.Options();

        // Here we make the refresh scroll distance to 75% of the GridView height
        ptrOptions.refreshScrollDistance = 0.75f;

        // Here we customise the animations which are used when showing/hiding the header view
        ptrOptions.headerInAnimation = R.anim.slide_in_top;
        ptrOptions.headerOutAnimation = R.anim.slide_out_top;

        // Here we define a custom header layout which will be inflated and used
        ptrOptions.headerLayout = R.layout.customised_header;

        // Here we define a custom header transformer which will alter the header based on the
        // current pull-to-refresh state
        ptrOptions.headerTransformer = new CustomisedHeaderTransformer();

        // Here we create a PullToRefreshAttacher manually with the Options instance created above.
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, ptrOptions);

        /**
         * As GridView is an AbsListView derived class, we create a new
         * AbsListViewDelegate instance. You do NOT need to do this if you're using
         * a supported scrollable Views. It is merely in this sample to show you how to set a
         * custom view delegate.
         */
        PullToRefreshAttacher.ViewDelegate handler = new AbsListViewDelegate();
        mPullToRefreshAttacher.addRefreshableView(gridView, handler, this);
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
                    Thread.sleep(Constants.SIMULATED_REFRESH_LENGTH);
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

    /**
     * Here's a customised header transformer which displays the scroll progress as text.
     */
    static class CustomisedHeaderTransformer extends PullToRefreshAttacher.HeaderTransformer {

        private TextView mMainTextView;
        private TextView mProgressTextView;

        @Override
        public void onViewCreated(Activity activity, View headerView) {
            mMainTextView = (TextView) headerView.findViewById(R.id.ptr_text);
            mProgressTextView = (TextView) headerView.findViewById(R.id.ptr_text_secondary);
        }

        @Override
        public void onReset() {
            mMainTextView.setVisibility(View.VISIBLE);
            mMainTextView.setText(R.string.pull_to_refresh_pull_label);

            mProgressTextView.setVisibility(View.GONE);
            mProgressTextView.setText("");
        }

        @Override
        public void onPulled(float percentagePulled) {
            mProgressTextView.setVisibility(View.VISIBLE);
            mProgressTextView.setText(Math.round(100f * percentagePulled) + "%");
        }

        @Override
        public void onRefreshStarted() {
            mMainTextView.setText(R.string.pull_to_refresh_refreshing_label);
            mProgressTextView.setVisibility(View.GONE);
        }

        @Override
        public void onReleaseToRefresh() {
            mMainTextView.setText(R.string.pull_to_refresh_release_label);
        }

        @Override
        public void onRefreshMinimized() {
            // In this header transformer, we will ignore this call
        }
    }
}
