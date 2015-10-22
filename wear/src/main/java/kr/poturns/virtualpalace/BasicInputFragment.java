package kr.poturns.virtualpalace;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import kr.poturns.virtualpalace.inputcollector.GestureInputCollector;
import kr.poturns.virtualpalace.inputcollector.InputCollector;
import kr.poturns.virtualpalace.inputmodule.GestureInputFilter;

/**
 * 가속도 센서 / 제스쳐 입력을 받아 Controller로 전송하는 Fragment
 * <p/>
 * Created by Myungjin Kim on 2015-09-02.
 */
public class BasicInputFragment extends BackShakeFragment {


    private static final long DURATION_VIBRATE_INPUT_DETECT = 100;

    private Vibrator mVibrator;

    private GestureInputCollector gestureInputCollector;

    private GestureOverlayView gestureOverlayView;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_normal_input;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        gestureInputCollector = getGestureInputCollector();
        gestureInputCollector.setAdditionalListener(new InputCollector.OnInputResultListener<String>() {
            @Override
            public void onInputResult(String s) {
                Toast.makeText(getActivity(), GestureInputFilter.getOperationName(s), Toast.LENGTH_SHORT).show();
                mVibrator.vibrate(DURATION_VIBRATE_INPUT_DETECT);
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
        gestureInputCollector.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        gestureInputCollector.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        gestureOverlayView.removeAllOnGesturePerformedListeners();
        gestureInputCollector.setAdditionalListener(null);
    }

    @Override
    protected void finish() {
        // TODO getWearInputConnector().transferTextData("exit input menu");
        mVibrator.vibrate(500);
        super.finish();
    }

}
