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
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public final class PullToRefreshAttacher implements View.OnTouchListener {

    /**
     * Default configuration values
     */
    private static final int DEFAULT_HEADER_LAYOUT = R.layout.default_header;
    private static final int DEFAULT_ANIM_HEADER_IN = R.anim.fade_in;
    private static final int DEFAULT_ANIM_HEADER_OUT = R.anim.fade_out;
    private static final float DEFAULT_REFRESH_SCROLL_DISTANCE = 0.5f;

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "PullToRefreshAttacher";

    private final View mRefreshableView;
    private final Delegate mDelegate;
    private final View mHeaderView;

    private final Animation mHeaderInAnimation, mHeaderOutAnimation;

    private final int mTouchSlop;
    private final float mRefreshScrollDistance;
    private float mInitialMotionY, mLastMotionY;
    private boolean mIsBeingDragged, mIsRefreshing, mIsHandlingTouchEvent;

    private OnRefreshListener mRefreshListener;
    private final HeaderTransformer mHeaderTransformer;

    public PullToRefreshAttacher(Activity activity, View view) {
        this(activity, view, new Options());
    }

    public <V extends View> PullToRefreshAttacher(Activity activity, V view, Options options) {
        if (options == null) {
            Log.i(LOG_TAG, "Given null options so using default options.");
            options = new Options();
        }

        // Copy necessary values from options
        mRefreshScrollDistance = options.refreshScrollDistance;

        // View to detect refreshes for
        mRefreshableView = view;
        mRefreshableView.setOnTouchListener(this);

        // Delegate
        Delegate delegate = options.delegate;
        if (delegate == null) {
            delegate = InstanceCreationUtils.getBuiltInDelegateForView(view);
            if (delegate == null) {
                throw new IllegalArgumentException("No delegate given. Please provide one.");
            }
        }
        mDelegate = delegate;

        // Header Transformer
        mHeaderTransformer = options.headerTransformer != null ? options.headerTransformer
                : new DefaultHeaderTransformer();

        // Create animations for use later
        mHeaderInAnimation = AnimationUtils.loadAnimation(activity, options.headerInAnimation);
        mHeaderOutAnimation = AnimationUtils.loadAnimation(activity, options.headerOutAnimation);
        if (mHeaderOutAnimation != null) {
            mHeaderOutAnimation.setAnimationListener(new AnimationCallback());
        }

        // Get touch slop for use later
        mTouchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();

        // Get Window Decor View
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();

        // Create Header view and then add to Decor View
        mHeaderView = LayoutInflater.from(delegate.getContextForInflater(activity))
                .inflate(options.headerLayout, decorView, false);
        if (mHeaderView == null) {
            throw new IllegalArgumentException("Must supply valid layout id for header.");
        }
        mHeaderView.setVisibility(View.GONE);

        // Create DecorChildLayout which will move all of the system's decor view's children + the
        // Header View to itself. See DecorChildLayout for more info.
        DecorChildLayout decorContents = new DecorChildLayout(activity, decorView,
                mHeaderView);

        // Now add the DecorChildLayout to the decor view
        decorView.addView(decorContents, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        // Notify transformer
        mHeaderTransformer.onViewCreated(mHeaderView);
    }

    /**
     * Manually set this Attacher's refreshing state. The header will be displayed or hidden as
     * requested.
     * @param refreshing - Whether the attacher should be in a refreshing state,
     */
    public final void setRefreshing(boolean refreshing) {
        setRefreshingInt(refreshing, false);
    }

    /**
     * @return true if this Attacher is currently in a refreshing state.
     */
    public final boolean isRefreshing() {
        return mIsRefreshing;
    }

    /**
     * Call this when your refresh is complete and this view should reset itself (header view
     * will be hidden).
     *
     * This is the equivalent of calling <code>setRefreshing(false)</code>.
     */
    public final void setRefreshComplete() {
        setRefreshingInt(false, false);
    }

    @Override
    public final boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                // If we're already refreshing or not handling the event, ignore it
                if (mIsRefreshing || !mIsHandlingTouchEvent) {
                    return false;
                }

                final float y = event.getY();

                // We're not currently being dragged so check to see if the user has scrolled enough
                if (!mIsBeingDragged && (y - mInitialMotionY) > mTouchSlop) {
                    mIsBeingDragged = true;
                    onPullStarted();
                }

                if (mIsBeingDragged) {
                    final float yDx = y - mLastMotionY;

                    /**
                     * Check to see if the user is scrolling the right direction (down).
                     * We allow a small scroll up which is the check against negative touch slop.
                     */
                    if (yDx >= -mTouchSlop) {
                        onPull(y);
                        // Only record the y motion if the user has scrolled down.
                        if (yDx > 0f) {
                            mLastMotionY = y;
                        }
                    } else {
                        resetTouch();
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                // If we're already refreshing, ignore
                if (!mIsRefreshing && mDelegate.isScrolledToTop(mRefreshableView)) {
                    mIsHandlingTouchEvent = true;
                    mInitialMotionY = event.getY();
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                resetTouch();
                break;
            }
        }

        // Always return false as we only want to observe events
        return false;
    }

    private void resetTouch() {
        if (mIsBeingDragged) {
            // We were being dragged, but not any more.
            mIsBeingDragged = false;
            onPullEnded();
        }
        mIsHandlingTouchEvent = false;
        mInitialMotionY = mLastMotionY = 0f;
    }

    /**
     * Set a listener for when a refresh is started.
     * @param listener - OnRefreshListener to be invoked when a refresh is started.
     */
    public void setRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    void onPullStarted() {
        if (DEBUG) {
            Log.d(LOG_TAG, "onPullStarted");
        }
        // Show Header
        if (mHeaderInAnimation != null) {
            mHeaderView.startAnimation(mHeaderInAnimation);
        }
        mHeaderView.setVisibility(View.VISIBLE);
    }

    void onPull(float y) {
        if (DEBUG) {
            Log.d(LOG_TAG, "onPull");
        }

        final float pxScrollForRefresh = mRefreshableView.getHeight() * mRefreshScrollDistance;
        final float scrollLength = y - mInitialMotionY;

        if (scrollLength < pxScrollForRefresh) {
            mHeaderTransformer.onPulled(scrollLength / pxScrollForRefresh);
        } else {
            setRefreshingInt(true, true);
        }
    }

    void onPullEnded() {
        if (DEBUG) {
            Log.d(LOG_TAG, "onPullEnded");
        }
        if (!mIsRefreshing) {
            reset(true);
        }
    }

    private void setRefreshingInt(boolean refreshing, boolean fromTouch) {
        if (DEBUG) {
            Log.d(LOG_TAG, "setRefreshingInt: " + refreshing);
        }
        // Check to see if we need to do anything
        if (mIsRefreshing == refreshing) {
            return;
        }

        if (refreshing && canRefresh(fromTouch)) {
            startRefresh(fromTouch);
        } else {
            reset(fromTouch);
        }

        mIsRefreshing = refreshing;
    }

    private boolean canRefresh(boolean fromTouch) {
        return !mIsRefreshing && (!fromTouch || mRefreshListener != null);
    }

    private void reset(boolean fromTouch) {
        if (mHeaderView.getVisibility() != View.GONE) {
            // Hide Header
            if (mHeaderOutAnimation != null) {
                mHeaderView.startAnimation(mHeaderOutAnimation);
                // HeaderTransformer.onReset() is called once the animation has finished
            } else {
                // As we're not animating, call the header transformer now
                mHeaderTransformer.onReset();
            }
            mHeaderView.setVisibility(View.GONE);
        }
    }

    private void startRefresh(boolean fromTouch) {
        // Call OnRefreshListener if this call has originated from a touch event
        if (fromTouch) {
            mRefreshListener.onRefreshStarted(mRefreshableView);
        }
        // Call Transformer
        mHeaderTransformer.onRefreshStarted();

        // Make sure header is visible.
        // TODO Use header in anim
        if (mHeaderView.getVisibility() != View.VISIBLE) {
            mHeaderView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Simple Listener to listen for any callbacks to Refresh.
     */
    public interface OnRefreshListener {
        /**
         * Called when the user has initiated a refresh by pulling.
         * @param view - View which the user has started the refresh from.
         */
        public void onRefreshStarted(View view);
    }

    public static abstract class HeaderTransformer {
        /**
         * Called whether the header view has been inflated from the resources defined in
         * {@link Options#headerLayout}.
         *
         * @param headerView - inflated header view.
         */
        public abstract void onViewCreated(View headerView);

        /**
         * Called when the header should be reset. You should update any child views to reflect this.
         * <p/>
         * You should <strong>not</strong> change the
         * visibility of the header view.
         */
        public abstract void onReset();

        /**
         * Called the user has pulled on the scrollable view.
         * @param  percentagePulled - value between 0.0f and 1.0f depending on how far the user has pulled.
         */
        public abstract void onPulled(float percentagePulled);

        /**
         * Called when a refresh has begun. Theoretically this call is similar to that provided
         * from {@link OnRefreshListener} but is more suitable for header view updates.
         */
        public abstract void onRefreshStarted();
    }

    public static abstract class Delegate {

        /**
         * Allows you to provide support for View which do not have built-in support. In this
         * method you should cast <code>view</code> to it's native class, and check if it is
         * scrolled to the top.
         *
         * @param view The view this PullToRefreshAttacher was created with.
         * @return true if <code>view</code> is scrolled to the top.
         */
        public abstract boolean isScrolledToTop(View view);

        /**
         * @return Context which should be used for inflating the header layout
         */
        public Context getContextForInflater(Activity activity) {
            if (Build.VERSION.SDK_INT >= 14) {
                return activity.getActionBar().getThemedContext();
            } else {
                return activity;
            }
        }
    }

    public static final class Options {
        /**
         * Delegate instance which will be used. If null, we will try to find an instance which
         * will work for the given scrollable view.
         */
        public Delegate delegate = null;

        /**
         * The layout resource ID which should be inflated to be displayed above the Action Bar
         */
        public int headerLayout = DEFAULT_HEADER_LAYOUT;

        /**
         * The header transformer to be used to transfer the header view. If null, an instance of
         * {@link DefaultHeaderTransformer} will be used.
         */
        public HeaderTransformer headerTransformer = null;

        /**
         * The anim resource ID which should be started when the header is being hidden.
         */
        public int headerOutAnimation = DEFAULT_ANIM_HEADER_OUT;

        /**
         * The anim resource ID which should be started when the header is being shown.
         */
        public int headerInAnimation = DEFAULT_ANIM_HEADER_IN;

        /**
         * The percentage of the refreshable view that needs to be scrolled before a refresh
         * is initiated.
         */
        public float refreshScrollDistance = DEFAULT_REFRESH_SCROLL_DISTANCE;
    }

    private class AnimationCallback implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == mHeaderOutAnimation) {
                mHeaderTransformer.onReset();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * Default Header Transformer.
     */
    public static class DefaultHeaderTransformer extends HeaderTransformer {
        private TextView mHeaderTextView;
        private ProgressBar mHeaderProgressBar;

        @Override
        public void onViewCreated(View headerView) {
            // Get ProgressBar and TextView. Also set initial text on TextView
            mHeaderProgressBar = (ProgressBar) headerView.findViewById(R.id.ptr_progress);
            mHeaderTextView = (TextView) headerView.findViewById(R.id.ptr_text);

            // Call onReset to make sure that the View is consistent
            onReset();
        }

        @Override
        public void onReset() {
            // Reset Progress Bar
            if (mHeaderProgressBar != null) {
                mHeaderProgressBar.setVisibility(View.GONE);
                mHeaderProgressBar.setProgress(0);
                mHeaderProgressBar.setIndeterminate(false);
            }

            // Reset Text View
            if (mHeaderTextView != null) {
                mHeaderTextView.setVisibility(View.VISIBLE);
                mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
            }
        }

        @Override
        public void onPulled(float percentagePulled) {
            if (mHeaderProgressBar != null) {
                mHeaderProgressBar.setVisibility(View.VISIBLE);
                mHeaderProgressBar
                        .setProgress(Math.round(mHeaderProgressBar.getMax() * percentagePulled));
            }
        }

        @Override
        public void onRefreshStarted() {
            if (mHeaderTextView != null) {
                mHeaderTextView.setText(R.string.pull_to_refresh_refreshing_label);
            }
            if (mHeaderProgressBar != null) {
                mHeaderProgressBar.setIndeterminate(true);
            }
        }
    }

    /**
     * This class allows us to insert a layer in between the system decor view and the actual decor.
     * (e.g. Action Bar views). This is needed so we can receive a call to fitSystemWindows(Rect)
     * so we can adjust the header view to fit the system windows too.
     */
    final static class DecorChildLayout extends FrameLayout {
        private ViewGroup mHeaderViewWrapper;

        DecorChildLayout(Context context, ViewGroup systemDecorView, View headerView) {
            super(context);

            // Move all children from decor view to here
            for (int i = 0, z = systemDecorView.getChildCount() ; i < z ; i++) {
                View child = systemDecorView.getChildAt(i);
                systemDecorView.removeView(child);
                addView(child);
            }

            /**
             * Wrap the Header View in a FrameLayout and add it to this view. It is wrapped
             * so any inset changes do not affect the actual header view.
             */
            mHeaderViewWrapper = new FrameLayout(context);
            mHeaderViewWrapper.addView(headerView);
            addView(mHeaderViewWrapper, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @Override
        protected boolean fitSystemWindows(Rect insets) {
            if (DEBUG) {
                Log.d(LOG_TAG, "fitSystemWindows: " + insets.toString());
            }

            // Adjust the Header View's padding to take the insets into account
            mHeaderViewWrapper.setPadding(insets.left, insets.top, insets.right, insets.bottom);

            // Call return super so that the rest of the
            return super.fitSystemWindows(insets);
        }
    }

}
