package kr.poturns.virtualpalace;

import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import kr.poturns.virtualpalace.inputcollector.GestureInputCollector;
import kr.poturns.virtualpalace.inputcollector.InputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorMovementData;

/**
 * 가속도 센서 / 제스쳐 입력을 받아 Controller로 전송하는 Fragment
 * <p/>
 * Created by Myungjin Kim on 2015-09-02.
 */
public class BasicInputFragment extends BaseFragment {
    private SensorInputCollector sensorInputCollector;
    private GestureInputCollector gestureInputCollector;

    private GestureOverlayView gestureOverlayView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestureInputCollector = getGestureInputCollector();
        sensorInputCollector = getSensorInputCollector();

        gestureInputCollector.setResultListener(new InputCollector.OnInputResultListener<String>() {
            @Override
            public void onInputResult(String s) {
                Toast.makeText(getActivity(), String.format("gesture : [ %s ]", s), Toast.LENGTH_SHORT).show();
            }
        });
        sensorInputCollector.setResultListener(new InputCollector.OnInputResultListener<SensorMovementData>() {
            @Override
            public void onInputResult(SensorMovementData sensorMovementData) {
                Toast.makeText(getActivity(), String.format("sensor : [ %s ]", sensorMovementData.toString()), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_normal_input;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gestureOverlayView = (GestureOverlayView) view.findViewById(R.id.touch);
        gestureOverlayView.setGestureColor(Color.BLACK);

        gestureOverlayView.addOnGesturePerformedListener(gestureInputCollector);
        gestureOverlayView.setOnClickListener(gestureInputCollector);

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
        gestureOverlayView.setOnClickListener(null);
    }
}
