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

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class ActionBarPullToRefresh {

    public static SetupWizard from(Activity activity) {
        return new SetupWizard(activity);
    }

    public static final class SetupWizard {
        private final Activity mActivity;
        private Options mOptions;
        private int[] refreshableViewIds;
        private OnRefreshListener mOnRefreshListener;
        private ViewGroup mViewGroupToInsertInto;

        private SetupWizard(Activity activity) {
            mActivity = activity;
        }

        public SetupWizard options(Options options) {
            mOptions = options;
            return this;
        }

        public SetupWizard allChildrenArePullable() {
            refreshableViewIds = null;
            return this;
        }

        public SetupWizard theseChildrenArePullable(int... viewIds) {
            refreshableViewIds = viewIds;
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

            if (refreshableViewIds != null) {
                pullToRefreshLayout.addChildrenAsPullable(refreshableViewIds);
            } else {
                pullToRefreshLayout.addAllChildrenAsPullable();
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
