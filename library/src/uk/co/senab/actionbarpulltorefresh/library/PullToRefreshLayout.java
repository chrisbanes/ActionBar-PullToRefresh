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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * FIXME
 */
public class PullToRefreshLayout extends FrameLayout {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "PullToRefreshLayout";

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
                if (DEBUG) Log.d(LOG_TAG, "Adding View to Attacher: " + view);
                attacher.addRefreshableView(view, null, refreshListener, false);
            }
        }

        mPullToRefreshAttacher = attacher;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (DEBUG) Log.d(LOG_TAG, "onInterceptTouchEvent. " + event.toString());

        if (mPullToRefreshAttacher != null && getChildCount() > 0) {
            View touchTarget = getChildForTouchEvent(event.getX(), event.getY());
            if (touchTarget != null && mPullToRefreshAttacher
                    .onInterceptTouchEvent(touchTarget, event)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (DEBUG) Log.d(LOG_TAG, "onTouchEvent. " + event.toString());

        if (mPullToRefreshAttacher != null) {
            View touchTarget = getChildForTouchEvent(event.getX(), event.getY());
            return mPullToRefreshAttacher.onTouchEvent(touchTarget, event);
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

    private View getChildForTouchEvent(final float x, final float y) {
        View child;
        for (int z = getChildCount() - 1;  z >= 0 ; z--) {
            child = getChildAt(z);
            if (child.isShown() && x >= child.getLeft() && x <= child.getRight()
                    && y >= child.getTop() && y <= child.getBottom()) {
                if (DEBUG) Log.d(LOG_TAG, "Got Child for Touch Event: " + child);
                return child;
            }
        }
        return null;
    }
}
