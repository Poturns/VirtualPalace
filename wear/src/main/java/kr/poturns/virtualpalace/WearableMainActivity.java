package kr.poturns.virtualpalace;

import android.app.Activity;
import android.os.Bundle;

import kr.poturns.virtualpalace.inputcollector.GestureInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorInputCollector;
import kr.poturns.virtualpalace.inputcollector.SensorMovementData;
import kr.poturns.virtualpalace.inputmodule.GestureInputFilter;
import kr.poturns.virtualpalace.inputmodule.SensorInputFilter;
import kr.poturns.virtualpalace.inputmodule.WearInputConnector;
import kr.poturns.virtualpalace.inputmodule.WearableInputDetector;


public class WearableMainActivity extends Activity {
    //private WearableInputDetector<MotionData> motionInputDetector;
   // private MotionInputCollector motionInputCollector;

    private WearableInputDetector<SensorMovementData> sensorInputDetector;
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

    /*
    private void initMotionInputDetector() {
        motionInputCollector = new MotionInputCollector(this);
        motionInputDetector = new WearableInputDetector<>(new MotionInputFilter(), motionInputCollector);
        motionInputDetector.setOperationInputConnector(wearInputConnector);
    }
    */

    private void initSensorInputDetector() {
        sensorInputCollector = new SensorInputCollector(this);
        sensorInputDetector = new WearableInputDetector<>(new SensorInputFilter(), sensorInputCollector);
        sensorInputDetector.setOperationInputConnector(wearInputConnector);
    }

    private void initGestureInputDetector() {
        gestureInputCollector = new GestureInputCollector(this);
        gestureInputDetector = new WearableInputDetector<>(new GestureInputFilter(), gestureInputCollector);
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
    /**
     * GestureInputDetector 를 얻는다.

    public WearableInputDetector<MotionData> getMotionInputDetector() {
        return motionInputDetector;
    }

    /**
     * MotionInputCollector 를 얻는다.

    public MotionInputCollector getMotionInputCollector() {
        return motionInputCollector;
    }
     */

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
     *  GestureInputCollector 를 얻는다.
     */
    public GestureInputCollector getGestureInputCollector() {
        return gestureInputCollector;
    }
    /**
     * GestureInputDetector 를 얻는다.
     */
    public WearableInputDetector<String> getGestureInputDetector() {
        return gestureInputDetector;
    }

    /**
     * WearInputConnector 를 얻는다.
     */
    public WearInputConnector getWearInputConnector() {
        return wearInputConnector;
    }


}
