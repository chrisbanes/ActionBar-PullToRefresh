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
import android.app.Fragment;
import android.app.ListActivity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.senab.actionbarpulltorefresh.library.FragmentHelper;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

/**
 * This sample shows how to use ActionBar-PullToRefresh with a
 * {@link android.widget.ListView ListView}, and manually creating (and attaching) a
 * {@link PullToRefreshAttacher} to the view.
 */
public class ListViewActivity extends BaseSampleActivity {

    @Override
    protected Fragment getSampleFragment() {
        return new SampleListFragment();
    }

    /**
     * Fragment Class
     */
    public static class SampleListFragment extends ListFragment implements
            PullToRefreshAttacher.OnRefreshListener {

        private static String[] ITEMS = {"Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
                "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu",
                "Airag", "Airedale", "Aisy Cendre", "Allgauer Emmentaler", "Abbaye de Belloc",
                "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi", "Acorn", "Adelost",
                "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
                "Allgauer Emmentaler"};

        private PullToRefreshLayout mPullToRefreshLayout;
        private PullToRefreshAttacher mPullToRefreshAttacher;

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // As we're using a ListFragment we need to inject a PullToRefreshLayout into the View.
            // This is easily done with FragmentHelper
            mPullToRefreshLayout = FragmentHelper.wrapListFragmentView(view);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Here we create a PullToRefreshAttacher manually without an Options instance.
            // PullToRefreshAttacher will manually create one using default values.
            mPullToRefreshAttacher = PullToRefreshAttacher.get(getActivity());
            mPullToRefreshAttacher.setOnRefreshListener(this);

            // Set the Refreshable View to be the ListView and the refresh listener to be this.
            mPullToRefreshLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, false);

            /**
             * Get ListView and give it an adapter to display the sample items
             */
            ListView listView = getListView();
            ListAdapter adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    ITEMS);
            listView.setAdapter(adapter);
            setListShownNoAnimation(true);
        }

        @Override
        public void onDestroy() {
            // We now need to destroy the PullToRefreshAttacher
            mPullToRefreshAttacher.destroy();

            super.onDestroy();
        }

        @Override
        public void onRefreshStarted(View view) {
            // Hide the list
            setListShown(false);

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
                    // Show the list again
                    setListShown(true);
                }
            }.execute();
        }
    }
}
