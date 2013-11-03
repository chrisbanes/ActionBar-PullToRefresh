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

package uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.app.Activity;
import android.content.Context;

import uk.co.senab.actionbarpulltorefresh.library.EnvironmentDelegate;
import uk.co.senab.actionbarpulltorefresh.library.HeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.Options;

public class PullToRefreshAttacher extends
        uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher {

    public static PullToRefreshAttacher get(Activity activity) {
        return get(activity, new Options());
    }

    public static PullToRefreshAttacher get(Activity activity, Options options) {
        return new PullToRefreshAttacher(activity, options);
    }

    protected PullToRefreshAttacher(Activity activity, Options options) {
        super(activity, options);
    }

    @Override
    protected EnvironmentDelegate createDefaultEnvironmentDelegate() {
        return new AbsEnvironmentDelegate();
    }

    @Override
    protected HeaderTransformer createDefaultHeaderTransformer() {
        return new AbsDefaultHeaderTransformer();
    }

    public static class AbsEnvironmentDelegate extends EnvironmentDelegate {
        /**
         * @return Context which should be used for inflating the header layout
         */
        public Context getContextForInflater(Activity activity) {
            ActionBar ab = null;
            if (activity instanceof SherlockActivity) {
                ab = ((SherlockActivity) activity).getSupportActionBar();
            } else if (activity instanceof SherlockListActivity) {
                ab = ((SherlockListActivity) activity).getSupportActionBar();
            } else if (activity instanceof SherlockFragmentActivity) {
                ab = ((SherlockFragmentActivity) activity).getSupportActionBar();
            } else if (activity instanceof SherlockExpandableListActivity) {
                ab = ((SherlockExpandableListActivity) activity).getSupportActionBar();
            } else if (activity instanceof SherlockPreferenceActivity) {
                ab = ((SherlockPreferenceActivity) activity).getSupportActionBar();
            }

            Context context = null;
            if (ab != null) {
                context = ab.getThemedContext();
            }
            if (context == null) {
                context = super.getContextForInflater(activity);
            }
            return context;
        }
    }
}
