package kr.poturns.virtualpalace;

import android.app.Activity;
import android.os.Bundle;

import kr.poturns.virtualpalace.inputcollector.GestureData;
import kr.poturns.virtualpalace.inputcollector.GestureInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorMovementData;
import kr.poturns.virtualpalace.inputmodule.GestureInputFilter;
import kr.poturns.virtualpalace.inputmodule.SensorInputFilter;
import kr.poturns.virtualpalace.inputmodule.WearInputConnector;
import kr.poturns.virtualpalace.inputmodule.WearableInputDetector;


public class WearableMainActivity extends Activity {
    private WearableInputDetector<GestureData> gestureInputDetector;
    private GestureInputCollector gestureInputCollector;

    private WearableInputDetector<SensorMovementData> sensorInputDetector;
    private SensorInputCollector sensorInputCollector;

    private WearInputConnector wearInputConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wearInputConnector = new WearInputConnector(this);

        initGestureInputDetector();
        initSensorInputDetector();

        getFragmentManager().beginTransaction()
                .replace(R.id.content, new MainFragment())
                .commit();
    }

    private void initGestureInputDetector() {
        gestureInputCollector = new GestureInputCollector(this);
        gestureInputDetector = new WearableInputDetector<>(new GestureInputFilter(), gestureInputCollector);
        gestureInputDetector.setOperationInputConnector(wearInputConnector);

    }

    private void initSensorInputDetector() {
        sensorInputCollector = new SensorInputCollector(this);
        sensorInputDetector = new WearableInputDetector<>(new SensorInputFilter(), sensorInputCollector);
        sensorInputDetector.setOperationInputConnector(wearInputConnector);
    }


    @Override
    protected void onResume() {
        super.onResume();
        wearInputConnector.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wearInputConnector.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wearInputConnector.destroy();
    }

    /*
    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        return gestureInputCollector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }
*/

    /**
     * GestureInputDetector 를 얻는다.
     */
    public WearableInputDetector<GestureData> getGestureInputDetector() {
        return gestureInputDetector;
    }

    /**
     * GestureInputCollector 를 얻는다.
     */
    public GestureInputCollector getGestureInputCollector() {
        return gestureInputCollector;
    }

    /**
     * SensorInputDetector 를 얻는다.
     */
    public WearableInputDetector<SensorMovementData> getSensorInputDetector() {
        return sensorInputDetector;
    }

    /**
     * SensorInputCollector 를 얻는다.
     */
    public SensorInputCollector getSensorInputCollector() {
        return sensorInputCollector;
    }

    /**
     * WearInputConnector 를 얻는다.
     */
    public WearInputConnector getWearInputConnector() {
        return wearInputConnector;
    }

}
