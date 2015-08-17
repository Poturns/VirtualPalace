package kr.poturns.virtualpalace;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import kr.poturns.virtualpalace.inputmodule.WearInputConnector;
import kr.poturns.virtualpalace.inputmodule.WearableInputDetector;
import kr.poturns.virtualpalace.inputprocessor.GestureData;
import kr.poturns.virtualpalace.inputprocessor.GestureInputProcessor;
import kr.poturns.virtualpalace.inputprocessor.SensorInputProcessor;
import kr.poturns.virtualpalace.inputprocessor.SensorMovementData;


public class WearableMainActivity extends Activity {
    private WearableInputDetector<GestureData> gestureInputDetector;
    private GestureInputProcessor gestureInputProcessor;

    private WearableInputDetector<SensorMovementData> sensorInputDetector;
    private SensorInputProcessor sensorInputProcessor;

    private WearInputConnector wearInputConnector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wearInputConnector = new WearInputConnector(this);

        initGestureInputDetector();
        initSensorInputDetector();

    }

    private void initGestureInputDetector(){
        gestureInputProcessor = new GestureInputProcessor(this);
        gestureInputDetector = new WearableInputDetector<>();
        gestureInputDetector.setInputProcessor(gestureInputProcessor);
        gestureInputDetector.setOperationInputConnector(wearInputConnector);

    }

    private void initSensorInputDetector(){
        sensorInputProcessor = new SensorInputProcessor(this);
        sensorInputDetector = new WearableInputDetector<>();
        sensorInputDetector.setInputProcessor(sensorInputProcessor);
        sensorInputDetector.setOperationInputConnector(wearInputConnector);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        return gestureInputProcessor.onTouchEvent(ev) && super.dispatchTouchEvent(ev);
    }

    public WearableInputDetector<GestureData> getGestureInputDetector() {
        return gestureInputDetector;
    }

    public GestureInputProcessor getGestureInputProcessor() {
        return gestureInputProcessor;
    }

    public WearableInputDetector<SensorMovementData> getSensorInputDetector() {
        return sensorInputDetector;
    }

    public SensorInputProcessor getSensorInputProcessor() {
        return sensorInputProcessor;
    }

}
