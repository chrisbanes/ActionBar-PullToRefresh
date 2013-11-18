/*
 * Copyright (C) 2013 The Android Open Source Project
 * Copyright (C) 2013 Chris Banes
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
package uk.co.senab.actionbarpulltorefresh.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import uk.co.senab.actionbarpulltorefresh.library.R;

/**
 * Modified version of ButteryProgressBar from:
 * https://android.googlesource.com/platform/packages/apps/UnifiedEmail/+/kitkat-release/src/com/android/mail/ui/ButteryProgressBar.java
 *
 * Implements more of the {@link android.widget.ProgressBar} API to achieve increased compatibility.
 */
public class PullToRefreshProgressBar extends View implements
        AnimationRunnable.AnimatorUpdateListener {

    // The baseline width that the other constants below are optimized for.
    private static final int BASE_WIDTH_DP = 300;

    // A reasonable animation duration for the base width above. It will be weakly scaled up and
    // down for wider and narrower widths, respectively to provide a "constant" detent velocity.
    private static final int BASE_DURATION_MS = 450;

    // A reasonable number of detents for the given width above. It will be weakly scaled up and
    // down for wider and narrower widths, respectively.
    private static final int BASE_SEGMENT_COUNT = 3;

    private static final int DEFAULT_BAR_HEIGHT_DP = 4;
    private static final int DEFAULT_INDETERMINATE_BAR_SPACING_DP = 5;
    private static final int DEFAULT_PROGRESS_MAX = 10000;

    private final AnimationRunnable mIndeterminateAnimator;
    private final Paint mPaint = new Paint();

    private final int mIndeterminateBarSpacing;
    private final float mDensity;
    private int mSegmentCount;

    private boolean mIndeterminate;
    private int mProgress;
    private int mProgressMax;

    private int mProgressBarColor;
    private float mProgressBarRadiusPx;

    private final RectF mDrawRect = new RectF();

    public PullToRefreshProgressBar(Context c) {
        this(c, null);
    }

    public PullToRefreshProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDensity = getResources().getDisplayMetrics().density;

        mProgressMax = DEFAULT_PROGRESS_MAX;

        mIndeterminateBarSpacing = Math.round(DEFAULT_INDETERMINATE_BAR_SPACING_DP * mDensity);

        mIndeterminateAnimator = new AnimationRunnable(this);
        mIndeterminateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mIndeterminateAnimator.setUpdateListener(this);

        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.default_progress_bar_color));
    }

    public synchronized boolean isIndeterminate() {
        return mIndeterminate;
    }

    public synchronized void setIndeterminate(final boolean indeterminate) {
        setProgressState(mProgress, mProgressMax, indeterminate);
    }

    public synchronized void setProgress(int progress) {
        setProgressState(progress, mProgressMax, mIndeterminate);
    }

    public synchronized void setProgressBarColor(int color) {
        mProgressBarColor = color;
        invalidate();
    }

    public synchronized void setProgressBarCornerRadius(float radiusPx) {
        mProgressBarRadiusPx = radiusPx;
        invalidate();
    }

    public synchronized void setMax(int max) {
        setProgressState(mProgress, max, mIndeterminate);
    }

    public synchronized int getMax() {
        return mProgressMax;
    }

    void drawProgress(Canvas canvas) {
        mPaint.setColor(mProgressBarColor);

        final float progress = Math.max(Math.min(mProgress / (float) mProgressMax, 1f), 0f);
        final float barWidth = progress * canvas.getWidth();
        final float l = (canvas.getWidth() - barWidth) / 2f;

        mDrawRect.set(l, 0f, l + barWidth, canvas.getHeight());
        canvas.drawRoundRect(mDrawRect, mProgressBarRadiusPx, mProgressBarRadiusPx, mPaint);
    }

    void drawIndeterminate(Canvas canvas) {
        if (!mIndeterminateAnimator.isStarted()) {
            return;
        }

        mPaint.setColor(mProgressBarColor);

        final float animProgress = mIndeterminateAnimator.getAnimatedValue();
        final float barWidth = canvas.getWidth() / (float) mSegmentCount;

        for (int i = -1; i < mSegmentCount; i++) {
            final float l = (i + animProgress) * barWidth;
            final float r = l + barWidth - mIndeterminateBarSpacing;
            mDrawRect.set(l, 0f, r, canvas.getHeight());
            canvas.drawRect(mDrawRect, mPaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            final float widthMultiplier = getWidth() / mDensity / BASE_WIDTH_DP;
            // simple scaling by width is too aggressive, so dampen it first
            final float durationMult = 0.3f * (widthMultiplier - 1) + 1;
            final float segmentMult = 0.1f * (widthMultiplier - 1) + 1;
            mIndeterminateAnimator.setDuration((int) (BASE_DURATION_MS * durationMult));
            mSegmentCount = (int) (BASE_SEGMENT_COUNT * segmentMult);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int specHeight = MeasureSpec.getSize(heightMeasureSpec);

        int height;

        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                height = specHeight;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(specHeight, Math.round(DEFAULT_BAR_HEIGHT_DP * mDensity));
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                height = Math.round(DEFAULT_BAR_HEIGHT_DP * mDensity);
                break;
        }

        setMeasuredDimension(specWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIndeterminate) {
            drawIndeterminate(canvas);
        } else {
            drawProgress(canvas);
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (mIndeterminate) {
            if (visibility == VISIBLE) {
                mIndeterminateAnimator.start();
            } else {
                mIndeterminateAnimator.cancel();
            }
        }
    }

    void setProgressState(int progress, int progressMax, boolean indeterminate) {
        boolean invalidate = false;

        if (mIndeterminate != indeterminate) {
            mIndeterminate = indeterminate;
            if (indeterminate != mIndeterminateAnimator.isStarted()) {
                if (mIndeterminate) {
                    mIndeterminateAnimator.start();
                } else {
                    mIndeterminateAnimator.cancel();
                }
            }
            invalidate = true;
        }

        if (progress != mProgress) {
            mProgress = progress;
            if (!mIndeterminate) {
                invalidate = true;
            }
        }

        if (progressMax != mProgressMax) {
            mProgressMax = progressMax;
            if (!mIndeterminate) {
                invalidate = true;
            }
        }

        if (invalidate) {
            invalidate();
        }
    }

    @Override
    public void onAnimationUpdate(AnimationRunnable animation) {
        invalidate();
    }
}
