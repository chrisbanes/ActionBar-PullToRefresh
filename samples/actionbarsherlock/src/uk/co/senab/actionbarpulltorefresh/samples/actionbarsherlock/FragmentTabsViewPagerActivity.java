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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A sample which show you how to use PullToRefreshLayout with Fragments in a ViewPager.
 */
public class FragmentTabsViewPagerActivity extends SherlockFragmentActivity {
    private static String EXTRA_TITLE = "extra_title";

    private FragmentTabPager mFragmentTabPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_tabs_vp);
        ViewPager vp = (ViewPager) findViewById(R.id.ptr_viewpager);
        mFragmentTabPager = new FragmentTabPager(this, vp);

        // Add 3 tabs which will switch fragments
        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Bundle b = new Bundle();
        b.putString(EXTRA_TITLE, "Tab 1");
        mFragmentTabPager.addTab(ab.newTab().setText("Tab 1"), SampleFragment.class, b);

        b = new Bundle();
        b.putString(EXTRA_TITLE, "Tab 2");
        mFragmentTabPager.addTab(ab.newTab().setText("Tab 2"), SampleFragment.class, b);

        b = new Bundle();
        b.putString(EXTRA_TITLE, "Tab 3");
        mFragmentTabPager.addTab(ab.newTab().setText("Tab 3"), SampleFragment.class, b);
    }

    /**
     * Fragment Class
     */
    public static class SampleFragment extends SherlockFragment implements
            OnRefreshListener {

        private PullToRefreshLayout mPullToRefreshLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            // Inflate the layout
            View view = inflater.inflate(R.layout.layout_fragment, container, false);

            // Now give the find the PullToRefreshLayout and set it up
            mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
            ActionBarPullToRefresh.from(getActivity())
                    .allChildrenArePullable()
                    .listener(this)
                    .setup(mPullToRefreshLayout);

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

                    // Notify PullToRefreshLayout that the refresh has finished
                    mPullToRefreshLayout.setRefreshComplete();
                }
            }.execute();
        }
    }
}

