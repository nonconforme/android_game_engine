package org.es.towerdefense.component;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;

import org.es.engine.gamemechanic.DrawingThread;
import org.es.engine.graphics.utils.DrawTextUtils;
import org.es.engine.hud.HUD;
import org.es.engine.hud.Text;
import org.es.towerdefense.BuildConfig;
import org.es.towerdefense.object.Player;
import org.es.towerdefense.process.GameMgr;

import static org.es.engine.graphics.utils.DrawTextUtils.HorizontalAlign.CENTER;
import static org.es.engine.graphics.utils.DrawTextUtils.HorizontalAlign.LEFT;
import static org.es.engine.graphics.utils.DrawTextUtils.HorizontalAlign.RIGHT;
import static org.es.engine.graphics.utils.DrawTextUtils.VerticalAlign.TOP;

/**
 * @author Cyril Leroux
 *         Created on 30/01/14.
 */
public class TowerDefenseThread extends DrawingThread {

    public enum TouchMode {
        NONE, SCROLL, ZOOM
    }

    private final Player mPlayer;
    private final HUD mMainHud;
    private final HUD mMainHudDebug;
    private final GameMgr mGameMgr;

    private ScaleGestureDetector mScaleDetector;

    private float mLastTouchX = Float.MIN_VALUE;
    private float mLastTouchY = Float.MIN_VALUE;

    private float mScaleFactor = 1.f;

    private TouchMode mTouchMode;
    private String mTouchAction = "Touch the screen";

    public TowerDefenseThread(SurfaceHolder surfaceHolder, Context context) {
        super(surfaceHolder, context);

        mPlayer = new Player(20, 1000);
        mGameMgr = new GameMgr(mPlayer, context);
        mTouchMode = TouchMode.NONE;

        mMainHud = new HUD();
        HudHelper.initMainHud(mMainHud, mPlayer, mGameMgr, getResources());

        mMainHudDebug = new HUD();
        initHudDebug(mMainHudDebug, context);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    protected void updateSurfaceSize(int surfaceWidth, int surfaceHeight) {

        mMainHud.onUpdateSurfaceSize(surfaceWidth, surfaceHeight);
        mGameMgr.updateSurfaceSize(surfaceWidth, surfaceHeight);
    }

    @Override
    protected boolean update() {
        while (hasNext()) {
            processEvent(pollInputEvent());
        }
        mGameMgr.update();
        return false;
    }

    @Override
    protected void processEvent(MotionEvent event) {

        // TODO implementation

        final int action = event.getActionMasked();
        final int id = event.getActionIndex();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mTouchAction = "processEvent => id:" + id + " act:" + action;
        } else {
            mTouchAction = "Action : " + MotionEvent.actionToString(event.getAction());
        }

        if (BuildConfig.DEBUG) {
            Log.d("TowerDefenseThread", mTouchAction);
        }

        switch (action) {

            // First pointer down
            case MotionEvent.ACTION_DOWN:
                // Intercepts touch events
                if (mMainHud.consumeEvent(event)) {
                    return;
                }
                // Switch Mode to SCROLL
                onScrollBegin(event.getX(), event.getY());
                break;

            // Last pointer up
            case MotionEvent.ACTION_UP:
                // Switch Mode to None
                onScrollEnd();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // Switch Mode to Zoom
                mTouchMode = TouchMode.ZOOM;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // TODO if (zoom) => switch to drag
                if (mTouchMode.equals(TouchMode.ZOOM) && event.getPointerCount() == 2) {
                    onScrollBegin(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // TODO Do the job here !
                if (mTouchMode.equals(TouchMode.SCROLL)) {
                    onScroll(event);
                }
                break;

            default:
        }

        // mScaleDetector.onTouchEvent may throw an exception (historyPos out of range)
        try {
            mScaleDetector.onTouchEvent(event);
        } catch (IllegalArgumentException e) {
            if (BuildConfig.DEBUG) {
                Log.e("TowerDefenseThread", "Crash while calling mScaleDetector.onTouchEvent(event)", e);
            }
        }

    }

    @Override
    protected void processEvent(KeyEvent event) {
        // TODO handle keyboard

        final int action = event.getAction();
        if (BuildConfig.DEBUG) {
            Log.d("TowerDefenseThread", "processEvent(KeyEvent) : " + action);
        }
    }

    @Override
    protected void doDraw(Canvas canvas) {

        // Draw game elements: battleground, towers, enemies, ...
        mGameMgr.draw(canvas);

        // Draw the main Head-up display:Scores, GUI, ...
        mMainHud.draw(canvas, null);
        mMainHudDebug.draw(canvas, null);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float lastSpanX;
        private float lastSpanY;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastSpanX = detector.getCurrentSpanX();
            lastSpanY = detector.getCurrentSpanY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 5.0f));
            mGameMgr.updateScaleFactor(mScaleFactor);
            return true;
        }
    }

    private void onScrollBegin(float x, float y) {
        mTouchMode = TouchMode.SCROLL;
        mLastTouchX = x;
        mLastTouchY = y;
    }

    private void onScroll(MotionEvent event) {

        if (mLastTouchX != Float.MIN_VALUE && mLastTouchY != Float.MIN_VALUE) {
            float dx = event.getX() - mLastTouchX;
            float dy = event.getY() - mLastTouchY;
            mGameMgr.updateOffset(dx, dy);
        }
        mLastTouchX = event.getX();
        mLastTouchY = event.getY();

    }

    private void onScrollEnd() {
        mTouchMode = TouchMode.NONE;
        mLastTouchX = Float.MIN_VALUE;
        mLastTouchY = Float.MIN_VALUE;
    }

    public void initHudDebug(HUD hud, Context context) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1f);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(30f);

        hud.addControl(new Text(0.98f, 0.98f, "", LEFT, TOP, paint) {
            @Override public String getText() { return mTouchMode.name(); }
        });

        hud.addControl(new Text(0.5f, 0.98f, "", CENTER, TOP, paint) {
            @Override public String getText() { return mTouchAction; }
        });

        // Draw app version
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            hud.addControl(new Text(0.01f, 0.98f, "Version code: ", RIGHT, TOP, paint) {
                @Override
                public String getText() { return String.valueOf(info.versionCode); }
            });

            hud.addControl(new Text(0.01f, 0.94f, "Version name: ", RIGHT, TOP, paint) {
                @Override
                public String getText() { return String.valueOf(info.versionName); }
            });

        } catch (PackageManager.NameNotFoundException e) { }
    }
}