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
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashSet;

import uk.co.senab.actionbarpulltorefresh.library.listeners.HeaderViewListener;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

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

    public SetupWizard setup(Activity activity) {
        return new SetupWizard(activity);
    }

    /**
     * Manually set this Attacher's refreshing state. The header will be
     * displayed or hidden as requested.
     *
     * @param refreshing
     *            - Whether the attacher should be in a refreshing state,
     */
    public final void setRefreshing(boolean refreshing) {
        ensureAttacher();
        mPullToRefreshAttacher.setRefreshing(refreshing);
    }

    /**
     * @return true if this Attacher is currently in a refreshing state.
     */
    public final boolean isRefreshing() {
        ensureAttacher();
        return mPullToRefreshAttacher.isRefreshing();
    }

    /**
     * Call this when your refresh is complete and this view should reset itself
     * (header view will be hidden).
     *
     * This is the equivalent of calling <code>setRefreshing(false)</code>.
     */
    public final void setRefreshComplete() {
        ensureAttacher();
        mPullToRefreshAttacher.setRefreshComplete();
    }

    /**
     * Set a {@link uk.co.senab.actionbarpulltorefresh.library.listeners.HeaderViewListener} which is called when the visibility
     * state of the Header View has changed.
     *
     * @param listener
     */
    public final void setHeaderViewListener(HeaderViewListener listener) {
        ensureAttacher();
        mPullToRefreshAttacher.setHeaderViewListener(listener);
    }

    /**
     * @return The Header View which is displayed when the user is pulling, or
     *         we are refreshing.
     */
    public final View getHeaderView() {
        ensureAttacher();
        return mPullToRefreshAttacher.getHeaderView();
    }

    /**
     * @return The HeaderTransformer currently used by this Attacher.
     */
    public HeaderTransformer getHeaderTransformer() {
        ensureAttacher();
        return mPullToRefreshAttacher.getHeaderTransformer();
    }


    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        if (DEBUG) {
            Log.d(LOG_TAG, "onInterceptTouchEvent. " + event.toString());
        }
        if (mPullToRefreshAttacher != null && getChildCount() > 0) {
            return mPullToRefreshAttacher.onInterceptTouchEvent(event);
        }
        return false;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        if (DEBUG) {
            Log.d(LOG_TAG, "onTouchEvent. " + event.toString());
        }
        if (mPullToRefreshAttacher != null) {
            return mPullToRefreshAttacher.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        // Destroy the PullToRefreshAttacher
        if (mPullToRefreshAttacher != null) {
            mPullToRefreshAttacher.destroy();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (mPullToRefreshAttacher != null) {
            mPullToRefreshAttacher.onConfigurationChanged(newConfig);
        }
        super.onConfigurationChanged(newConfig);
    }

    void setPullToRefreshAttacher(PullToRefreshAttacher attacher) {
        if (attacher != null) {
            for (int i = 0, z = getChildCount(); i < z; i++) {
                attacher.addRefreshableView(getChildAt(i), null);
            }
        }
        mPullToRefreshAttacher = attacher;
    }

    void setPullToRefreshAttacher(PullToRefreshAttacher attacher, int[] refreshableViewIds) {
        if (attacher != null && refreshableViewIds.length > 0) {
            for (int i = 0, z = refreshableViewIds.length; i < z; i++) {
                attacher.addRefreshableView(findViewById(refreshableViewIds[i]), null);
            }
        }
        mPullToRefreshAttacher = attacher;
    }

    protected PullToRefreshAttacher createPullToRefreshAttacher(Activity activity,
            Options options) {
        return new PullToRefreshAttacher(activity, options != null ? options : createDefaultOptions());
    }

    protected Options createDefaultOptions() {
        return new Options();
    }

    private void ensureAttacher() {
        if (mPullToRefreshAttacher == null) {
            throw new IllegalStateException("You need to setup the PullToRefreshLayout before using it");
        }
    }

    public final class SetupWizard {
        final Activity mActivity;
        Options mOptions;
        int[] refreshableViewIds;
        OnRefreshListener mOnRefreshListener;

        SetupWizard(Activity activity) {
            mActivity = activity;
        }

        public SetupWizard options(Options options) {
            mOptions = options;
            return this;
        }

        public SetupWizard allViewsAreRefreshable() {
            refreshableViewIds = null;
            return this;
        }

        public SetupWizard theseViewsAreRefreshable(int... viewIds) {
            refreshableViewIds = viewIds;
            return this;
        }

        public SetupWizard withListener(OnRefreshListener listener) {
            mOnRefreshListener = listener;
            return this;
        }

        public void done() {
            PullToRefreshAttacher attacher = createPullToRefreshAttacher(mActivity, mOptions);
            attacher.setOnRefreshListener(mOnRefreshListener);

            if (refreshableViewIds != null) {
                setPullToRefreshAttacher(attacher, refreshableViewIds);
            } else {
                setPullToRefreshAttacher(attacher);
            }
        }
    }
}
