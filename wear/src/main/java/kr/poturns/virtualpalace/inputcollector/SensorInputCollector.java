package kr.poturns.virtualpalace.inputcollector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 센서 입력을 감지하여 {@link SensorMovementData}형태로 반환하는 클래스
 */
public class SensorInputCollector extends AbstractInputCollector<SensorMovementData> implements SensorEventListener {

    /**
     * 센서가 움직임을 감지할 최소한의 속도
     */
    private static final int MOVEMENT_SPEED_THRESHOLD = 500;
    /**
     * 센서가 한번 측정 후, 다시 측정하기까지 걸리는 시간
     */
    private static final int SENSOR_ACTIVATE_TIME_THRESHOLD = 200;
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    /**
     * 센서가 측정한 시간
     */
    private long mSensorLastTime;
    /**
     * 센서가 측정한 위치 값
     */
    private float mSensorLastX, mSensorLastY, mSensorLastZ;

    public SensorInputCollector(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * 센서 측정을 시작한다.
     */
    @Override
    public void startListening() {
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * 센서 측정을 종료한다.
     */
    @Override
    public void stopListening() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = currentTime - mSensorLastTime;

            if (gabOfTime > SENSOR_ACTIVATE_TIME_THRESHOLD) {
                mSensorLastTime = currentTime;
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                //센서가 측정한 속도
                float sensorMovementSpeed = Math.abs(x + y + z - mSensorLastX - mSensorLastY - mSensorLastZ) / gabOfTime * 10000;

                if (sensorMovementSpeed > MOVEMENT_SPEED_THRESHOLD) {
                    if (listener != null)
                        listener.onInputResult(new SensorMovementData(x, y, z, sensorMovementSpeed));
                }

                mSensorLastX = x;
                mSensorLastY = y;
                mSensorLastZ = z;
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
