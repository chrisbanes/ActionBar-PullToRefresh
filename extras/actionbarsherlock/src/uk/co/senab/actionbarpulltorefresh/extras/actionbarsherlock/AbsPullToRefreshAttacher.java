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
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;

import uk.co.senab.actionbarpulltorefresh.library.EnvironmentDelegate;
import uk.co.senab.actionbarpulltorefresh.library.HeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.Options;

class AbsPullToRefreshAttacher extends
        uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher {

    private FrameLayout mHeaderViewWrapper;

    protected AbsPullToRefreshAttacher(Activity activity, Options options) {
        super(activity, options);
    }

    @Override
    protected void addHeaderViewToActivity(View headerView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            super.addHeaderViewToActivity(headerView);
        } else {
            // On older devices we need to wrap the HeaderView in a FrameLayout otherwise
            // visibility changes do not take effect
            mHeaderViewWrapper = new FrameLayout(getAttachedActivity());
            mHeaderViewWrapper.addView(headerView);
            super.addHeaderViewToActivity(mHeaderViewWrapper);
        }
    }

    @Override
    protected void updateHeaderViewPosition(View headerView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            super.updateHeaderViewPosition(headerView);
        } else if (mHeaderViewWrapper != null) {
            super.updateHeaderViewPosition(mHeaderViewWrapper);
        }
    }

    @Override
    protected void removeHeaderViewFromActivity(View headerView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            super.removeHeaderViewFromActivity(headerView);
        } else if (mHeaderViewWrapper != null) {
            super.removeHeaderViewFromActivity(mHeaderViewWrapper);
            mHeaderViewWrapper = null;
        }
    }

    @Override
    protected EnvironmentDelegate createDefaultEnvironmentDelegate() {
        return new AbsEnvironmentDelegate();
    }

    @Override
    protected HeaderTransformer createDefaultHeaderTransformer() {
        return new AbsDefaultHeaderTransformer();
    }

    public static class AbsEnvironmentDelegate implements EnvironmentDelegate {
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
                context = activity;
            }
            return context;
        }
    }
}
