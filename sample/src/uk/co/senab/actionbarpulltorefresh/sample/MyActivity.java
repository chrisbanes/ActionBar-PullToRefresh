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

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshHelper;

public class MyActivity extends ListActivity implements PullToRefreshHelper.OnRefreshListener {

    private static String[] STRINGS = {"Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
            "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
            "Aisy Cendre",
            "Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
            "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
            "Aisy Cendre",
            "Allgauer Emmentaler"};

    private PullToRefreshHelper mPullToRefreshHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = getListView();
        listView.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, STRINGS));

        mPullToRefreshHelper = new PullToRefreshHelper(this, getListView());
        mPullToRefreshHelper.setRefreshListener(this);

    }

    @Override
    public void onRefresh(View view) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mPullToRefreshHelper.onRefreshComplete();
            }
        }.execute();
    }
}
