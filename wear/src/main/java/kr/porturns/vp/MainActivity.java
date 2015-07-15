package kr.porturns.vp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import kr.poturns.util.WearableCommHelper;

public class MainActivity extends Activity {
    //private static final String TAG = "MainActivity";

    private GestureDetector mTouchGestureDetector;
    private SensorFragment sensorFragment;

    private Toast mPrevToast;

    private WearableCommHelper wearableCommHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_main);

        wearableCommHelper = new WearableCommHelper(this);

        sensorFragment = new SensorFragment();
        mTouchGestureDetector = new GestureDetector(this, sensorFragment);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, sensorFragment)
                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        wearableCommHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wearableCommHelper.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorFragment = null;
        wearableCommHelper.destroy();
    }

    public void sendMessage(String path, Object object){
        wearableCommHelper.sendMessage(path, object);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mTouchGestureDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        return mTouchGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    public void showToast(String msg) {
        if (mPrevToast != null)
            mPrevToast.cancel();

        mPrevToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mPrevToast.show();
    }


}
