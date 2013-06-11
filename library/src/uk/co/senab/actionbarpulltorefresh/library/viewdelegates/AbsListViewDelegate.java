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

package uk.co.senab.actionbarpulltorefresh.library.viewdelegates;

import android.view.View;
import android.widget.AbsListView;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * FIXME
 */
public class AbsListViewDelegate
        extends PullToRefreshAttacher.ViewDelegate {

    public static final Class SUPPORTED_VIEW_CLASS = AbsListView.class;

    @Override
    public boolean isScrolledToTop(View view) {
        AbsListView absListView = (AbsListView) view;
        if (absListView.getCount() == 0) {
            return true;
        } else if (absListView.getFirstVisiblePosition() == 0) {
            final View firstVisibleChild = absListView.getChildAt(0);
            return firstVisibleChild != null && firstVisibleChild.getTop() >= 0;
        }
        return false;
    }
}
