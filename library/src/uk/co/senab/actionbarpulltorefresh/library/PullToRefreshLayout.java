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

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * FIXME
 */
public class PullToRefreshLayout extends FrameLayout {

    private PullToRefreshAttacher mPullToRefreshAttacher;

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set the {@link PullToRefreshAttacher} to be used with this layout. The view which is added
     * to this layout will automatically be added as a refreshable-view in the attacher.
     */
    public void setPullToRefreshAttacher(PullToRefreshAttacher attacher,
            PullToRefreshAttacher.OnRefreshListener refreshListener) {
        View view;
        for (int i = 0, z = getChildCount(); i < z; i++) {
            view = getChildAt(i);

            if (mPullToRefreshAttacher != null) {
                mPullToRefreshAttacher.removeRefreshableView(view);
            }

            if (attacher != null) {
                attacher.addRefreshableView(view, null, refreshListener, false);
            }
        }

        mPullToRefreshAttacher = attacher;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mPullToRefreshAttacher != null && getChildCount() > 0) {
            return mPullToRefreshAttacher.onInterceptTouchEvent(getChildAt(0), event);
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPullToRefreshAttacher != null && getChildCount() > 0) {
            return mPullToRefreshAttacher.onTouchEvent(getChildAt(0), event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mPullToRefreshAttacher != null) {
            mPullToRefreshAttacher.onConfigurationChanged(newConfig);
        }
    }
}
