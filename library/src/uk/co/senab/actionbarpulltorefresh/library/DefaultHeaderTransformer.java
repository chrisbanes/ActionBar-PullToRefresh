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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.co.senab.actionbarpulltorefresh.library.platform.SDK11;

/**
 * Default Header Transformer.
 */
public class DefaultHeaderTransformer extends PullToRefreshAttacher.HeaderTransformer {

    private View mHeaderView;
    private ViewGroup mContentLayout;
    private TextView mHeaderTextView;
    private ProgressBar mHeaderProgressBar;

    private CharSequence mPullRefreshLabel, mRefreshingLabel, mReleaseLabel;

    private boolean mUseCustomProgressColor = false;
    private int mProgressDrawableColor;
    private long mAnimationDuration;

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
        mHeaderView = headerView;

        // Get ProgressBar and TextView
        mHeaderProgressBar = (ProgressBar) headerView.findViewById(R.id.ptr_progress);
        mHeaderTextView = (TextView) headerView.findViewById(R.id.ptr_text);
        mContentLayout = (ViewGroup) headerView.findViewById(R.id.ptr_content);

        // Default Labels to display
        mPullRefreshLabel = activity.getString(R.string.pull_to_refresh_pull_label);
        mRefreshingLabel = activity.getString(R.string.pull_to_refresh_refreshing_label);
        mReleaseLabel = activity.getString(R.string.pull_to_refresh_release_label);

        mAnimationDuration = activity.getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        // Setup the View styles
        setupViewsFromStyles(activity, headerView);

        // Apply any custom ProgressBar colors
        applyProgressBarColor();

        // FIXME: I do not like this call here
        onReset();
    }

    @Override
    public void onConfigurationChanged(Activity activity, Configuration newConfig) {
        setupViewsFromStyles(activity, getHeaderView());
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                SDK11.setAlpha(mContentLayout, 1f);
            }
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
            ObjectAnimator.ofFloat(mContentLayout, "alpha", 1f, 0f).start();
        }
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public boolean showHeaderView() {
        final boolean changeVis = mHeaderView.getVisibility() != View.VISIBLE;

        if (changeVis) {
            mHeaderView.setVisibility(View.VISIBLE);
            AnimatorSet animSet = new AnimatorSet();
            ObjectAnimator transAnim = ObjectAnimator.ofFloat(mContentLayout, "translationY",
                    -mContentLayout.getHeight(), 0f);
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mHeaderView, "alpha", 0f, 1f);
            animSet.playTogether(transAnim, alphaAnim);
            animSet.setDuration(mAnimationDuration);
            animSet.start();
        }

        return changeVis;
    }

    @Override
    public boolean hideHeaderView() {
        final boolean changeVis = mHeaderView.getVisibility() != View.GONE;

        if (changeVis) {
            Animator animator;
            if (mContentLayout.getAlpha() >= 0.5f) {
                // If the content layout is showing, translate and fade out
                animator = new AnimatorSet();
                ObjectAnimator transAnim = ObjectAnimator.ofFloat(mContentLayout, "translationY",
                        0f, -mContentLayout.getHeight());
                ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mHeaderView, "alpha", 1f, 0f);
                ((AnimatorSet) animator).playTogether(transAnim, alphaAnim);
            } else {
                // If the content layout isn't showing (minimized), just fade out
                animator = ObjectAnimator.ofFloat(mHeaderView, "alpha", 1f, 0f);
            }
            animator.setDuration(mAnimationDuration);
            animator.addListener(new HideAnimationCallback());
            animator.start();
        }

        return changeVis;
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

    private void setupViewsFromStyles(Activity activity, View headerView) {
        final TypedArray styleAttrs = obtainStyledAttrsFromThemeAttr(activity,
                R.attr.ptrHeaderStyle, R.styleable.PullToRefreshHeader);

        // Retrieve the Action Bar size from the app theme or the Action Bar's style
        if (mContentLayout != null) {
            final int height = styleAttrs
                    .getDimensionPixelSize(R.styleable.PullToRefreshHeader_ptrHeaderHeight,
                            getActionBarSize(activity));
            mContentLayout.getLayoutParams().height = height;
            mContentLayout.requestLayout();
        }

        // Retrieve the Action Bar background from the app theme or the Action Bar's style (see #93)
        Drawable bg = styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrHeaderBackground)
                ? styleAttrs.getDrawable(R.styleable.PullToRefreshHeader_ptrHeaderBackground)
                : getActionBarBackground(activity);
        if (bg != null) {
            mHeaderTextView.setBackgroundDrawable(bg);

            // If we have an opaque background we can remove the background from the content layout
            if (mContentLayout != null && bg.getOpacity() == PixelFormat.OPAQUE) {
                mContentLayout.setBackgroundResource(0);
            }
        }

        // Retrieve the Action Bar Title Style from the app theme or the Action Bar's style
        Context abContext = headerView.getContext();
        final int titleTextStyle = styleAttrs
                .getResourceId(R.styleable.PullToRefreshHeader_ptrHeaderTitleTextAppearance,
                        getActionBarTitleStyle(abContext));
        if (titleTextStyle != 0) {
            mHeaderTextView.setTextAppearance(abContext, titleTextStyle);
        }

        // Retrieve the Progress Bar Color the style
        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrProgressBarColor)) {
            mUseCustomProgressColor = true;
            mProgressDrawableColor = styleAttrs
                    .getColor(R.styleable.PullToRefreshHeader_ptrProgressBarColor, 0);
        }

        // Retrieve the text strings from the style (if they're set)
        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrPullText)) {
            mPullRefreshLabel = styleAttrs.getString(R.styleable.PullToRefreshHeader_ptrPullText);
        }
        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrRefreshingText)) {
            mRefreshingLabel = styleAttrs
                    .getString(R.styleable.PullToRefreshHeader_ptrRefreshingText);
        }
        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrReleaseText)) {
            mReleaseLabel = styleAttrs.getString(R.styleable.PullToRefreshHeader_ptrReleaseText);
        }

        styleAttrs.recycle();
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

        // Now get the action bar style values...
        TypedArray abStyle = obtainStyledAttrsFromThemeAttr(context, android.R.attr.actionBarStyle,
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

        // Now get the action bar style values...
        TypedArray abStyle = obtainStyledAttrsFromThemeAttr(context, android.R.attr.actionBarStyle,
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

    class HideAnimationCallback extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            View headerView = getHeaderView();
            if (headerView != null) {
                headerView.setVisibility(View.GONE);
            }
            onReset();
        }
    }

    protected static TypedArray obtainStyledAttrsFromThemeAttr(Context context, int themeAttr,
            int[] styleAttrs) {
        // Need to get resource id of style pointed to from the theme attr
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(themeAttr, outValue, true);
        final int styleResId =  outValue.resourceId;

        // Now return the values (from styleAttrs) from the style
        return context.obtainStyledAttributes(styleResId, styleAttrs);
    }
}
