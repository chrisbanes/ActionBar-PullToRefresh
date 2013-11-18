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

package uk.co.senab.actionbarpulltorefresh.library.widget;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import uk.co.senab.actionbarpulltorefresh.library.sdk.Compat;

class AnimationRunnable implements Runnable {

    static interface AnimatorUpdateListener {
        /**
         * <p>Notifies the occurrence of another frame of the animation.</p>
         *
         * @param animation The animation which was repeated.
         */
        void onAnimationUpdate(AnimationRunnable animation);

    }

    public static final int INFINITE = -1;

    private final View mView;

    private boolean mStarted;
    private int mDuration;
    private long mStartTime;
    private Interpolator mInterpolator;

    private int mRunCount;
    private int mRepeatCount;

    private float mAnimationValue;

    private AnimatorUpdateListener mUpdateListener;

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

    public void setUpdateListener(AnimatorUpdateListener listener) {
        mUpdateListener = listener;
    }

    public void start() {
        if (mStarted) return;
        checkState();
        mRunCount = 0;
        mStarted = true;
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        Compat.postOnAnimation(mView, this);
    }

    private void restart() {
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
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
        if (!mStarted || mUpdateListener == null) return;

        final long timeElapsed = AnimationUtils.currentAnimationTimeMillis() - mStartTime;
        mAnimationValue = mInterpolator.getInterpolation(timeElapsed / (float) mDuration);

        mUpdateListener.onAnimationUpdate(this);

        if (timeElapsed < mDuration) {
            Compat.postOnAnimation(mView, this);
        } else {
            if (++mRunCount < mRepeatCount || mRepeatCount == INFINITE) {
                restart();
            }
        }
    }

    private void checkState() {
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
        if (mDuration == 0) {
            mDuration = mView.getResources().getInteger(android.R.integer.config_shortAnimTime);
        }
    }
}