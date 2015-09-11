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
            return Direction.NONE;

        switch (in.type) {
            case MotionData.TYPE_FLING_TOWARD_RIGHT:
                return Direction.EAST;
            case MotionData.TYPE_FLING_TOWARD_LEFT:
                return Direction.WEST;
            case MotionData.TYPE_FLING_TOWARD_DOWN:
                return Direction.SOUTH;
            case MotionData.TYPE_FLING_TOWARD_UP:
                return Direction.NORTH;
            default:
                return Direction.NONE;
        }
    }

    /**
     * 취소 이벤트를 발생한다.
     *
     * @param motionData 입력
     * @return 취소 이벤트의 발생 여부
     */
    @Override
    public boolean isCanceling(MotionData motionData) {
        return false;
    }

    /**
     * Return 에 해당하는 특수 기능 코드를 반환한다.
     *
     * @param motionData 입력
     * @return {#Operation} 내 KEY.. OPERATIONS.
     */
    @Override
    public int isKeyPressed(MotionData motionData) {
        return 0;
    }

    /**
     * Return 에 해당하는 특수 기능 코드를 반환한다.
     *
     * @param motionData 입력
     * @return 특수 기능 코드
     */
    @Override
    public int isSpecialOperation(MotionData motionData) {
        return 0;
    }
}
