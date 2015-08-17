package kr.poturns.virtualpalace;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import kr.poturns.virtualpalace.inputcollector.GestureData;
import kr.poturns.virtualpalace.inputcollector.GestureInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorMovementData;
import kr.poturns.virtualpalace.inputmodule.WearInputConnector;
import kr.poturns.virtualpalace.inputmodule.WearableInputDetector;


public class WearableMainActivity extends Activity {
    private WearableInputDetector<GestureData> gestureInputDetector;
    private GestureInputCollector gestureInputProcessor;

    private WearableInputDetector<SensorMovementData> sensorInputDetector;
    private SensorInputCollector sensorInputProcessor;

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
        gestureInputProcessor = new GestureInputCollector(this);
        gestureInputDetector = new WearableInputDetector<>(this);
        gestureInputDetector.setInputProcessor(gestureInputProcessor);
        gestureInputDetector.setOperationInputConnector(wearInputConnector);

    }

    private void initSensorInputDetector(){
        sensorInputProcessor = new SensorInputCollector(this);
        sensorInputDetector = new WearableInputDetector<>(this);
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

    public GestureInputCollector getGestureInputProcessor() {
        return gestureInputProcessor;
    }

    public WearableInputDetector<SensorMovementData> getSensorInputDetector() {
        return sensorInputDetector;
    }

    public SensorInputCollector getSensorInputProcessor() {
        return sensorInputProcessor;
    }

}
