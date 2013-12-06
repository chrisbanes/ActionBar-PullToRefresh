package uk.co.senab.actionbarpulltorefresh.library.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class CenterProgressDrawable extends Drawable {

    static final int MAX_LEVEL = 10000;

    private final Paint mPaint;
    private final RectF mDrawRect;

    public CenterProgressDrawable(int color) {
        mPaint = new Paint();
        mPaint.setColor(color);

        mDrawRect = new RectF();
    }

    @Override
    protected boolean onLevelChange(int level) {
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        final float progress = Math.max(Math.min(getLevel() / (float) MAX_LEVEL, 1f), 0f);
        final float barWidth = progress * canvas.getWidth();
        final float l = (canvas.getWidth() - barWidth) / 2f;

        mDrawRect.set(l, 0f, l + barWidth, canvas.getHeight());
        canvas.drawRect(mDrawRect, mPaint);
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
