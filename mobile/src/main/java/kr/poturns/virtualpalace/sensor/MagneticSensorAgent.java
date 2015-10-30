package kr.poturns.virtualpalace.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;

/**
 * <b> 자기장 센서 AGENT </b>
 *
 * @author Yeonho.Kim
 */
public class MagneticSensorAgent extends BaseSensorAgent implements SensorEventListener2 {

    // * * * C O N S T A N T S * * * //
    public static final int DATA_INDEX_AXIS_X = 1;
    public static final int DATA_INDEX_AXIS_Y = 2;
    public static final int DATA_INDEX_AXIS_Z = 3;
    public static final int DATA_INDEX_ACCURACY = 4;

    private final SensorManager mSensorManagerF;
    private final Sensor mSensorF;


    // * * * F I E L D S * * * //
    private float axisX;
    private float axisY;
    private float axisZ;
    private int accuracy;



    // * * * C O N S T R U C T O R S * * * //
    public MagneticSensorAgent(Context context) {
        mSensorManagerF = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorF = mSensorManagerF.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }



    // * * * I N H E R I T S * * * //
    @Override
    public void startListening() {
        super.startListening();
        mSensorManagerF.registerListener(this, mSensorF, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void stopListening() {
        super.stopListening();
        mSensorManagerF.unregisterListener(this);
    }

    /**
     * @return
     */
    @Override
    public double[] getLatestData() {
        return new double[] {
                mLatestMeasuredTimestamp,
                axisX,
                axisY,
                axisZ,
                accuracy
        };
    }

    /**
     * Agent Type 반환
     *
     * @return Agent Type
     */
    @Override
    public int getAgentType() {
        return TYPE_AGENT_MAGNETIC;
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mLatestMeasuredTimestamp = event.timestamp;

        axisX = event.values[DATA_INDEX_AXIS_X -1];
        axisY = event.values[DATA_INDEX_AXIS_Y -1];
        axisZ = event.values[DATA_INDEX_AXIS_Z -1];
        accuracy = event.accuracy;

        onDataMeasured();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        this.accuracy = accuracy;
    }
}
