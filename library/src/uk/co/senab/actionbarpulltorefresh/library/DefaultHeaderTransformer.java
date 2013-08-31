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
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Default Header Transformer.
 */
public class DefaultHeaderTransformer extends PullToRefreshAttacher.HeaderTransformer {

    private ViewGroup mContentLayout;
    private TextView mHeaderTextView;
    private ProgressBar mHeaderProgressBar;

    private CharSequence mPullRefreshLabel, mRefreshingLabel, mReleaseLabel;

    private boolean mUseCustomProgressColor = false;
    private int mProgressDrawableColor;

    private final Interpolator mInterpolator = new AccelerateInterpolator();

    protected DefaultHeaderTransformer() {
        final int min = getMinimumApiLevel();
        if (Build.VERSION.SDK_INT < min) {
            throw new IllegalStateException("This HeaderTransformer is designed to run on SDK "
                    + min
                    + "+. If using ActionBarSherlock or ActionBarCompat you should use the appropriate provided extra.");
        }
    }

    @Override
    public void onViewCreated(Activity activity, View headerView) {
        // Get ProgressBar and TextView. Also set initial text on TextView
        mHeaderProgressBar = (ProgressBar) headerView.findViewById(R.id.ptr_progress);
        mHeaderTextView = (TextView) headerView.findViewById(R.id.ptr_text);

        // Apply any custom ProgressBar colors
        applyProgressBarColor();

        // Labels to display
        mPullRefreshLabel = activity.getString(R.string.pull_to_refresh_pull_label);
        mRefreshingLabel = activity.getString(R.string.pull_to_refresh_refreshing_label);
        mReleaseLabel = activity.getString(R.string.pull_to_refresh_release_label);

        // Retrieve the Action Bar size from the Activity's theme
        mContentLayout = (ViewGroup) headerView.findViewById(R.id.ptr_content);
        if (mContentLayout != null) {
            mContentLayout.getLayoutParams().height = getActionBarSize(activity);
            mContentLayout.requestLayout();
        }

        // Retrieve the Action Bar background from the Activity's theme (see #93).
        Drawable abBg = getActionBarBackground(activity);
        if (abBg != null) {
            // If we do not have a opaque background we just display a solid solid behind it
            if (abBg.getOpacity() != PixelFormat.OPAQUE) {
                View view = headerView.findViewById(R.id.ptr_text_opaque_bg);
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            mHeaderTextView.setBackgroundDrawable(abBg);
        }

        // Retrieve the Action Bar Title Style from the Action Bar's theme
        Context abContext = headerView.getContext();
        final int titleTextStyle = getActionBarTitleStyle(abContext);
        if (titleTextStyle != 0) {
            mHeaderTextView.setTextAppearance(abContext, titleTextStyle);
        }

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
            mHeaderTextView.setText(mPullRefreshLabel);
        }

        // Reset the Content Layout
        if (mContentLayout != null) {
            mContentLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPulled(float percentagePulled) {
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            final float progress = mInterpolator.getInterpolation(percentagePulled);
            mHeaderProgressBar.setProgress(Math.round(mHeaderProgressBar.getMax() * progress));
        }
    }

    @Override
    public void onRefreshStarted() {
        if (mHeaderTextView != null) {
            mHeaderTextView.setText(mRefreshingLabel);
        }
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            mHeaderProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public void onReleaseToRefresh() {
        if (mHeaderTextView != null) {
            mHeaderTextView.setText(mReleaseLabel);
        }
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setProgress(mHeaderProgressBar.getMax());
        }
    }

    @Override
    public void onRefreshMinimized() {
        // Here we fade out most of the header, leaving just the progress bar
        if (mContentLayout != null) {
            mContentLayout.startAnimation(AnimationUtils
                    .loadAnimation(mContentLayout.getContext(), R.anim.fade_out));
            mContentLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Set color to apply to the progress bar. Automatically enables usage of the custom color. Use
     * {@link #setProgressBarColorEnabled(boolean)} to disable and re-enable the custom color usage.
     * <p/>
     * The best way to apply a color is to load the color from resources: {@code
     * setProgressBarColor(getResources().getColor(R.color.your_color_name))}.
     *
     * @param color The color to use.
     */
    public void setProgressBarColor(int color) {
        mProgressDrawableColor = color;
        setProgressBarColorEnabled(true);
    }

    /**
     * Enable or disable the use of a custom progress bar color. You can set what color to use with
     * {@link #setProgressBarColor(int)}, which also automatically enables custom color usage.
     */
    public void setProgressBarColorEnabled(boolean enabled) {
        mUseCustomProgressColor = enabled;
        applyProgressBarColor();
    }


    /**
     * Set Text to show to prompt the user is pull (or keep pulling).
     *
     * @param pullText - Text to display.
     */
    public void setPullText(CharSequence pullText) {
        mPullRefreshLabel = pullText;
        if (mHeaderTextView != null) {
            mHeaderTextView.setText(mPullRefreshLabel);
        }
    }

    /**
     * Set Text to show to tell the user that a refresh is currently in progress.
     *
     * @param refreshingText - Text to display.
     */
    public void setRefreshingText(CharSequence refreshingText) {
        mRefreshingLabel = refreshingText;
    }

    /**
     * Set Text to show to tell the user has scrolled enough to refresh.
     *
     * @param releaseText - Text to display.
     */
    public void setReleaseText(CharSequence releaseText) {
        mReleaseLabel = releaseText;
    }

    private void applyProgressBarColor() {
        if (mHeaderProgressBar != null) {
            if (mUseCustomProgressColor) {
                mHeaderProgressBar.getProgressDrawable()
                        .setColorFilter(mProgressDrawableColor, PorterDuff.Mode.SRC_ATOP);
                mHeaderProgressBar.getIndeterminateDrawable()
                        .setColorFilter(mProgressDrawableColor, PorterDuff.Mode.SRC_ATOP);
            } else {
                mHeaderProgressBar.getProgressDrawable().clearColorFilter();
                mHeaderProgressBar.getIndeterminateDrawable().clearColorFilter();
            }
        }
    }

    protected Drawable getActionBarBackground(Context context) {
        int[] android_styleable_ActionBar = {android.R.attr.background};

        // Need to get resource id of style pointed to from actionBarStyle
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarStyle, outValue, true);
        // Now get action bar style values...
        TypedArray abStyle = context.getTheme().obtainStyledAttributes(outValue.resourceId,
                android_styleable_ActionBar);
        try {
            // background is the first attr in the array above so it's index is 0.
            return abStyle.getDrawable(0);
        } finally {
            abStyle.recycle();
        }
    }

    protected int getActionBarSize(Context context) {
        int[] attrs = {android.R.attr.actionBarSize};
        TypedArray values = context.getTheme().obtainStyledAttributes(attrs);
        try {
            return values.getDimensionPixelSize(0, 0);
        } finally {
            values.recycle();
        }
    }

    protected int getActionBarTitleStyle(Context context) {
        int[] android_styleable_ActionBar = {android.R.attr.titleTextStyle};

        // Need to get resource id of style pointed to from actionBarStyle
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarStyle, outValue, true);
        // Now get action bar style values...
        TypedArray abStyle = context.getTheme().obtainStyledAttributes(outValue.resourceId,
                android_styleable_ActionBar);
        try {
            // titleTextStyle is the first attr in the array above so it's index is 0.
            return abStyle.getResourceId(0, 0);
        } finally {
            abStyle.recycle();
        }
    }

    protected int getMinimumApiLevel() {
        return Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
}
