package kr.poturns.virtualpalace.inputcollector;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Myungjin Kim on 2015-10-19.
 * <p/>
 * 방향 터치 입력을 처리하여 Detector로 전달하는 클래스
 */
public class TouchInputCollector extends AbstractInputCollector<MotionEvent> implements View.OnTouchListener {
    private static final int MSG_TAP_DOWN = 1;
    private static final int DELAY_DIRECTION_MSG = 100; //100ms

    private final Handler mHandler;
    private boolean isListening = false;
    private OnTouchEventListener onTouchEventListener;

    public TouchInputCollector() {
        mHandler = new TouchHandler();
    }

    public void setOnTouchEventListener(OnTouchEventListener onTouchEventListener) {
        this.onTouchEventListener = onTouchEventListener;
    }

    @Override
    public void startListening() {
        isListening = true;
    }

    @Override
    public void stopListening() {
        isListening = false;
        clearTapEvent();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isListening)
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                event.setSource(v.getId());
                sendTapDownEvent(event);
                if (onTouchEventListener != null)
                    onTouchEventListener.onTouchDown();
                break;

            case MotionEvent.ACTION_UP:
                clearTapEvent();
                if (onTouchEventListener != null)
                    onTouchEventListener.onTouchUp();
                break;
            default:
                break;
        }

        return false;
    }


    private void clearTapEvent() {
        mHandler.removeMessages(MSG_TAP_DOWN);
    }

    private void sendTapDownEvent(MotionEvent event) {
        clearTapEvent();
        Message.obtain(mHandler, MSG_TAP_DOWN, 0, 0, event).sendToTarget();
    }

    private class TouchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TAP_DOWN:
                    MotionEvent event = (MotionEvent) msg.obj;
                    if (listener != null) {
                        listener.onInputResult(event);
                    }

                    Message newMessage = Message.obtain(this, MSG_TAP_DOWN, 0, 0, event);
                    sendMessageDelayed(newMessage, DELAY_DIRECTION_MSG);

                    break;

                default:
                    break;
            }
        }
    }

    /**
     * TouchInputCollector에서 터치를 처리한 후 추가 이벤트를 처리하기 위한 리스너
     */
    public interface OnTouchEventListener {
        /**
         * 버튼에 손가락을 터치하였을 때 호출된다.
         */
        void onTouchDown();

        /**
         * 버튼에서 손가락을 떼었을 때 호출된다.
         */
        void onTouchUp();
    }

}