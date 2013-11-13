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
package uk.co.senab.actionbarpulltorefresh.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import uk.co.senab.actionbarpulltorefresh.library.sdk.Compat;

/**
 * Modified version of ButteryProgressBar from:
 * https://android.googlesource.com/platform/packages/apps/UnifiedEmail/+/kitkat-release/src/com/android/mail/ui/ButteryProgressBar.java
 *
 * Implements more of the {@link android.widget.ProgressBar} API to achieve increased compatibility.
 */
public class PullToRefreshProgressBar extends View {

    // The baseline width that the other constants below are optimized for.
    private static final int BASE_WIDTH_DP = 300;

    // A reasonable animation duration for the base width above. It will be weakly scaled up and
    // down for wider and narrower widths, respectively to provide a "constant" detent velocity.
    private static final int BASE_DURATION_MS = 500;

    // A reasonable number of detents for the given width above. It will be weakly scaled up and
    // down for wider and narrower widths, respectively.
    private static final int BASE_SEGMENT_COUNT = 5;

    private static final int DEFAULT_BAR_HEIGHT_DP = 4;
    private static final int DEFAULT_DETENT_WIDTH_DP = 3;
    private static final int DEFAULT_PROGRESS_MAX = 10000;

    private final AnimationRunnable mIndeterminateAnimator;
    private final Paint mPaint = new Paint();

    private final int mSolidBarDetentWidth;
    private final float mDensity;
    private int mSegmentCount;

    private boolean mIndeterminate;
    private int mProgress;
    private int mProgressMax;

    public PullToRefreshProgressBar(Context c) {
        this(c, null);
    }

    public PullToRefreshProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDensity = getResources().getDisplayMetrics().density;

        mProgressMax = DEFAULT_PROGRESS_MAX;

        mSolidBarDetentWidth = Math.round(DEFAULT_DETENT_WIDTH_DP * mDensity);

        mIndeterminateAnimator = new AnimationRunnable(this);
        mIndeterminateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mIndeterminateAnimator.setInterpolator(new ExponentialInterpolator());

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
        mPaint.setColor(color);
        invalidate();
    }

    public synchronized void setMax(int max) {
        setProgressState(mProgress, max, mIndeterminate);
    }

    public synchronized int getMax() {
        return mProgressMax;
    }

    void drawProgress(Canvas canvas) {
        final float progress = Math.max(Math.min(mProgress / (float) mProgressMax, 1f), 0f);
        final int barWidth = Math.round(progress * canvas.getWidth());

        final int l = (canvas.getWidth() - barWidth) / 2;
        canvas.drawRect(l, 0, l + barWidth, canvas.getHeight(), mPaint);
    }

    void drawIndeterminate(Canvas canvas) {
        if (!mIndeterminateAnimator.isStarted()) {
            return;
        }

        final float val = mIndeterminateAnimator.getAnimatedValue() + 1f;

        final int w = getWidth();
        // Because the left-most segment doesn't start all the way on the left, and because it moves
        // towards the right as it animates, we need to offset all drawing towards the left. This
        // ensures that the left-most detent starts at the left origin, and that the left portion
        // is never blank as the animation progresses towards the right.
        final int offset = w >> mSegmentCount - 1;
        // segments are spaced at half-width, quarter, eighth (powers-of-two). to maintain a smooth
        // transition between segments, we used a power-of-two interpolator.
        for (int i = 0; i < mSegmentCount; i++) {
            final float l = val * (w >> (i + 1));
            final float r = (i == 0) ? w + offset : l * 2;
            canvas.drawRect(l + mSolidBarDetentWidth - offset, 0, r - offset, canvas.getHeight(),
                    mPaint);
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

    void onAnimationUpdate() {
        invalidate();
    }

    private static class ExponentialInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            return (float) Math.pow(2.0, input) - 1;
        }
    }

    class AnimationRunnable implements Runnable {

        public static final int INFINITE = -1;

        private final View mView;

        private boolean mStarted;
        private int mDuration;
        private long mStartTime;
        private Interpolator mInterpolator;

        private int mRunCount;
        private int mRepeatCount;

        private float mAnimationValue;

        public AnimationRunnable(View view) {
            mView = view;
        }

        public void setDuration(int duration) {
            mDuration = duration;
        }

        public void setInterpolator(Interpolator interpolator) {
            mInterpolator = interpolator;
        }

        public void setRepeatCount(int repeatCount) {
            mRepeatCount = repeatCount;
        }

        public void start() {
            if (mStarted) return;

            checkState();
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mStarted = true;
            Compat.postOnAnimation(mView, this);
        }

        public void cancel() {
            mStarted = false;
            mView.removeCallbacks(this);
        }

        public boolean isStarted() {
            return mStarted;
        }

        public float getAnimatedValue() {
            return mAnimationValue;
        }

        @Override
        public final void run() {
            if (!mStarted) return;

            final long timeElapsed = AnimationUtils.currentAnimationTimeMillis() - mStartTime;
            mAnimationValue = mInterpolator.getInterpolation(timeElapsed / (float) mDuration);

            onAnimationUpdate();

            if (timeElapsed < mDuration) {
                Compat.postOnAnimation(mView, this);
            } else {
                if (++mRunCount < mRepeatCount || mRepeatCount == INFINITE) {
                    cancel();
                    start();
                }
            }
        }

        private void checkState() {
            if (mInterpolator == null) {
                mInterpolator = new LinearInterpolator();
            }
            if (mDuration == 0) {
                mDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
            }
        }
    }

}
