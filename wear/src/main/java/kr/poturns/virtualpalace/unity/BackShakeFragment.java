package kr.poturns.virtualpalace.unity;

import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import kr.poturns.virtualpalace.inputcollector.InputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorMovementData;

/**
 * 센서 입력을 통해 뒤로가기를 처리하는 Fragment.
 *
 * Created by Myungjin Kim on 2015-10-19.
 */
public abstract class BackShakeFragment extends BaseFragment implements InputCollector.OnInputResultListener<SensorMovementData> {
    /**
     * 뒤로가기 Message
     */
    private static final int MESSAGE_ANDROID_BACK = 97;
    /**
     * 뒤로가기 취소 Message
     */
    private static final int MESSAGE_CANCEL_ANDROID_BACK = 98;
    /**
     * 최소 흔들기 속도
     */
    private static final int SHAKING_MIN_SPEED = 500;
    /**
     * 뒤로가기 Message를 취소하기 위해 대기할 시간. ms
     */
    private static final int CANCELING_DELAY = 500;

    protected SensorInputCollector sensorInputCollector;

    private BackHandler mHandler;
    private boolean isInShaking = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new BackHandler(this);

        sensorInputCollector = getSensorInputCollector();

        sensorInputCollector.setResultListener(this);

    }

    @Override
    public void onInputResult(SensorMovementData sensorMovementData) {
        // Toast.makeText(getActivity(), String.format("sensor : [ %s ]", sensorMovementData.toString()), Toast.LENGTH_SHORT).show();
        if (!isInShaking) {
            if (sensorMovementData.speed > SHAKING_MIN_SPEED) {
                isInShaking = true;
                Toast.makeText(getActivity(), "1초동안 흔들면 이전 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                // 1초 뒤에 '뒤로가기' 명령을 실행하도록 하고,
                // CANCELING_DELAY 동안 흔들림이 감지되지 않으면 '뒤로가기' 명령을 취소함.
                mHandler.sendEmptyMessageDelayed(MESSAGE_ANDROID_BACK, 1000);
                mHandler.sendEmptyMessageDelayed(MESSAGE_CANCEL_ANDROID_BACK, CANCELING_DELAY);
            }
        } else {
            // CANCELING_DELAY 동안 흔들림이 감지되어서 '뒤로가기 취소' 를 갱신함.
            // '뒤로가기' 명령이 실행되면 '뒤로가기 취소' 명령은 취소됨.
            mHandler.removeMessages(MESSAGE_CANCEL_ANDROID_BACK);
            mHandler.sendEmptyMessageDelayed(MESSAGE_CANCEL_ANDROID_BACK, CANCELING_DELAY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorInputCollector.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorInputCollector.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sensorInputCollector.setResultListener(null);
    }


    protected void finish() {
        getActivity().onBackPressed();
    }

    private static class BackHandler extends android.os.Handler {
        private WeakReference<BackShakeFragment> fragmentRef;

        public BackHandler(BackShakeFragment f) {
            fragmentRef = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            BackShakeFragment f = fragmentRef.get();
            if (f == null)
                return;

            switch (msg.what) {
                case MESSAGE_ANDROID_BACK:
                    removeMessages(MESSAGE_CANCEL_ANDROID_BACK);
                    f.finish();

                    break;

                case MESSAGE_CANCEL_ANDROID_BACK:
                    removeMessages(MESSAGE_ANDROID_BACK);
                    f.isInShaking = false;
                    break;

                default:
                    break;
            }
        }
    }
}
