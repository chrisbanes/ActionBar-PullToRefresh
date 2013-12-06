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

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class CenterProgressDrawable extends Drawable {

    static final int MAX_LEVEL = 10000;

    private final Paint mPaint;

    public CenterProgressDrawable(int color, int strokeWidth) {
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        final float progress = Math.max(Math.min(getLevel() / (float) MAX_LEVEL, 1f), 0f);
        final float barWidth = progress * canvas.getWidth();
        final float l = (canvas.getWidth() - barWidth) / 2f;

        canvas.drawLine(l, canvas.getHeight() / 2, l + barWidth, canvas.getHeight() / 2, mPaint);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
