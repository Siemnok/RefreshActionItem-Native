package com.manuelpeinado.refreshactionitem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Class used internally by {@link RefreshActionItem} to show a determinate progress
 * indicator. Two display modes are supported: "doughnut" and "pie" 
 */
class ProgressIndicator extends View {
    /**
     * Style that shows a hollow indicator, similar to that of the standard
     * indeterminate ProgressBar wheel
     */
    public static final int STYLE_DOUGHNUT = 0;
    /**
     * Style that shows a pie (pac-man) indicator
     */
    public static final int STYLE_PIE = 1;
    private final RectF mRect = new RectF();
    private final RectF mRectInner = new RectF();
    private final Paint mPaintForeground = new Paint();
    private final Paint mPaintBackground = new Paint();
    private final Paint mPaintErase = new Paint();
    private static final Xfermode PORTER_DUFF_CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private int mColorForeground = Color.WHITE;
    private int mColorBackground = Color.BLACK;
    private float mValue;
    private int mStyle = STYLE_DOUGHNUT;
    /**
     * Value which makes our custom drawn indicator have roughly the same size
     * as the built-in ProgressBar indicator. Unit: dp
     */
    private static final float PADDING = 4;
    private float mPadding;
    private Bitmap mBitmap;
    /**
     * Value which makes our custom drawn indicator have roughly the same
     * thickness as the built-in ProgressBar indicator. Expressed as the ration
     * between the inner and outer radiuses
     */
    private static final float INNER_RADIUS_RATIO = 0.84f;

    public ProgressIndicator(Context context) {
        this(context, null);
    }

    public ProgressIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources r = context.getResources();
        float scale = r.getDisplayMetrics().density;
        mPadding = scale * PADDING;
        mPaintForeground.setColor(mColorForeground);
        mPaintForeground.setAntiAlias(true);
        mPaintBackground.setColor(mColorBackground);
        mPaintBackground.setAntiAlias(true);
        mPaintErase.setXfermode(PORTER_DUFF_CLEAR);
        mPaintErase.setAntiAlias(true);
    }

    /**
     * Set the style of this indicator.The two supported styles are "doughnut"
     * and "pie"
     * 
     * @param style One of {@link STYLE_DOUGHNUT} or {@link STYLE_PIE}
     */
    public void setStyle(int style) {
        if (mStyle == style) {
            return;
        }
        mStyle = style;
        updateBitmap();
    }

    /**
     * Return the current style of this indicator.
     * 
     * @return One of {@link STYLE_DOUGHNUT} or {@link STYLE_PIE}
     */
    public int getStyle() {
        return mStyle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, getWidth() / 2 - mBitmap.getWidth() / 2, 
                          getHeight() / 2 - mBitmap.getHeight() / 2, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float bitmapWidth = w - 2 * mPadding;
        float bitmapHeight = h - 2 * mPadding;
        float radius = Math.min(bitmapWidth / 2, bitmapHeight / 2);
        mRect.set(0, 0, bitmapWidth, bitmapHeight);
        radius *= INNER_RADIUS_RATIO;
        mRectInner.set(bitmapWidth / 2f - radius, bitmapHeight / 2f - radius, bitmapWidth / 2f + radius, bitmapHeight / 2f + radius);
        updateBitmap();
    }

    /**
     * Set the foreground color for this indicator. The foreground is the part
     * of the indicator that shows the actual progress
     */
    public void setForegroundColor(int color) {
        this.mColorForeground = color;
        mPaintForeground.setColor(color);
        invalidate();
    }
    
    /**
     * Set the background color for this indicator. The background is a dim and subtle
     * part of the indicator that appears below the actual progress
     */
    public void setBackgroundColor(int color) {
        this.mColorBackground = color;
        mPaintBackground.setColor(color);
        invalidate();
    }

    /**
     * @param value A number between 0 and 1
     */
    public synchronized void setValue(float value) {
        mValue = value;
        updateBitmap();
    }

    private void updateBitmap() {
        if (mRect == null || mRect.width() == 0) {
            return;
        }
        mBitmap = Bitmap.createBitmap((int) mRect.width(), (int) mRect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawArc(mRect, -90, 360, true, mPaintBackground);
        if (mValue < 0.01f) {
            canvas.drawLine(mRect.width() / 2, mRect.height() / 2, mRect.width() / 2, 0, mPaintForeground);
        }
        float angle = mValue * 360;
        canvas.drawArc(mRect, -90, angle, true, mPaintForeground);
        if (mStyle == STYLE_DOUGHNUT) {
            canvas.drawArc(mRectInner, -90, 360, true, mPaintErase);
        }
        postInvalidate();
    }
}