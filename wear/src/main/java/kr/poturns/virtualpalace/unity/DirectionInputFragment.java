package kr.poturns.virtualpalace.unity;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.inputcollector.TouchInputCollector;
import kr.poturns.virtualpalace.inputmodule.WearableInputDetector;

import static kr.poturns.virtualpalace.input.IOperationInputFilter.Direction.EAST;
import static kr.poturns.virtualpalace.input.IOperationInputFilter.Direction.NONE;
import static kr.poturns.virtualpalace.input.IOperationInputFilter.Direction.NORTH;
import static kr.poturns.virtualpalace.input.IOperationInputFilter.Direction.SOUTH;
import static kr.poturns.virtualpalace.input.IOperationInputFilter.Direction.WEST;

/**
 * 방향 입력을 처리하는 Fragment
 * Created by Myungjin Kim on 2015-10-19.
 */
public class DirectionInputFragment extends BackShakeFragment {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_direction_input;
    }

    // static final String TAG = "DirectionInputFragment";
    private static final int[] VIEW_IDS = {R.id.image_up, R.id.image_down, R.id.image_left, R.id.image_right};

    private static final long DURATION_VIBRATE_TOUCH_DOWN = 50;
    private static final long DURATION_VIBRATE_TOUCH_UP = 100;

    private TouchInputCollector touchInputCollector;
    private WearableInputDetector<MotionEvent> touchDetector;

    private View[] directions;
    private Vibrator mVibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        touchInputCollector = new TouchInputCollector();
        touchInputCollector.setOnTouchEventListener(new TouchInputCollector.OnTouchEventListener() {
            @Override
            public void onTouchDown() {
                mVibrator.vibrate(DURATION_VIBRATE_TOUCH_DOWN);
            }

            @Override
            public void onTouchUp() {
                mVibrator.vibrate(DURATION_VIBRATE_TOUCH_UP);
            }
        });

        touchDetector = new WearableInputDetector<>(new TouchInputFilter(), touchInputCollector);
        touchDetector.setOperationInputConnector(getWearInputConnector());

        //touchDetector.setBatchProcessing(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        directions = new View[VIEW_IDS.length];
        for (int i = 0; i < VIEW_IDS.length; i++) {
            directions[i] = view.findViewById(VIEW_IDS[i]);
            directions[i].setOnTouchListener(touchInputCollector);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        touchInputCollector.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        touchInputCollector.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        for (int i = 0; i < VIEW_IDS.length; i++) {
            directions[i].setOnTouchListener(null);
            directions[i] = null;
        }
        directions = null;

        touchInputCollector.setResultListener(null);
        touchInputCollector.setOnTouchEventListener(null);
        touchDetector.setOperationInputConnector(null);
        touchDetector = null;

        mVibrator = null;
    }

    @Override
    protected void finish() {
        mVibrator.vibrate(500);
        super.finish();
    }


    private static class TouchInputFilter implements IOperationInputFilter<MotionEvent> {

        @Override
        public int isGoingTo(MotionEvent motionEvent) {
            return checkMotionDirection(motionEvent);
        }

        @Override
        public int isTurningTo(MotionEvent motionEvent) {
            return checkMotionDirection(motionEvent);
        }

        @Override
        public int isFocusingTo(MotionEvent motionEvent) {
            return checkMotionDirection(motionEvent);
        }

        @Override
        public int isZoomingTo(MotionEvent motionEvent) {
            return checkMotionDirection(motionEvent);
        }

        @Override
        public boolean isSelecting(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean isCanceling(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public int isKeyPressed(MotionEvent motionEvent) {
            return 0;
        }

        @Override
        public int isSpecialOperation(MotionEvent motionEvent) {
            return 0;
        }

        private static int checkMotionDirection(MotionEvent motionEvent) {
            switch (motionEvent.getSource()) {
                case R.id.image_up:
                    return NORTH;
                case R.id.image_down:
                    return SOUTH;
                case R.id.image_left:
                    return WEST;
                case R.id.image_right:
                    return EAST;
                default:
                    return NONE;
            }
        }
    }
}
