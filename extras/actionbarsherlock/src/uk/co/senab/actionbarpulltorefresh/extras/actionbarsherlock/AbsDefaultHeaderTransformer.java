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

package uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class AbsDefaultHeaderTransformer extends DefaultHeaderTransformer {

    private Animation mHeaderInAnimation, mHeaderOutAnimation;

    @Override
    public void onViewCreated(Activity activity, View headerView) {
        super.onViewCreated(activity, headerView);

        // Create animations for use later
        mHeaderInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        mHeaderOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);

        if (mHeaderOutAnimation != null || mHeaderInAnimation != null) {
            final AnimationCallback callback = new AnimationCallback();
            if (mHeaderOutAnimation != null) {
                mHeaderOutAnimation.setAnimationListener(callback);
            }
        }
    }

    @Override
    protected Drawable getActionBarBackground(Context context) {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= super.getMinimumApiLevel()) {
            return super.getActionBarBackground(context);
        }

        // Get action bar style values...
        TypedArray abStyle = obtainStyledAttrsFromThemeAttr(context, R.attr.actionBarStyle,
                R.styleable.SherlockActionBar);
        try {
            return abStyle.getDrawable(R.styleable.SherlockActionBar_background);
        } finally {
            abStyle.recycle();
        }
    }

    @Override
    protected int getActionBarSize(Context context) {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= super.getMinimumApiLevel()) {
            return super.getActionBarSize(context);
        }

        TypedArray values = context.obtainStyledAttributes(R.styleable.SherlockTheme);
        try {
            return values.getDimensionPixelSize(R.styleable.SherlockTheme_actionBarSize, 0);
        } finally {
            values.recycle();
        }
    }

    @Override
    protected int getActionBarTitleStyle(Context context) {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= super.getMinimumApiLevel()) {
            return super.getActionBarTitleStyle(context);
        }

        // Get action bar style values...
        TypedArray abStyle = obtainStyledAttrsFromThemeAttr(context, R.attr.actionBarStyle,
                R.styleable.SherlockActionBar);
        try {
            return abStyle.getResourceId(R.styleable.SherlockActionBar_titleTextStyle, 0);
        } finally {
            abStyle.recycle();
        }
    }

    @Override
    public boolean showHeaderView() {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= super.getMinimumApiLevel()) {
            return super.showHeaderView();
        }

        final View headerView = getHeaderView();
        final boolean changeVis = headerView != null && headerView.getVisibility() != View.VISIBLE;
        if (changeVis) {
            // Show Header
            if (mHeaderInAnimation != null) {
                // AnimationListener will call HeaderViewListener
                headerView.startAnimation(mHeaderInAnimation);
            }
            headerView.setVisibility(View.VISIBLE);
        }
        return changeVis;
    }

    @Override
    public boolean hideHeaderView() {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= super.getMinimumApiLevel()) {
            return super.hideHeaderView();
        }

        final View headerView = getHeaderView();
        final boolean changeVis = headerView != null && headerView.getVisibility() != View.GONE;
        if (changeVis) {
            // Hide Header
            if (mHeaderOutAnimation != null) {
                // AnimationListener will call HeaderTransformer and
                // HeaderViewListener
                headerView.startAnimation(mHeaderOutAnimation);
            } else {
                // As we're not animating, hide the header + call the header
                // transformer now
                headerView.setVisibility(View.GONE);
                onReset();
            }
        }
        return changeVis;
    }

    @Override
    public void onRefreshMinimized() {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= super.getMinimumApiLevel()) {
            super.onRefreshMinimized();
            return;
        }

        // Here we fade out most of the header, leaving just the progress bar
        View contentLayout = getHeaderView().findViewById(R.id.ptr_content);
        if (contentLayout != null) {
            contentLayout.startAnimation(AnimationUtils
                    .loadAnimation(contentLayout.getContext(), R.anim.fade_out));
            contentLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected int getMinimumApiLevel() {
        return Build.VERSION_CODES.ECLAIR_MR1;
    }

    class AnimationCallback implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == mHeaderOutAnimation) {
                View headerView = getHeaderView();
                if (headerView != null) {
                    headerView.setVisibility(View.GONE);
                }
                onReset();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
