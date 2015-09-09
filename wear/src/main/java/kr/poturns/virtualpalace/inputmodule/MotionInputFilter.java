package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.inputcollector.MotionData;

/**
 * Created by Myungjin Kim on 2015-08-03.
 * <p>
 * MotionEvent를 처리하여 생성된 MotionData를 해석하는 InputFilter
 */
public class MotionInputFilter implements IOperationInputFilter<MotionData> {
    @Override
    public int isGoingTo(MotionData motionData) {
        return 0;
    }

    @Override
    public int isTurningTo(MotionData motionData) {
        return 0;
    }

    @Override
    public int isFocusingTo(MotionData motionData) {
        return 0;
    }

    @Override
    public int isZoomingTo(MotionData motionData) {
        return 0;
    }

    @Override
    public boolean isSelecting(MotionData motionData) {
        return motionData.isTypeOf(MotionData.TYPE_TOUCH) && motionData.type == MotionData.TYPE_TOUCH_TAP;
    }

    private static int checkGestureDataDirection2D(MotionData in) {
        if (!in.isTypeOf(MotionData.TYPE_FLING))
            return DIRECTION_NONE;

        switch (in.type) {
            case MotionData.TYPE_FLING_TOWARD_RIGHT:
                return DIRECTION_EAST;
            case MotionData.TYPE_FLING_TOWARD_LEFT:
                return DIRECTION_WEST;
            case MotionData.TYPE_FLING_TOWARD_DOWN:
                return DIRECTION_SOUTH;
            case MotionData.TYPE_FLING_TOWARD_UP:
                return DIRECTION_NORTH;
            default:
                return DIRECTION_NONE;
        }
    }

}
