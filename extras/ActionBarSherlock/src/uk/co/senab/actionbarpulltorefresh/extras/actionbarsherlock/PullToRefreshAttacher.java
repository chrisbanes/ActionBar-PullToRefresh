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

package uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListActivity;

import android.app.Activity;
import android.content.Context;

public class PullToRefreshAttacher extends
        uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher {

    public PullToRefreshAttacher(Activity activity) {
        super(activity);
    }

    public PullToRefreshAttacher(Activity activity, Options options) {
        super(activity, options);
    }

    @Override
    protected EnvironmentDelegate createDefaultEnvironmentDelegate() {
        return new AbsEnvironmentDelegate();
    }

    @Override
    protected void onOptionsCreated(Options options) {
        // Replace the standard header with our ABS version
        if (options.headerLayout == R.layout.default_header) {
            options.headerLayout = R.layout.abs_default_header;
        }
    }

    public static class AbsEnvironmentDelegate extends EnvironmentDelegate {
        /**
         * @return Context which should be used for inflating the header layout
         */
        public Context getContextForInflater(Activity activity) {
            if (activity instanceof SherlockActivity) {
                return ((SherlockActivity) activity).getSupportActionBar().getThemedContext();
            } else if (activity instanceof SherlockListActivity) {
                return ((SherlockListActivity) activity).getSupportActionBar().getThemedContext();
            }
            return activity;
        }
    }
}
