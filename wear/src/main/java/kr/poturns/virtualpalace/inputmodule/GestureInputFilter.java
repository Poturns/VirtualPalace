package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * GestureOverlayView에서 얻어진 제스쳐(Prediction)를 적절히 해석하는 InputFilter
 * Created by Myungjin Kim on 2015-09-03.
 */
public class GestureInputFilter implements IOperationInputFilter<String> {
    private static final String SPECIAL_OP_CANCEL1 = "Cancel1";
    private static final String SPECIAL_OP_CANCEL2 = "Cancel2";
    private static final String SPECIAL_OP_MENU = "Menu";
    private static final String SPECIAL_OP_DEEP = "Deep";
    private static final String SPECIAL_OP_SELECT = "Select";
    private static final String SPECIAL_OP_SWITCH_MODE = "SwitchMode";

    private static final String DIRECTION_UP_TO_DOWN = "DupToDown";
    private static final String DIRECTION_DOWN_TO_UP = "DdownToUp";
    private static final String DIRECTION_LEFT_TO_RIGHT = "DleftToRight";
    private static final String DIRECTION_RIGHT_TO_LEFT = "DrightToLeft";
    /**
     * 한번 터치한 이벤트 / 명령
     */
    public static final String OPERATION_CLICK = "click";

    /**
     * 주어진 명령의 이름을 반환한다.
     */
    public static String getOperationName(String s) {
        switch (s) {
            case DIRECTION_UP_TO_DOWN:
                return "Down";
            case DIRECTION_DOWN_TO_UP:
                return "Up";
            case DIRECTION_RIGHT_TO_LEFT:
                return "Left";
            case DIRECTION_LEFT_TO_RIGHT:
                return "Right";
            case SPECIAL_OP_CANCEL1:
            case SPECIAL_OP_CANCEL2:
                return "Cancel";
            case SPECIAL_OP_MENU:
            case SPECIAL_OP_DEEP:
            case SPECIAL_OP_SELECT:
            case SPECIAL_OP_SWITCH_MODE:
                return s;
            default:
                return "";
        }
    }

    @Override
    public int isGoingTo(String s) {
        return detectDirection(s);
    }

    @Override
    public int isTurningTo(String s) {
        return detectDirection(s);
    }

    @Override
    public int isFocusingTo(String s) {
        return detectDirection(s);
    }

    @Override
    public int isZoomingTo(String s) {
        return detectDirection(s);
    }

    @Override
    public boolean isSelecting(String s) {
        return s.equals(SPECIAL_OP_SELECT) || s.equals(OPERATION_CLICK);
    }

    /**
     * 특별한 명령을 감지한다.
     */
    private int detectSpecialOperation(String s) {
        switch (s) {
            case SPECIAL_OP_CANCEL1:
            case SPECIAL_OP_CANCEL2:
                return Operation.CANCEL;
            case SPECIAL_OP_MENU:
                return Operation.KEY_MENU;
            case SPECIAL_OP_DEEP:
                return Operation.DEEP;
            case SPECIAL_OP_SELECT:
                return Operation.SELECT;
            case SPECIAL_OP_SWITCH_MODE:
                return Operation.SWITCH_MODE;
            default:
                return Operation.NONE;
        }
    }

    private int detectDirection(String s) {
        switch (s) {
            case DIRECTION_UP_TO_DOWN:
                return Direction.SOUTH;
            case DIRECTION_DOWN_TO_UP:
                return Direction.NORTH;
            case DIRECTION_RIGHT_TO_LEFT:
                return Direction.WEST;
            case DIRECTION_LEFT_TO_RIGHT:
                return Direction.EAST;
            default:
                return Direction.NONE;
        }
    }

    @Override
    public boolean isCanceling(String gestureData) {
        return detectSpecialOperation(gestureData) == Operation.CANCEL;
    }

    @Override
    public int isKeyPressed(String gestureData) {
        return Operation.NONE;
    }

    @Override
    public int isSpecialOperation(String gestureData) {
        return detectSpecialOperation(gestureData);
    }

}
