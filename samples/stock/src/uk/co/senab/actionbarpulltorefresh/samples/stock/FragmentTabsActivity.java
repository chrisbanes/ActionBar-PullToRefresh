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

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

/**
 * A sample which show you how to use {@link PullToRefreshLayout} with Fragments.
 */
public class FragmentTabsActivity extends BaseSampleActivity implements ActionBar.TabListener {
    private static String EXTRA_TITLE = "extra_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_tabs);

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

        // Set title for display purposes
        Bundle b = new Bundle();
        b.putString(EXTRA_TITLE, tab.getText().toString());
        fragment.setArguments(b);

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

