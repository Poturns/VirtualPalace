package kr.poturns.virtualpalace.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;

/**
 * <b> 자이로 센서 AGENT </b>
 *
 * @author Yeonho.Kim
 */
public class GyroSensorAgent extends BaseSensorAgent implements SensorEventListener2 {

    // * * * C O N S T A N T S * * * //
    public static final int DATA_INDEX_AXIS_X = 1;
    public static final int DATA_INDEX_AXIS_Y = 2;
    public static final int DATA_INDEX_AXIS_Z = 3;

    private final SensorManager mSensorManagerF;
    private final Sensor mSensorF;


    // * * * F I E L D S * * * //
    private float axisX;
    private float axisY;
    private float axisZ;


    // * * * C O N S T R U C T O R S * * * //
    public GyroSensorAgent(Context context) {
        mSensorManagerF = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorF = mSensorManagerF.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

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

    @Override
    public int getAgentType() {
        return TYPE_AGENT_GYROSCOPE;
    }

    /**
     * @return
     */
    @Override
    public double[] getLatestData() {
        return new double[]{
                mLatestMeasuredTimestamp,
                axisX,
                axisY,
                axisZ,
        };
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        mLatestMeasuredTimestamp = event.timestamp;

        // Angular speed around the x-axis
        axisX = event.values[DATA_INDEX_AXIS_X -1];
        // Angular speed around the y-axis
        axisY = event.values[DATA_INDEX_AXIS_Y -1];
        // Angular speed around the z-axis
        axisZ = event.values[DATA_INDEX_AXIS_Z -1];

    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
