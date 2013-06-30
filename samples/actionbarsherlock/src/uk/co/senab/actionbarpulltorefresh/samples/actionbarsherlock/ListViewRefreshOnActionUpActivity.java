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

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * This sample shows how to use ActionBar-PullToRefresh with a
 * {@link android.widget.ListView ListView}, and manually creating (and attaching) a
 * {@link uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher} to the view.
 * It uses (@link uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.Options)
 * to declare that we should only refresh once the user has lifted their finger off the screen.
 */
public class ListViewRefreshOnActionUpActivity extends SherlockListActivity
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

        /**
         * Get ListView and give it an adapter to display the sample items
         */
        ListView listView = getListView();
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                ITEMS);
        listView.setAdapter(adapter);

        // As we're modifying some of the options, create an instance of
        // PullToRefreshAttacher.Options
        PullToRefreshAttacher.Options ptrOptions = new PullToRefreshAttacher.Options();

        // Here we create a PullToRefreshAttacher manually with the Options instance created above.
        mPullToRefreshAttacher = new PullToRefreshAttacher(this, ptrOptions);
        ptrOptions.refreshOnActionUp = true;

        // Set the Refreshable View to be the ListView and the refresh listener to be this.
        mPullToRefreshAttacher.setRefreshableView(listView, this);
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
