package kr.poturns.virtualpalace.inputmodule.speech;

import java.util.List;

import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * Created by Myungjin Kim on 2015-11-21.
 *
 * 문자열의 동일 여부로 명령을 판별하는 IOperationInputFilter
 */
public class EqualitySpeechFilter implements IOperationInputFilter<List<String>> {

    private static final String WORD_OPERATION_KEY_OK = "ok";
    private static final String WORD_OPERATION_KEY_BACK = "뒤로";
    private static final String WORD_OPERATION_KEY_HOME = "홈";
    private static final String WORD_OPERATION_KEY_VOLUME_UP = "소리 크게";
    private static final String WORD_OPERATION_KEY_VOLUME_DOWN = "소리 작게";

    private static final String WORD_OPERATION_SPECIAL_EXIT = "종료";
    private static final String WORD_OPERATION_SPECIAL_SEARCH = "검색";

    private static final String[] WORDS_OPERATION_SPECIAL_CANCEL = {
            "취소",
            "뒤로",
            "cancel", "캔슬",
            "back", "백"
    };

    private static final String WORD_OPERATION_SPECIAL_SWITCH_MODE = "모드";
    private static final String WORD_OPERATION_SPECIAL_MENU = "메뉴";

    private static final String[] WORDS_OPERATION_SPECIAL_SELECT = {
            WORD_OPERATION_KEY_OK,
            "선택",
            "확인",
            "select", "셀렉트",
            "confirm", "컨펌"
    };

    private static final String WORD_OPERATION_SPECIAL_DEEP = "딥";
    //private static final String WORD_OPERATION_SPECIAL_ENG_DEEP = "deep";


    private static final String[] WORDS_DIRECTION_PREV = {
            "이전",
            "previous"
    };
    private static final String[] WORDS_DIRECTION_NEXT = {
            "다음",
            "next"
    };

    private static final String[] WORDS_DIRECTION_EAST = {
            "동쪽",
            "east", "이스트"
    };
    private static final String[] WORDS_DIRECTION_WEST = {
            "서쪽",
            "west", "웨스트"
    };
    private static final String[] WORDS_DIRECTION_SOUTH = {
            "남쪽",
            "south", "사우스"
    };

    private static final String[] WORDS_DIRECTION_NORTH = {
            "북쪽",
            "north", "노스"
    };

    private static final String WORD_DIRECTION_3D_CENTER = "가운데로";
    private static final String WORD_DIRECTION_3D_FORWARD = "앞으로";
    private static final String WORD_DIRECTION_3D_BACKWARD = "뒤로";
    private static final String WORD_DIRECTION_3D_UPWARD = "위로";
    private static final String WORD_DIRECTION_3D_DOWNWARD = "아래로";

    @Override
    public int isGoingTo(List<String> string) {
        return checkDirection(string);
    }

    @Override
    public int isTurningTo(List<String> string) {
        return checkDirection(string);
    }

    @Override
    public int isFocusingTo(List<String> string) {
        return checkDirection(string);
    }

    @Override
    public int isZoomingTo(List<String> string) {
        return checkDirection(string);
    }

    @Override
    public boolean isSelecting(List<String> strings) {
        return isTargetWord(strings, WORDS_OPERATION_SPECIAL_SELECT);
    }


    @Override
    public boolean isCanceling(List<String> strings) {
        return isTargetWord(strings, WORDS_OPERATION_SPECIAL_CANCEL);
    }


    private static boolean isTargetWord(String input, String... targetWordPool) {
        for (String word : targetWordPool) {
            if (word.equals(input)) return true;
        }
        return false;
    }

    private static boolean isTargetWord(List<String> inputs, String... targetWordPool) {
        for (String s : inputs) {
            for (String word : targetWordPool) {
                if (word.equals(s)) return true;
            }
        }
        return false;
    }

    @Override
    public int isKeyPressed(List<String> string) {
        return checkKeyOperation(string);
    }

    private static int checkKeyOperation(List<String> strings) {
        for (String s : strings) {
            int op = checkKeyOperation(s);

            if (op != Operation.NONE)
                return op;
        }

        return Operation.NONE;
    }

    private static int checkKeyOperation(String s) {
        if (isTargetWord(s, WORD_OPERATION_KEY_OK))
            return Operation.KEY_OK;

        else if (isTargetWord(s, WORD_OPERATION_KEY_BACK))
            return Operation.KEY_BACK;

        else if (isTargetWord(s, WORD_OPERATION_KEY_HOME))
            return Operation.KEY_HOME;

        else if (isTargetWord(s, WORD_OPERATION_KEY_VOLUME_UP))
            return Operation.KEY_VOLUME_UP;

        else if (isTargetWord(s, WORD_OPERATION_KEY_VOLUME_DOWN))
            return Operation.KEY_VOLUME_DOWN;

        else return Operation.NONE;
    }

    @Override
    public int isSpecialOperation(List<String> string) {
        return checkSpecialOperation(string);
    }

    private static int checkSpecialOperation(List<String> strings) {
        for (String s : strings) {
            int op = checkSpecialOperation(s);
            if (op != Operation.NONE)
                return op;
        }

        return Operation.NONE;
    }

    private static int checkSpecialOperation(String s) {
        if (isTargetWord(s, WORDS_OPERATION_SPECIAL_CANCEL))
            return Operation.CANCEL;

        else if (isTargetWord(s, WORD_OPERATION_SPECIAL_EXIT))
            return Operation.TERMINATE;

        else if (isTargetWord(s, WORD_OPERATION_SPECIAL_MENU))
            return Operation.KEY_MENU;

        else if (isTargetWord(s, WORD_OPERATION_SPECIAL_SEARCH))
            return Operation.SEARCH;

        else if (isTargetWord(s, WORDS_OPERATION_SPECIAL_SELECT))
            return Operation.SELECT;

        else if (isTargetWord(s, WORD_OPERATION_SPECIAL_SWITCH_MODE))
            return Operation.SWITCH_MODE;

        else if (isTargetWord(s, WORD_OPERATION_SPECIAL_DEEP))
            return Operation.DEEP;

        else return Operation.NONE;
    }

    private int checkDirection(List<String> strings) {
        for (String s : strings) {
            int op = checkDirection(s);
            if (op != Direction.NONE)
                return op;
        }

        return Direction.NONE;
    }

    private int checkDirection(String s) {
        //2d
        if (isTargetWord(s, WORDS_DIRECTION_EAST) || isTargetWord(s, WORDS_DIRECTION_NEXT))
            return Direction.EAST;

        else if (isTargetWord(s, WORDS_DIRECTION_WEST)|| isTargetWord(s, WORDS_DIRECTION_PREV))
            return Direction.WEST;

        else if (isTargetWord(s, WORDS_DIRECTION_SOUTH))
            return Direction.SOUTH;

        else if (isTargetWord(s, WORDS_DIRECTION_NORTH))
            return Direction.NORTH;

        //3d
        if (isTargetWord(s, WORD_DIRECTION_3D_BACKWARD))
            return Direction.BACKWARD;

        else if (isTargetWord(s, WORD_DIRECTION_3D_FORWARD))
            return Direction.FORWARD;

        else if (isTargetWord(s, WORD_DIRECTION_3D_UPWARD))
            return Direction.UPWARD;

        else if (isTargetWord(s, WORD_DIRECTION_3D_DOWNWARD))
            return Direction.DOWNWARD;

        else if (isTargetWord(s, WORD_DIRECTION_3D_CENTER))
            return Direction.CENTER;

        else return Direction.NONE;
    }

}
