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
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * FIXME
 */
public class PullToRefreshLayout extends FrameLayout {

    private PullToRefreshAttacher mPullToRefreshAttacher;
    private View mRefreshableView;

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() == 0) {
            super.addView(child, index, params);
            mRefreshableView = child;
        } else {
            throw new IllegalArgumentException("PullToRefreshLayout can only have one child.");
        }
    }

    /**
     * Set the {@link PullToRefreshAttacher} to be used with this layout. The view which is added
     * to this layout will automatically be added as a refreshable-view in the attacher.
     */
    public void setPullToRefreshAttacher(PullToRefreshAttacher attacher,
            PullToRefreshAttacher.OnRefreshListener refreshListener) {
        if (mPullToRefreshAttacher != null && mRefreshableView != null) {
            mPullToRefreshAttacher.removeRefreshableView(mRefreshableView);
        }

        mPullToRefreshAttacher = attacher;

        if (attacher != null && mRefreshableView != null) {
            attacher.addRefreshableView(mRefreshableView, null, refreshListener, false);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mPullToRefreshAttacher != null && mRefreshableView != null) {
            return mPullToRefreshAttacher.onInterceptTouchEvent(mRefreshableView, event);
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPullToRefreshAttacher != null && mRefreshableView != null) {
            return mPullToRefreshAttacher.onTouchEvent(mRefreshableView, event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mPullToRefreshAttacher != null && mRefreshableView != null) {
            mPullToRefreshAttacher.onConfigurationChanged(newConfig);
        }
    }
}
