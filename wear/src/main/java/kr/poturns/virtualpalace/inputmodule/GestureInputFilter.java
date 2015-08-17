package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.inputcollector.GestureData;

/**
 * Created by Myungjin Kim on 2015-08-03.
 * <p>
 * 제스쳐를 처리하는 InputFilter
 */
public class GestureInputFilter implements IOperationInputFilter<GestureData> {
    @Override
    public int isGoingTo(GestureData gestureData) {
        return 0;
    }

    @Override
    public int isTurningTo(GestureData gestureData) {
        return 0;
    }

    @Override
    public int isFocusingTo(GestureData gestureData) {
        return 0;
    }

    @Override
    public int isZoomingTo(GestureData gestureData) {
        return 0;
    }

    @Override
    public boolean isSelecting(GestureData gestureData) {
        return gestureData.isTypeOf(GestureData.TYPE_TOUCH) && gestureData.type == GestureData.TYPE_TOUCH_TAP;
    }

    private static int checkGestureDataDirection2D(GestureData in) {
        if (!in.isTypeOf(GestureData.TYPE_FLING))
            return DIRECTION_NONE;

        switch (in.type) {
            case GestureData.TYPE_FLING_TOWARD_RIGHT:
                return DIRECTION_EAST;
            case GestureData.TYPE_FLING_TOWARD_LEFT:
                return DIRECTION_WEST;
            case GestureData.TYPE_FLING_TOWARD_DOWN:
                return DIRECTION_SOUTH;
            case GestureData.TYPE_FLING_TOWARD_UP:
                return DIRECTION_NORTH;
            default:
                return DIRECTION_NONE;
        }
    }

}
