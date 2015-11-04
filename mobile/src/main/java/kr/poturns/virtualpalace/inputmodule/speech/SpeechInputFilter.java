package kr.poturns.virtualpalace.inputmodule.speech;

import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p>
 * STT 를 통해 얻어진 결과를 적절한 방향 / 명령 으로 해석하는 클래스
 */
public class SpeechInputFilter implements IOperationInputFilter<String> {

    private static final String WORD_OPERATION_KEY_OK = "ok";
    private static final String WORD_OPERATION_KEY_BACK = "뒤로";
    private static final String WORD_OPERATION_KEY_HOME = "홈";
    private static final String WORD_OPERATION_KEY_VOLUME_UP = "소리 크게";
    private static final String WORD_OPERATION_KEY_VOLUME_DOWN = "소리 작게";

    private static final String WORD_OPERATION_SPECIAL_EXIT = "종료";
    private static final String WORD_OPERATION_SPECIAL_SEARCH = "검색";

    private static final String WORD_OPERATION_SPECIAL_CANCEL = "취소";
    private static final String WORD_OPERATION_SPECIAL_CANCEL_BACK = "뒤로";
    private static final String WORD_OPERATION_SPECIAL_ENG_CANCEL = "cancel";
    private static final String WORD_OPERATION_SPECIAL_ENG_CANCEL_BACK = "back";

    private static final String WORD_OPERATION_SPECIAL_SWITCH_MODE = "모드";
    private static final String WORD_OPERATION_SPECIAL_MENU = "메뉴";

    private static final String WORD_OPERATION_SPECIAL_SELECT = "선택";
    private static final String WORD_OPERATION_SPECIAL_SELECT_CONFIRM = "확인";
    private static final String WORD_OPERATION_SPECIAL_ENG_SELECT = "select";
    private static final String WORD_OPERATION_SPECIAL_ENG_SELECT_CONFIRM = "confirm";

    private static final String WORD_OPERATION_SPECIAL_DEEP = "딥";
    private static final String WORD_OPERATION_SPECIAL_ENG_DEEP = "deep";


    //private static final String WORD_DIRECTION_NONE = "";
    private static final String WORD_DIRECTION_PREV = "이전";
    private static final String WORD_DIRECTION_ENG_PREV = "previous";
    private static final String WORD_DIRECTION_NEXT = "다음";
    private static final String WORD_DIRECTION_ENG_NEXT = "next";

    private static final String WORD_DIRECTION_EAST = "동쪽";
    private static final String WORD_DIRECTION_WEST = "서쪽";
    private static final String WORD_DIRECTION_SOUTH = "남쪽";
    private static final String WORD_DIRECTION_NORTH = "북쪽";

    private static final String WORD_DIRECTION_ENG_EAST = "east";
    private static final String WORD_DIRECTION_ENG_WEST = "west";
    private static final String WORD_DIRECTION_ENG_SOUTH = "south";
    private static final String WORD_DIRECTION_ENG_NORTH = "north";

    private static final String WORD_DIRECTION_3D_CENTER = "가운데로";
    private static final String WORD_DIRECTION_3D_FORWARD = "앞으로";
    private static final String WORD_DIRECTION_3D_BACKWARD = "뒤로";
    private static final String WORD_DIRECTION_3D_UPWARD = "위로";
    private static final String WORD_DIRECTION_3D_DOWNWARD = "아래로";

    @Override
    public int isGoingTo(String string) {
        return checkDirection(string);
    }

    @Override
    public int isTurningTo(String string) {
        return checkDirection(string);
    }

    @Override
    public int isFocusingTo(String string) {
        return checkDirection(string);
    }

    @Override
    public int isZoomingTo(String string) {
        return checkDirection(string);
    }

    @Override
    public boolean isSelecting(String string) {
        return WORD_OPERATION_SPECIAL_SELECT.equals(string);
    }

    @Override
    public boolean isCanceling(String string) {
        return isCancelWord(string);
    }

