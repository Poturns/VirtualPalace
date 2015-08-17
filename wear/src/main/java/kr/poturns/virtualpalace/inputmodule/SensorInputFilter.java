package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.inputcollector.SensorMovementData;

/**
 * Created by Myungjin Kim on 2015-08-03.
 * <p>
 * 센서 입력을 처리하는 InputFilter
 */
public class SensorInputFilter implements IOperationInputFilter<SensorMovementData> {
    @Override
    public int isGoingTo(SensorMovementData sensorMovementData) {
        return checkDataDirection(sensorMovementData);
    }

    @Override
    public int isTurningTo(SensorMovementData sensorMovementData) {
        return checkDataDirection(sensorMovementData);
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

    private static int checkDataDirection(SensorMovementData in) {
        float max = in.x > in.y ? (in.x > in.z ? in.x : in.z) : (in.y > in.z ? in.y : in.z);

        if (max == in.x) {
            if (in.x > 0) return DIRECTION_EAST;
            else return DIRECTION_WEST;
        } else if (max == in.y) {
            if (in.y > 0) return DIRECTION_NORTH;
            else return DIRECTION_SOUTH;
        } else {
            if (in.z > 0) return DIRECTION_3D_UPWARD;
            else return DIRECTION_3D_DOWNWARD;
        }
    }

}
