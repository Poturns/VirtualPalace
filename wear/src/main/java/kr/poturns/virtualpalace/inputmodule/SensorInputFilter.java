package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.inputcollector.SensorMovementData;

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

    @Override
    public boolean isCanceling(SensorMovementData sensorMovementData) {
        return false;
    }

    @Override
    public int isKeyPressed(SensorMovementData sensorMovementData) {
        return 0;
    }

    @Override
    public int isSpecialOperation(SensorMovementData sensorMovementData) {
        return 0;
    }
    
    private static int checkDataDirection(SensorMovementData in) {
        float max = in.x > in.y ? (in.x > in.z ? in.x : in.z) : (in.y > in.z ? in.y : in.z);

        if (max == in.x) {
            if (in.x > 0) return Direction.EAST;
            else return Direction.WEST;
        } else if (max == in.y) {
            if (in.y > 0) return Direction.NORTH;
            else return Direction.SOUTH;
        } else {
            if (in.z > 0) return Direction.UPWARD;
            else return Direction.DOWNWARD;
        }
    }
}
