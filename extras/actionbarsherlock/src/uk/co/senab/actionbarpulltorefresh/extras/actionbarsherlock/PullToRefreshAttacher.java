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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

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
    protected HeaderTransformer createDefaultHeaderTransformer() {
        return new AbsDefaultHeaderTransformer();
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
            } else if (activity instanceof SherlockFragmentActivity) {
                return ((SherlockFragmentActivity) activity).getSupportActionBar()
                        .getThemedContext();
            } else if (activity instanceof SherlockExpandableListActivity) {
                return ((SherlockExpandableListActivity) activity).getSupportActionBar()
                        .getThemedContext();
            } else if (activity instanceof SherlockPreferenceActivity) {
                return ((SherlockPreferenceActivity) activity).getSupportActionBar()
                        .getThemedContext();
            }
            return super.getContextForInflater(activity);
        }
    }

    public static class AbsDefaultHeaderTransformer extends DefaultHeaderTransformer {

        @Override
        protected Drawable getActionBarBackground(Context context) {
            // Super handles ICS+ anyway...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                return super.getActionBarBackground(context);
            }

            // Need to get resource id of style pointed to from actionBarStyle
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.actionBarStyle, outValue, true);
            // Now get action bar style values...
            TypedArray abStyle = context.getTheme().obtainStyledAttributes(outValue.resourceId,
                    R.styleable.SherlockActionBar);
            try {
                return abStyle.getDrawable(R.styleable.SherlockActionBar_background);
            } finally {
                abStyle.recycle();
            }
        }

        @Override
        protected int getActionBarSize(Context context) {
            // Super handles ICS+ anyway...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                return super.getActionBarSize(context);
            }

            TypedArray values = context.getTheme()
                    .obtainStyledAttributes(R.styleable.SherlockTheme);
            try {
                return values.getDimensionPixelSize(R.styleable.SherlockTheme_actionBarSize, 0);
            } finally {
                values.recycle();
            }
        }

    }
}
