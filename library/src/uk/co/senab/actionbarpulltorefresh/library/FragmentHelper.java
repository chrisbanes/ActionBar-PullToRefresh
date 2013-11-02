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

import android.view.View;
import android.view.ViewGroup;

/**
 * Class to help integrate {@link PullToRefreshLayout} with your Fragments.
 */
public class FragmentHelper {

    /**
     * Wrap a ListFragment's View with a {@link PullToRefreshLayout}. The easiest to use this is so:
     * <pre>
     * {@code
            @Override
            public void onViewCreated(View view, Bundle savedInstanceState) {
               super.onViewCreated(view, savedInstanceState);
               mPullToRefreshLayout = FragmentHelper.wrapListFragmentView(view);
            }
       }
     * </pre>
     *
     * @param view Fragment's content view
     * @return The created {@link PullToRefreshLayout}
     */
    public static PullToRefreshLayout wrapListFragmentView(View view) {
        PullToRefreshLayout ptrLayout = new PullToRefreshLayout(view.getContext());

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            // Move all children to PullToRefreshLayout. This code looks a bit silly but the child
            // indices change every time we remove a View (so we can't just iterate through)
            View child = viewGroup.getChildAt(0);
            while (child != null) {
                viewGroup.removeViewAt(0);
                ptrLayout.addView(child);
                child = viewGroup.getChildAt(0);
            }

            viewGroup.addView(ptrLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            ptrLayout.addRefreshableView(android.R.id.list);
            ptrLayout.addRefreshableView(android.R.id.empty);
        }

        return ptrLayout;
    }

}
