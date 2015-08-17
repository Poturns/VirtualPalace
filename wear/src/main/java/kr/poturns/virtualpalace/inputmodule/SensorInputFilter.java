package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.inputprocessor.SensorMovementData;

/**
 * Created by Myungjin Kim on 2015-08-03.
 * <p/>
 * 센서 입력을 처리하는 InputFilter
 */
public class SensorInputFilter implements IOperationInputFilter<SensorMovementData> {
    @Override
    public int isGoingTo(SensorMovementData sensorMovementData) {
        return 0;
    }

    @Override
    public int isTurningTo(SensorMovementData sensorMovementData) {
        return 0;
    }

    @Override
    public int isFocusingTo(SensorMovementData sensorMovementData) {
        return 0;
    }

    @Override
    public int isZoomingTo(SensorMovementData sensorMovementData) {
        return 0;
    }

    @Override
    public boolean isSelecting(SensorMovementData sensorMovementData) {
        return false;
    }
}
