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

package uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * @see uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout
 */
public class PullToRefreshLayout extends
        uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout {

    public PullToRefreshLayout(Context context) {
        super(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected PullToRefreshAttacher createPullToRefreshAttacher(Activity activity,
            Options options) {
        return new AbcPullToRefreshAttacher(activity, options);
    }
}
