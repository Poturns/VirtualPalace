package kr.poturns.virtualpalace.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;

/**
 * <b> 가속도 센서 AGENT </b>
 *
 * @author Yeonho.Kim
 */
public class AcceleroSensorAgent extends BaseSensorAgent implements SensorEventListener2 {

    // * * * C O N S T A N T S * * * //
    public static final int DATA_INDEX_AXIS_X = 1;
    public static final int DATA_INDEX_AXIS_Y = 2;
    public static final int DATA_INDEX_AXIS_Z = 3;
    public static final int DATA_INDEX_ORIENTATION_X = 4;
    public static final int DATA_INDEX_ORIENTATION_Y = 5;
    public static final int DATA_INDEX_ORIENTATION_Z = 6;
    public static final int DATA_INDEX_ACCURACY = 7;

    private final SensorManager mSensorManagerF;
    private final Sensor mSensorF;

    private final OnDataCollaborationListener mGeoMagneticListener = new OnDataCollaborationListener() {
        @Override
        public void onCollaboration(int thisType, int targetType, double[] thisData, double[] targetData) {
            //
            float[] R = new float[9];
            SensorManager.getRotationMatrix(R, null,
                    new float[]{
                            (float) thisData[DATA_INDEX_AXIS_X],
                            (float) thisData[DATA_INDEX_AXIS_Y],
                            (float) thisData[DATA_INDEX_AXIS_Z]

                    }, new float[]{
                            (float) targetData[DATA_INDEX_AXIS_X],
                            (float) targetData[DATA_INDEX_AXIS_Y],
                            (float) targetData[DATA_INDEX_AXIS_Z]
                    });

            //
            float[] values = new float[3];
            SensorManager.getOrientation(R, values);


            // Azimuth (Axis Z = values[0])
            orientationZ = values[0];
            // Pitch (Axis X = values[1])
            orientationX = values[1];
            // Roll (Axis Y = values[2])
            orientationY = values[2];
        }
    };


    // * * * F I E L D S * * * //
    private float axisX;
    private float axisY;
    private float axisZ;
    private float orientationX;
    private float orientationY;
    private float orientationZ;
    private int accuracy;



    // * * * C O N S T R U C T O R S * * * //
    public AcceleroSensorAgent(Context context) {
        mSensorManagerF = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorF = mSensorManagerF.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        setOrientationEnabled(null, false);
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
        return new double[]{
                mLatestMeasuredTimestamp,
                axisX,
                axisY,
                axisZ,
                orientationX,
                orientationY,
                orientationZ,
                accuracy
        };
    }

    /**
     * Agent Type 반환
     *
     * @return
     */
    @Override
    public int getAgentType() {
        return TYPE_AGENT_ACCELEROMETER;
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

    @Override
    public void setCollaborationWith(BaseSensorAgent agent, OnDataCollaborationListener listener) {
        if (agent instanceof MagneticSensorAgent)
            setOrientationEnabled((MagneticSensorAgent) agent, true);
        else
            super.setCollaborationWith(agent, listener);
    }

    public void setCollaborationWith(BaseSensorAgent agent) {
        this.setCollaborationWith(agent, null);
    }

    // * * * S E T T E R S & G E T T E R S * * * //
    /**
     *
     * @param agent NULL 일 경우, Orientation 비활성화.
     */
    public void setOrientationEnabled(MagneticSensorAgent agent, boolean enable) {
        super.setCollaborationWith(agent, enable? mGeoMagneticListener : null);

        // ORIENTATION 데이터 초기화
        orientationX = orientationY = orientationZ = 0;
    }

}
