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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher.PullToRefreshActivityHelper;

/**
 * A sample which show you how to use PullToRefreshAttacher with Fragments.
 * <p/>
 * The TL;DR version is that the {@link PullToRefreshAttacher} should always be created in your
 * in {@link #onCreate(android.os.Bundle)} and then pulled in from your Fragments as necessary.
 */
public class FragmentTabsActivity extends SherlockFragmentActivity
        implements ActionBar.TabListener, PullToRefreshActivityHelper {
    private static String EXTRA_TITLE = "extra_title";

    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_tabs);

        // The attacher should always be created in the Activity's onCreate
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

        // Add 3 tabs which will switch fragments
        ActionBar ab = getSupportActionBar();
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

        // Set title for display purposes
        Bundle b = new Bundle();
        b.putString(EXTRA_TITLE, tab.getText().toString());
        fragment.setArguments(b);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ptr_fragment, fragment).commit();
    }

    // From TabListener
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    // From TabListener
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

    /**
     * Fragment Class
     */
    public static class SampleFragment extends SherlockFragment implements
            PullToRefreshAttacher.OnRefreshListener {
        private PullToRefreshAttacher mPullToRefreshAttacher;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            // Inflate the layout
            View view = inflater.inflate(R.layout.layout_fragment, container, false);

            // The ScrollView is what we'll be listening to for refresh starts
            ScrollView scrollView = (ScrollView) view.findViewById(R.id.ptr_scrollview);

            // Now get the PullToRefresh attacher from the Activity. An exercise to the reader
            // is to create an implicit interface instead of casting to the concrete Activity
            mPullToRefreshAttacher = ((PullToRefreshActivityHelper) getActivity())
                    .getPullToRefreshAttacher();

            // Now set the ScrollView as the refreshable view, and the refresh listener (this)
            mPullToRefreshAttacher.addRefreshableView(scrollView, this);

            // Set title in Fragment for display purposes.
            TextView title = (TextView) view.findViewById(R.id.tv_title);
            Bundle b = getArguments();
            if (b != null) {
                title.setText(b.getString(EXTRA_TITLE));
            }

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
    }
}

