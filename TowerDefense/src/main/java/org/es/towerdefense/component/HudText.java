package org.es.towerdefense.component;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import org.es.engine.graphics.utils.DrawingParam;
import org.es.utils.DrawTextUtils;

/**
 * @author Cyril Leroux
 *         Created 02/03/14.
 */
public class HudText extends Control {

    private final DrawTextUtils.HorizontalAlign mHorizontalAlign;
    private final DrawTextUtils.VerticalAlign mVerticalAlign;
    private final Paint mPaint;

    private final String mStaticText;
    private String mText;

    private float mX;
    private float mY;

    public HudText(String staticText, float x, float y, DrawTextUtils.HorizontalAlign hAlign, DrawTextUtils.VerticalAlign vAlign, Paint paint) {
        super(new RectF(x, y, x, y));

        mX = x;
        mY = y;
        mHorizontalAlign = hAlign;
        mVerticalAlign = vAlign;
        mPaint = paint;

        mStaticText = staticText;
        mText = "";
    }

    /**
     * Draw the HUD element.
     * @param canvas The canvas on which to draw.
     * @param param Not used for the main HUD. Can not be null for other HUD elements.
     */
    @Override
    public void draw(Canvas canvas, DrawingParam param) {
        mBounds.set(DrawTextUtils.drawText(mStaticText + mText, canvas, mX, mY, mHorizontalAlign, mVerticalAlign, mPaint));
    }

    public void setText(String text) { mText = text; }

    @Override
    public void setPosition(float x, float y) {
        mX = x;
        mY = y;
    }

    @Override
    public void offsetPosition(float dx, float dy) {
        mX += dx;
        mY += dy;
    }
}