/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.senab.actionbarpulltorefresh.sample;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * A sample which show you how to use PullToRefreshAttacher with Fragments.
 * <p/>
 * The TL;DR version is that the {@link PullToRefreshAttacher} should always be created in your
 * in {@link Activity#onCreate(android.os.Bundle)} and then passed to your Fragments as necessary.
 */
public class FragmentTabsActivity extends Activity implements ActionBar.TabListener {

    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_tabs);

        // The attacher should always be created in the Activity's onCreate
        mPullToRefreshAttacher = new PullToRefreshAttacher(this);

        // Add 3 tabs which will switch fragments
        ActionBar ab = getActionBar();
        ab.addTab(ab.newTab().setText("Tab 1").setTabListener(this));
        ab.addTab(ab.newTab().setText("Tab 2").setTabListener(this));
        ab.addTab(ab.newTab().setText("Tab 3").setTabListener(this));
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    // From TabListener
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Create Fragment
        SampleFragment fragment = new SampleFragment();

        // Give the Fragment a reference to our PullToRefreshAttacher
        fragment.setPullToRefreshAttacher(mPullToRefreshAttacher);

        // Set title for display purposes
        fragment.setTitle(tab.getText());

        ft.replace(R.id.ptr_fragment, fragment);
    }

    // From TabListener
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    // From TabListener
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }


    /**
     * Fragment Class
     */
    public static class SampleFragment extends Fragment implements
            PullToRefreshAttacher.OnRefreshListener {
        private CharSequence mTitle;
        private PullToRefreshAttacher mPullToRefreshAttacher;

        // Allow Activity to pass us it's PullToRefreshAttacher
        void setPullToRefreshAttacher(PullToRefreshAttacher attacher) {
            mPullToRefreshAttacher = attacher;
        }

        // Just allows the Activity to set the Fragment title
        void setTitle(CharSequence title) {
            mTitle = title;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            // Inflate the layout
            View view = inflater.inflate(R.layout.layout_fragment, container, false);

            // The ScrollView is what we'll be listening to for refresh starts
            ScrollView scrollView = (ScrollView) view.findViewById(R.id.ptr_scrollview);

            // Now set the ScrollView as the refreshable view, and the refresh listener (this)
            mPullToRefreshAttacher.setRefreshableView(scrollView, this);

            // Set title in Fragment for display purposes.
            TextView title = (TextView) view.findViewById(R.id.tv_title);
            title.setText(mTitle);

            return view;
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
}

