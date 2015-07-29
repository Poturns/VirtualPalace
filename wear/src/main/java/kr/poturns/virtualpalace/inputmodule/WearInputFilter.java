package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * Created by Myungjin Kim on 2015-07-30.
 */
public class WearInputFilter implements IOperationInputFilter<Object> {
    @Override
    public int isGoingTo(Object o) {
        return 0;
    }

    @Override
    public int isTurningTo(Object o) {
        return 0;
    }

    @Override
    public int isFocusingTo(Object o) {
        return 0;
    }

    @Override
    public int isZoomingTo(Object o) {
        return 0;
    }

    @Override
    public boolean isSelecting(Object o) {
        return false;
    }
}