    private static boolean isCancelWord(String string) {
        return WORD_OPERATION_SPECIAL_CANCEL.equals(string)
                || WORD_OPERATION_SPECIAL_CANCEL_BACK.equals(string)
                || WORD_OPERATION_SPECIAL_ENG_CANCEL.equalsIgnoreCase(string)
                || WORD_OPERATION_SPECIAL_ENG_CANCEL_BACK.equalsIgnoreCase(string);
    }

    @Override
    public int isKeyPressed(String string) {
        return checkKeyOperation(string);
    }

    private static int checkKeyOperation(String s) {
        if (WORD_OPERATION_KEY_OK.equals(s))
            return Operation.KEY_OK;

        else if (WORD_OPERATION_KEY_BACK.equals(s))
            return Operation.KEY_BACK;

        else if (WORD_OPERATION_KEY_HOME.equals(s))
            return Operation.KEY_HOME;

        else if (WORD_OPERATION_KEY_VOLUME_UP.equals(s))
            return Operation.KEY_VOLUME_UP;

        else if (WORD_OPERATION_KEY_VOLUME_DOWN.equals(s))
            return Operation.KEY_VOLUME_DOWN;

        else return Operation.NONE;
    }

    @Override
    public int isSpecialOperation(String string) {
        return checkSpecialOperation(string);
    }

    private static int checkSpecialOperation(String s) {
        if (isCancelWord(s))
            return Operation.CANCEL;

        else if (s.equals(WORD_OPERATION_SPECIAL_EXIT))
            return Operation.TERMINATE;

        else if (s.equals(WORD_OPERATION_SPECIAL_MENU))
            return Operation.KEY_MENU;

        else if (s.equals(WORD_OPERATION_SPECIAL_SEARCH))
            return Operation.SEARCH;

        else if (s.equals(WORD_OPERATION_SPECIAL_SELECT)
                || s.equalsIgnoreCase(WORD_OPERATION_SPECIAL_ENG_SELECT)
                || s.equalsIgnoreCase(WORD_OPERATION_KEY_OK)
                || s.equals(WORD_OPERATION_SPECIAL_SELECT_CONFIRM)
                || s.equalsIgnoreCase(WORD_OPERATION_SPECIAL_ENG_SELECT_CONFIRM))
            return Operation.SELECT;

        else if (s.equals(WORD_OPERATION_SPECIAL_SWITCH_MODE))
            return Operation.SWITCH_MODE;

        else if (s.equals(WORD_OPERATION_SPECIAL_DEEP)
                || s.equalsIgnoreCase(WORD_OPERATION_SPECIAL_ENG_DEEP))
            return Operation.DEEP;

        else return Operation.NONE;
    }

    private int checkDirection(String s) {
        //2d
        if (WORD_DIRECTION_EAST.equals(s)
                || WORD_DIRECTION_ENG_EAST.equalsIgnoreCase(s)
                || WORD_DIRECTION_NEXT.equals(s)
                || WORD_DIRECTION_ENG_NEXT.equalsIgnoreCase(s))
            return Direction.EAST;

        else if (WORD_DIRECTION_WEST.equals(s)
                || WORD_DIRECTION_ENG_WEST.equalsIgnoreCase(s)
                || WORD_DIRECTION_PREV.equals(s)
                || WORD_DIRECTION_ENG_PREV.equalsIgnoreCase(s))
            return Direction.WEST;

        else if (WORD_DIRECTION_SOUTH.equals(s)
                || WORD_DIRECTION_ENG_SOUTH.equalsIgnoreCase(s))
            return Direction.SOUTH;

        else if (WORD_DIRECTION_NORTH.equals(s)
                || WORD_DIRECTION_ENG_NORTH.equalsIgnoreCase(s))
            return Direction.NORTH;

        //3d
        if (WORD_DIRECTION_3D_BACKWARD.equals(s))
            return Direction.BACKWARD;

        else if (WORD_DIRECTION_3D_FORWARD.equals(s))
            return Direction.FORWARD;

        else if (WORD_DIRECTION_3D_UPWARD.equals(s))
            return Direction.UPWARD;

        else if (WORD_DIRECTION_3D_DOWNWARD.equals(s))
            return Direction.DOWNWARD;

        else if (WORD_DIRECTION_3D_CENTER.equals(s))
            return Direction.CENTER;

        else return Direction.NONE;
    }


}
