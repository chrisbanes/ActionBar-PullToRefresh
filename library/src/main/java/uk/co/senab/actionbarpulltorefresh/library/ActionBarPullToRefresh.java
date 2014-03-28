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

package uk.co.senab.actionbarpulltorefresh.library;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;

public class ActionBarPullToRefresh {

    public static SetupWizard from(Activity activity) {
        return new SetupWizard(activity);
    }

    public static final class SetupWizard {
        private final Activity mActivity;
        private Options mOptions;
        private int[] refreshableViewIds;
        private View[] refreshableViews;
        private OnRefreshListener mOnRefreshListener;
        private ViewGroup mViewGroupToInsertInto;
        private HashMap<Class, ViewDelegate> mViewDelegates;

        private SetupWizard(Activity activity) {
            mActivity = activity;
        }

        public SetupWizard options(Options options) {
            mOptions = options;
            return this;
        }

        public SetupWizard allChildrenArePullable() {
            refreshableViewIds = null;
            refreshableViews = null;
            return this;
        }

        public SetupWizard theseChildrenArePullable(int... viewIds) {
            refreshableViewIds = viewIds;
            refreshableViews = null;
            return this;
        }

        public SetupWizard theseChildrenArePullable(View... views) {
            refreshableViews = views;
            refreshableViewIds = null;
            return this;
        }

        public SetupWizard useViewDelegate(Class<?> viewClass, ViewDelegate delegate) {
            if (mViewDelegates == null) {
                mViewDelegates = new HashMap<Class, ViewDelegate>();
            }
            mViewDelegates.put(viewClass, delegate);
            return this;
        }

        public SetupWizard listener(OnRefreshListener listener) {
            mOnRefreshListener = listener;
            return this;
        }

        public SetupWizard insertLayoutInto(ViewGroup viewGroup) {
            mViewGroupToInsertInto = viewGroup;
            return this;
        }

        public void setup(PullToRefreshLayout pullToRefreshLayout) {
            PullToRefreshAttacher attacher = pullToRefreshLayout.createPullToRefreshAttacher(
                    mActivity, mOptions);
            attacher.setOnRefreshListener(mOnRefreshListener);

            if (mViewGroupToInsertInto != null) {
                insertLayoutIntoViewGroup(mViewGroupToInsertInto, pullToRefreshLayout);
            }

            pullToRefreshLayout.setPullToRefreshAttacher(attacher);

            // First add the pullable child views
            if (refreshableViewIds != null) {
                pullToRefreshLayout.addChildrenAsPullable(refreshableViewIds);
            } else if (refreshableViews != null) {
                pullToRefreshLayout.addChildrenAsPullable(refreshableViews);
            } else {
                pullToRefreshLayout.addAllChildrenAsPullable();
            }

            // Now set any custom view delegates
            if (mViewDelegates != null) {
                final Set<Map.Entry<Class, ViewDelegate>> entries = mViewDelegates.entrySet();
                for (final Map.Entry<Class, ViewDelegate> entry : entries) {
                    attacher.useViewDelegate(entry.getKey(), entry.getValue());
                }
            }
        }

        private static void insertLayoutIntoViewGroup(ViewGroup viewGroup,
                PullToRefreshLayout pullToRefreshLayout) {
            // Move all children to PullToRefreshLayout. This code looks a bit silly but the child
            // indices change every time we remove a View (so we can't just iterate through)
            View child = viewGroup.getChildAt(0);
            while (child != null) {
                viewGroup.removeViewAt(0);
                pullToRefreshLayout.addView(child);
                child = viewGroup.getChildAt(0);
            }

            viewGroup.addView(pullToRefreshLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
