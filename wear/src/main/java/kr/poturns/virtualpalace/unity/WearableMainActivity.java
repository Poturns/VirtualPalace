package kr.poturns.virtualpalace.unity;

import android.app.Activity;
import android.os.Bundle;

import kr.poturns.virtualpalace.inputcollector.GestureInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorInputCollector;
import kr.poturns.virtualpalace.inputmodule.GestureInputFilter;
import kr.poturns.virtualpalace.inputmodule.WearInputConnector;
import kr.poturns.virtualpalace.inputmodule.WearableInputDetector;


public class WearableMainActivity extends Activity {

    private SensorInputCollector sensorInputCollector;

    private WearableInputDetector<String> gestureInputDetector;
    private GestureInputCollector gestureInputCollector;

    private WearInputConnector wearInputConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wearInputConnector = new WearInputConnector(this);

        //initMotionInputDetector();
        initSensorInputDetector();
        initGestureInputDetector();

        getFragmentManager().beginTransaction()
                .replace(R.id.content, new MainFragment())
                .commit();
    }

    private void initSensorInputDetector() {
        sensorInputCollector = new SensorInputCollector(this);
    }

    private void initGestureInputDetector() {
        gestureInputCollector = new GestureInputCollector(this);
        gestureInputDetector = new WearableInputDetector<>(new GestureInputFilter(), gestureInputCollector);
        gestureInputDetector.setOperationInputConnector(wearInputConnector);
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

    /**
     * SensorInputCollector 를 얻는다.
     */
    public SensorInputCollector getSensorInputCollector() {
        return sensorInputCollector;
    }

    /**
     * GestureInputCollector 를 얻는다.
     */
    public GestureInputCollector getGestureInputCollector() {
        return gestureInputCollector;
    }

    /**
     * WearInputConnector 를 얻는다.
     */
    public WearInputConnector getWearInputConnector() {
        return wearInputConnector;
    }


}
