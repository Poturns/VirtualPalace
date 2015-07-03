package kr.porturns.vp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import kr.poturns.util.MovementData;
import kr.poturns.util.WearableCommHelper;


public class SensorFragment extends Fragment implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, SensorEventListener {
   // static final String TAG = "SensorFragment";
    /**
     * 센서가 움직임을 감지할 최소한의 속도
     */
    private static final int MOVEMENT_SPEED_THRESHOLD = 500;
    /**
     * 센서가 한번 측정 후, 다시 측정하기까지 걸리는 시간
     */
    private static final int SENSOR_ACTIVATE_TIME_THRESHOLD = 200;


    private static final int THRESHOLD_NOT_DETECT_SWIPE = 20;
    private static final int THRESHOLD_DETECT_SWIPE = 100;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private boolean mSensorActive;

    /**
     * 센서가 측정한 시간
     */
    private long mSensorLastTime;
    /**
     * 센서가 측정한 위치 값
     */
    private float mSensorLastX, mSensorLastY, mSensorLastZ;

    TextView mTextView;
    private Toast mFinishingToast;
    private boolean mFinishing = false, isLtoR;

    private MainActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mFinishingToast = Toast.makeText(getActivity(), "swipe to right to finish", Toast.LENGTH_SHORT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        mTextView = (TextView) view.findViewById(android.R.id.text1);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    private void sendMessage(String path, Object object){
        activity.sendMessage(path, object);
    }

    private void setText(String s){
        mTextView.setText(s);
        sendMessage(WearableCommHelper.SEND_STRING_MESSAGE_PATH, s);
    }

    //*************** SensorEvent Listener ***************

    /**
     * 센서 측정을 시작한다.
     */
    private void registerListener() {
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorActive = true;
    }

    /**
     * 센서 측정을 종료한다.
     */
    private void unregisterListener() {
        mSensorManager.unregisterListener(this);
        mSensorActive = false;
    }

    private void showToast(String msg) {
       activity.showToast(msg);
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
                    MovementData data = new MovementData(x, y, z, sensorMovementSpeed);
                    sendMessage(WearableCommHelper.DATA_TRANSFER_MESSAGE_PATH, data);
                    showToast(data.toString());
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

    //*************** SensorEvent Listener ***************


    //*************** Gesture Listener ***************


    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final float diffX = e1.getX() - e2.getX();
        final float diffY = e1.getY() - e2.getY();
        // 가로로 움직인 폭이 일정 이상이면 무시
        if (Math.abs(diffX) < THRESHOLD_DETECT_SWIPE) {
            if (diffY > THRESHOLD_NOT_DETECT_SWIPE) {
                // down to up
                //Toast.makeText(getActivity(), "d to u", Toast.LENGTH_SHORT).show();

                setText("d to u");
                isLtoR = false;
                mFinishing = false;
            } else if (-diffY > THRESHOLD_NOT_DETECT_SWIPE) {
                // up to down
                //Toast.makeText(getActivity(), "u to d", Toast.LENGTH_SHORT).show();

                setText("u to d");

                isLtoR = false;
                mFinishing = false;

            }

        }
        // 세로로 움직인 폭이 일정 이상이면 무시
        else if (Math.abs(diffY) < THRESHOLD_DETECT_SWIPE) {
            if (diffX > THRESHOLD_NOT_DETECT_SWIPE) {
                // right to left
                //Toast.makeText(getActivity(), "r to l", Toast.LENGTH_SHORT).show();
                setText("r to l");
                isLtoR = false;
                mFinishing = false;

            } else if (-diffX > THRESHOLD_NOT_DETECT_SWIPE) {

                // left to right
                //Toast.makeText(getActivity(), "l to r", Toast.LENGTH_SHORT).show();
                if (mFinishing)
                    getActivity().finish();
                else if (isLtoR) {
                    mFinishingToast.show();
                    mFinishing = true;
                }

                setText("l to r");
                isLtoR = true;

            }
        }
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public boolean onDoubleTap(MotionEvent e) {
        showToast("double tap");
        sendMessage(WearableCommHelper.SEND_STRING_MESSAGE_PATH, "double tap");
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        showToast("single tap");
        sendMessage(WearableCommHelper.SEND_STRING_MESSAGE_PATH, "single tap");
        return true;
    }


    //*************** Gesture Listener end ***************
}
