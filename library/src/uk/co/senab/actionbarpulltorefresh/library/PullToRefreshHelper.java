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
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PullToRefreshHelper implements View.OnTouchListener {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "PullToRefreshHelper";

    static final float PERCENTAGE_VIEW_MAX_SCROLL = 0.4f;

    private final View mRefreshableView;
    private final Delegate mViewDelegate;
    private final ViewGroup mWindowDecorView;

    private final View mHeaderView;
    private final TextView mHeaderLabel;
    private final ProgressBar mHeaderProgressBar;

    private int mPullToRefreshLabelResId = R.string.pull_to_refresh_pull_label;
    private int mRefreshingLabelResId = R.string.pull_to_refresh_refreshing_label;

    private final Animation mHeaderInAnimation, mHeaderOutAnimation;
    private final Animation.AnimationListener mAnimationListener;

    private final int mTouchSlop;
    private float mInitialMotionY, mLastMotionY;

    private boolean mIsBeingDragged;
    private boolean mIsRefreshing;

    private OnRefreshListener mRefreshListener;

    public PullToRefreshHelper(Activity activity, android.widget.ScrollView view) {
        this(activity, view, new ScrollViewDelegate());
    }

    public PullToRefreshHelper(Activity activity, android.widget.AbsListView view) {
        this(activity, view, new AbsListViewDelegate());
    }

    public <V extends View> PullToRefreshHelper(Activity activity, V view,
            Delegate delegate) {
        this(activity, view, delegate, R.layout.default_header, R.anim.fade_in,
                R.anim.fade_out);
    }

    public <V extends View> PullToRefreshHelper(Activity activity, V view,
            Delegate viewDelegate, int headerLayoutRes,
            int animInRes, int animOutRes) {
        mWindowDecorView = (ViewGroup) activity.getWindow().getDecorView();

        // TODO HACK! ICS's decor view doesn't seem to fit system windows.
        // May cause problems, need to investigate
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 16) {
            mWindowDecorView.setFitsSystemWindows(true);
        }

        // View to detect refreshes for
        mRefreshableView = view;
        mRefreshableView.setOnTouchListener(this);
        mViewDelegate = viewDelegate;

        mHeaderView = LayoutInflater.from(activity)
                .inflate(headerLayoutRes, mWindowDecorView, false);
        if (mHeaderView == null) {
            throw new IllegalArgumentException("Must supply valid layout id for header.");
        }
        mHeaderView.setVisibility(View.GONE);
        mWindowDecorView.addView(mHeaderView);

        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.ptr_progress);
        mHeaderLabel = (TextView) mHeaderView.findViewById(R.id.ptr_text);

        mAnimationListener = new AnimationCallback();
        mHeaderInAnimation = AnimationUtils.loadAnimation(activity, animInRes);
        mHeaderOutAnimation = AnimationUtils.loadAnimation(activity, animOutRes);
        mHeaderOutAnimation.setAnimationListener(mAnimationListener);

        mHeaderLabel.setText(mPullToRefreshLabelResId);

        mTouchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();
    }

    public void onRefreshComplete() {
        reset();
    }

    @Override
    public final boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                // If we're already refreshing, ignore
                if (mIsRefreshing) {
                    return false;
                }

                final float y = event.getY();
                final float yDiff = y - mLastMotionY;

                if (!mIsBeingDragged && yDiff > 0f && Math.abs(yDiff) > mTouchSlop
                        && mViewDelegate.isScrolledToTop(mRefreshableView)) {
                    // Reset initial y to be the starting y for pulling
                    mInitialMotionY = y;
                    mIsBeingDragged = true;
                    onPullStarted();
                }

                if (mIsBeingDragged) {
                    mLastMotionY = y;

                    if (mViewDelegate.isScrolledToTop(mRefreshableView)) {
                        onPull();
                    } else {
                        // We were being dragged, but not any more.
                        mIsBeingDragged = false;
                        mLastMotionY = mInitialMotionY = 0f;
                        onPullEnded();
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                // If we're already refreshing, ignore
                if (!mIsRefreshing) {
                    mLastMotionY = mInitialMotionY = event.getY();
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    // We were being dragged, but not any more.
                    mIsBeingDragged = false;
                    onPullEnded();
                }
                mLastMotionY = mInitialMotionY = 0f;
                break;
            }
        }

        // Always return false as we only want to observe events
        return false;
    }

    public void setRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    void onPullStarted() {
        if (DEBUG) {
            Log.d(LOG_TAG, "onPullStarted");
        }
        showHeader();
    }

    void onPull() {
        if (DEBUG) {
            Log.d(LOG_TAG, "onPull");
        }
        mHeaderProgressBar.setVisibility(View.VISIBLE);

        final float scrollToRefresh = mRefreshableView.getHeight() * PERCENTAGE_VIEW_MAX_SCROLL;
        final float scrollLength = mLastMotionY - mInitialMotionY;

        if (scrollLength < scrollToRefresh) {
            mHeaderProgressBar.setProgress(
                    Math.round(mHeaderProgressBar.getMax() * scrollLength / scrollToRefresh));
        } else {
            startRefresh();
        }
    }

    void onPullEnded() {
        if (DEBUG) {
            Log.d(LOG_TAG, "onPullEnded");
        }
        if (!mIsRefreshing) {
            reset();
        }
    }

    private void startRefresh() {
        if (DEBUG) {
            Log.d(LOG_TAG, "startRefresh");
        }
        if (mRefreshListener != null) {
            mIsRefreshing = true;

            // Call listener
            mRefreshListener.onRefresh(mRefreshableView);

            mHeaderLabel.setText(mRefreshingLabelResId);
            mHeaderProgressBar.setIndeterminate(true);
        } else {
            reset();
        }
    }

    private void showHeader() {
        mHeaderView.startAnimation(mHeaderInAnimation);
        mHeaderView.setVisibility(View.VISIBLE);
    }

    private void hideHeader() {
        mHeaderView.startAnimation(mHeaderOutAnimation);
        mHeaderView.setVisibility(View.GONE);
    }

    private void reset() {
        if (DEBUG) {
            Log.d(LOG_TAG, "reset()");
        }
        mIsRefreshing = false;
        mIsBeingDragged = false;
        hideHeader();
    }

    /**
     * Simple Listener to listen for any callbacks to Refresh.
     *
     * @author Chris Banes
     */
    public static interface OnRefreshListener {

        /**
         * onRefresh will be called for both a Pull from start, and Pull from end
         */
        public void onRefresh(View view);

    }

    private class AnimationCallback implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == mHeaderOutAnimation) {
                // Reset Progress Bar
                mHeaderProgressBar.setVisibility(View.GONE);
                mHeaderProgressBar.setProgress(0);
                mHeaderProgressBar.setIndeterminate(false);

                // Reset Inner Content
                mHeaderLabel.setVisibility(View.VISIBLE);
                mHeaderLabel.setText(mPullToRefreshLabelResId);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    public interface Delegate {
        boolean isScrolledToTop(View view);
    }

}
