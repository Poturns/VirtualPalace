package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.inputprocessor.GestureData;

/**
 * Created by Myungjin Kim on 2015-08-03.
 *
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
        return false;
    }

    @Override
    public boolean isCanceling(GestureData gestureData) {
        return false;
    }

    @Override
    public int isKeyPressed(GestureData gestureData) {
        return 0;
    }

    @Override
    public int isSpecialOperation(GestureData gestureData) {
        return 0;
    }

}
