package kr.poturns.virtualpalace;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import kr.poturns.virtualpalace.inputcollector.GestureInputCollector;
import kr.poturns.virtualpalace.inputcollector.InputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorMovementData;
import kr.poturns.virtualpalace.inputmodule.GestureInputFilter;

/**
 * 가속도 센서 / 제스쳐 입력을 받아 Controller로 전송하는 Fragment
 * <p/>
 * Created by Myungjin Kim on 2015-09-02.
 */
public class BasicInputFragment extends BaseFragment {
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

    private static final long DURATION_VIBRATE_INPUT_DETECT = 100;

    private Vibrator mVibrator;

    private SensorInputCollector sensorInputCollector;
    private GestureInputCollector gestureInputCollector;

    private GestureOverlayView gestureOverlayView;

    private BackHandler mHandler;
    private boolean isInShaking = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_normal_input;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mHandler = new BackHandler(this);

        gestureInputCollector = getGestureInputCollector();
        gestureInputCollector.setAdditionalListener(new InputCollector.OnInputResultListener<String>() {
            @Override
            public void onInputResult(String s) {
                Toast.makeText(getActivity(), GestureInputFilter.getOperationName(s), Toast.LENGTH_SHORT).show();
                mVibrator.vibrate(DURATION_VIBRATE_INPUT_DETECT);
            }
        });
        sensorInputCollector = getSensorInputCollector();

        sensorInputCollector.setResultListener(new InputCollector.OnInputResultListener<SensorMovementData>() {
            @Override
            public void onInputResult(SensorMovementData sensorMovementData) {
                // Toast.makeText(getActivity(), String.format("sensor : [ %s ]", sensorMovementData.toString()), Toast.LENGTH_SHORT).show();
                if (!isInShaking) {
                    if (sensorMovementData.speed > SHAKING_MIN_SPEED) {
                        isInShaking = true;
                        Toast.makeText(getActivity(), "2초동안 흔들면 이전 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                        // 2초 뒤에 '뒤로가기' 명령을 실행하도록 하고,
                        // CANCELING_DELAY 동안 흔들림이 감지되지 않으면 '뒤로가기' 명령을 취소함.
                        mHandler.sendEmptyMessageDelayed(MESSAGE_ANDROID_BACK, 2000);
                        mHandler.sendEmptyMessageDelayed(MESSAGE_CANCEL_ANDROID_BACK, CANCELING_DELAY);
                    }
                } else {
                    // CANCELING_DELAY 동안 흔들림이 감지되어서 '뒤로가기 취소' 를 갱신함.
                    // '뒤로가기' 명령이 실행되면 '뒤로가기 취소' 명령은 취소됨.
                    mHandler.removeMessages(MESSAGE_CANCEL_ANDROID_BACK);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_CANCEL_ANDROID_BACK, CANCELING_DELAY);
                }
            }
        });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gestureOverlayView = (GestureOverlayView) view.findViewById(R.id.touch);
        gestureOverlayView.setGestureColor(Color.BLACK);

        gestureOverlayView.addOnGesturePerformedListener(gestureInputCollector);

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorInputCollector.startListening();
        gestureInputCollector.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorInputCollector.stopListening();
        gestureInputCollector.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        gestureOverlayView.removeAllOnGesturePerformedListeners();
    }

    protected void finish() {
        // TODO getWearInputConnector().transferTextData("exit input menu");
        mVibrator.vibrate(500);
        getActivity().onBackPressed();
    }

    private static class BackHandler extends android.os.Handler {
        private WeakReference<BasicInputFragment> fragmentRef;

        public BackHandler(BasicInputFragment f) {
            fragmentRef = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            BasicInputFragment f = fragmentRef.get();
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
