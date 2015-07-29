package kr.poturns.virtualpalace.inputprocessor;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p>
 * 모션 이벤트 (터치)를 입력받아 {@link GestureData}로 변환하는 클래스
 */
public class GestureInputProcessor extends InputProcessor.Base<GestureData> implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final int THRESHOLD_NOT_DETECT_SWIPE = 20;
    private static final int THRESHOLD_DETECT_SWIPE = 100;

    private GestureDetector mGestureDetector;

    public GestureInputProcessor(Context context) {
        mGestureDetector = new GestureDetector(context, this);
    }

    /**
     * 모션 이벤트를 입력받는다.
     *
     * @param ev 발생한 모션이벤트
     * @return 처리 결과
     */
    public boolean onTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev);
    }

    //*************** Gesture Listener ***************

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final float diffX = e1.getX() - e2.getX();
        final float diffY = e1.getY() - e2.getY();
        // 가로로 움직인 폭이 일정 이상이면 무시

        final float absDiffX = Math.abs(diffX);
        final float absDiffY = Math.abs(diffY);
        if (absDiffX < THRESHOLD_DETECT_SWIPE) {
            if (diffY > THRESHOLD_NOT_DETECT_SWIPE) {
                // down to up
                if (listener != null)
                    listener.onInputResult(new GestureData(GestureData.TYPE_TOWARD_UP, (int) absDiffY));

            } else if (-diffY > THRESHOLD_NOT_DETECT_SWIPE) {
                // up to down
                if (listener != null)
                    listener.onInputResult(new GestureData(GestureData.TYPE_TOWARD_DOWN, (int) absDiffY));

            }

        }
        // 세로로 움직인 폭이 일정 이상이면 무시
        else if (absDiffY < THRESHOLD_DETECT_SWIPE) {
            if (diffX > THRESHOLD_NOT_DETECT_SWIPE) {
                // right to left
                if (listener != null)
                    listener.onInputResult(new GestureData(GestureData.TYPE_TOWARD_LEFT, (int) absDiffX));

            } else if (-diffX > THRESHOLD_NOT_DETECT_SWIPE) {

                // left to right
                if (listener != null)
                    listener.onInputResult(new GestureData(GestureData.TYPE_TOWARD_RIGHT, (int) absDiffX));

            }
        }
        return false;
    }

    public void onShowPress(MotionEvent e) {
        if (listener != null)
            listener.onInputResult(new GestureData(GestureData.TYPE_TOUCH_PRESS, GestureData.AMOUNT_NOTHING));
    }

    public boolean onDown(MotionEvent e) {
        if (listener != null)
            listener.onInputResult(new GestureData(GestureData.TYPE_TOUCH_DOWN, GestureData.AMOUNT_NOTHING));
        return false;
    }

    public boolean onDoubleTap(MotionEvent e) {
        if (listener != null)
            listener.onInputResult(new GestureData(GestureData.TYPE_TOUCH_TAP, 2));
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        if (listener != null)
            listener.onInputResult(new GestureData(GestureData.TYPE_TOUCH_TAP, 2));
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (listener != null)
            listener.onInputResult(new GestureData(GestureData.TYPE_TOUCH_TAP, 0));
        return true;
    }
}
