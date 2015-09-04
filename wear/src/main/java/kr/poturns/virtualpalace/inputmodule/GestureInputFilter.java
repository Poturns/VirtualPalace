package kr.poturns.virtualpalace.inputmodule;

import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * GestureOverlayView에서 얻어진 제스쳐(Prediction)를 적절히 해석하는 InputFilter
 * Created by Myungjin Kim on 2015-09-03.
 */
public class GestureInputFilter implements IOperationInputFilter<String> {
    private static final String SPECIAL_OP_CALL_UI = "callUI";
    private static final String SPECIAL_OP_CANCEL = "cancel";
    private static final String SPECIAL_OP_CHANGE_MODE = "changeMode";
    private static final String SPECIAL_OP_EXIT = "exit";
    private static final String SPECIAL_OP_MENU = "menu";
    private static final String SPECIAL_OP_SEARCH = "search";

    private static final String DIRECTION_UP_TO_DOWN = "upToDown";
    private static final String DIRECTION_DOWN_TO_UP = "downToUp";
    private static final String DIRECTION_LEFT_TO_RIGHT = "leftToRight";
    private static final String DIRECTION_RIGHT_TO_LEFT = "rightToLeft";
    /**
     * 한번 터치한 이벤트 / 명령
     */
    public static final String OPERATION_CLICK = "click";


    @Override
    public int isGoingTo(String s) {
        return 0;
    }

    @Override
    public int isTurningTo(String s) {
        return 0;
    }

    @Override
    public int isFocusingTo(String s) {
        return 0;
    }

    @Override
    public int isZoomingTo(String s) {
        return 0;
    }

    @Override
    public boolean isSelecting(String s) {
        return s.equals(OPERATION_CLICK);
    }

    private int detectDirection(String s) {
        switch (s) {
            case DIRECTION_UP_TO_DOWN:
                return DIRECTION_SOUTH;
            case DIRECTION_DOWN_TO_UP:
                return DIRECTION_NORTH;
            case DIRECTION_RIGHT_TO_LEFT:
                return DIRECTION_WEST;
            case DIRECTION_LEFT_TO_RIGHT:
                return DIRECTION_EAST;
            default:
                return DIRECTION_NONE;
        }
    }

    /**
     * 특별한 명령을 감지한다.
     */
    private int detectSpecialOperation(String s) {
        switch (s) {
            case SPECIAL_OP_CALL_UI:
                return 0;
            case SPECIAL_OP_CANCEL:
                return 0;
            case SPECIAL_OP_CHANGE_MODE:
                return 0;
            case SPECIAL_OP_EXIT:
                return 0;
            case SPECIAL_OP_MENU:
                return 0;
            case SPECIAL_OP_SEARCH:
                return 0;
            default:
                return OPERATION_NONE;
        }
    }
}
