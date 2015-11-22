package kr.poturns.virtualpalace.inputmodule.speech;

import java.util.List;

import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * Created by Myungjin Kim on 2015-11-21.
 * <p/>
 * 문자열이 비슷한 지 여부로 명령을 판별하는 IOperationInputFilter
 */
public class SimilaritySpeechFilter implements IOperationInputFilter<List<String>> {
    /*
        성능을 위해 첫번째 문자열만 체크를 한다.
     */

    private static final int BASE_CODE_POINT = '\uAC00';

    private static final char[][] HANGUL_MAP = {
            {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'},
            {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'},
            {' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'}
    };

    private static final int _21x28 = 588;
    private static final int _28 = 28;

    private static final char[] CODES_OPERATION_KEY_BACK = stringToSyllable("뒤로");
    private static final char[] CODES_OPERATION_KEY_HOME = stringToSyllable("홈");
    private static final char[] CODES_OPERATION_KEY_VOLUME_UP = stringToSyllable("소리크게");
    private static final char[] CODES_OPERATION_KEY_VOLUME_DOWN = stringToSyllable("소리작게");

    private static final char[] CODES_OPERATION_SPECIAL_EXIT = stringToSyllable("종료");
    private static final char[] CODES_OPERATION_SPECIAL_SEARCH = stringToSyllable("검색");

    private static final char[] CODES_OPERATION_SPECIAL_CANCEL = stringToSyllable("취소");
    private static final char[] CODES_OPERATION_SPECIAL_CANCEL_BACK = stringToSyllable("뒤로");

    private static final char[] CODES_OPERATION_SPECIAL_SWITCH_MODE = stringToSyllable("모드");
    private static final char[] CODES_OPERATION_SPECIAL_MENU = stringToSyllable("메뉴");

    private static final char[] CODES_OPERATION_SPECIAL_SELECT = stringToSyllable("선택");
    private static final char[] CODES_OPERATION_SPECIAL_SELECT_CONFIRM = stringToSyllable("확인");

    private static final char[] CODES_OPERATION_SPECIAL_DEEP = stringToSyllable("딥");

    //private static final char[] CODES_DIRECTION_NONE = "";
    private static final char[] CODES_DIRECTION_PREV = stringToSyllable("이전");
    private static final char[] CODES_DIRECTION_NEXT = stringToSyllable("다음");

    private static final char[] CODES_DIRECTION_EAST = stringToSyllable("동쪽");
    private static final char[] CODES_DIRECTION_WEST = stringToSyllable("서쪽");
    private static final char[] CODES_DIRECTION_SOUTH = stringToSyllable("남쪽");
    private static final char[] CODES_DIRECTION_NORTH = stringToSyllable("북쪽");

    private static final char[] CODES_DIRECTION_3D_CENTER = stringToSyllable("가운데로");
    private static final char[] CODES_DIRECTION_3D_FORWARD = stringToSyllable("앞으로");
    private static final char[] CODES_DIRECTION_3D_BACKWARD = stringToSyllable("뒤로");
    private static final char[] CODES_DIRECTION_3D_UPWARD = stringToSyllable("위로");
    private static final char[] CODES_DIRECTION_3D_DOWNWARD = stringToSyllable("아래로");

    private static final char[] ERROR_CHARS = {};

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

    private int checkDirection(List<String> strings) {
        return checkDirection(makeCharArray(strings.get(0)));
    }

    @Override
    public boolean isSelecting(List<String> strings) {
        return false;
    }

    @Override
    public boolean isCanceling(List<String> strings) {
        return false;
    }

    @Override
    public int isKeyPressed(List<String> string) {
        return checkKeyOperation(string);
    }

    private static int checkKeyOperation(List<String> strings) {
        return checkKeyOperation(makeCharArray(strings.get(0)));
    }

    @Override
    public int isSpecialOperation(List<String> string) {
        return checkSpecialOperation(string);
    }

    private static int checkSpecialOperation(List<String> strings) {
        return checkSpecialOperation(makeCharArray(strings.get(0)));
    }


    private static String removeSpace(String string) {
        return string.replace(" ", "");
    }

    private static char[] makeCharArray(String input) {
        try {
            return stringToSyllable(removeSpace(input));
        } catch (Exception e) {
            return ERROR_CHARS;
        }
    }

    private static int checkKeyOperation(char[] input) {
        if (checkWordSimilarity(input, CODES_OPERATION_SPECIAL_SELECT))
            return Operation.KEY_OK;

        else if (checkWordSimilarity(input, CODES_OPERATION_KEY_BACK))
            return Operation.KEY_BACK;

        else if (checkWordSimilarity(input, CODES_OPERATION_KEY_HOME))
            return Operation.KEY_HOME;

        else if (checkWordSimilarity(input, CODES_OPERATION_KEY_VOLUME_UP))
            return Operation.KEY_VOLUME_UP;

        else if (checkWordSimilarity(input, CODES_OPERATION_KEY_VOLUME_DOWN))
            return Operation.KEY_VOLUME_DOWN;

        else return Operation.NONE;
    }

    private static int checkSpecialOperation(char[] input) {
        if (checkWordSimilarity(input, CODES_OPERATION_SPECIAL_CANCEL) || checkWordSimilarity(input, CODES_OPERATION_SPECIAL_CANCEL_BACK))
            return Operation.CANCEL;

        else if (checkWordSimilarity(input, CODES_OPERATION_SPECIAL_EXIT))
            return Operation.TERMINATE;

        else if (checkWordSimilarity(input, CODES_OPERATION_SPECIAL_MENU))
            return Operation.KEY_MENU;

        else if (checkWordSimilarity(input, CODES_OPERATION_SPECIAL_SEARCH))
            return Operation.SEARCH;

        else if (checkWordSimilarity(input, CODES_OPERATION_SPECIAL_SELECT) || checkWordSimilarity(input, CODES_OPERATION_SPECIAL_SELECT_CONFIRM))
            return Operation.SELECT;

        else if (checkWordSimilarity(input, CODES_OPERATION_SPECIAL_SWITCH_MODE))
            return Operation.SWITCH_MODE;

        else if (checkWordSimilarity(input, CODES_OPERATION_SPECIAL_DEEP))
            return Operation.DEEP;

        else return Operation.NONE;
    }

    private int checkDirection(char[] input) {
        //2d
        if (checkWordSimilarity(input, CODES_DIRECTION_EAST) || checkWordSimilarity(input, CODES_DIRECTION_NEXT))
            return Direction.EAST;

        else if (checkWordSimilarity(input, CODES_DIRECTION_WEST) || checkWordSimilarity(input, CODES_DIRECTION_PREV))
            return Direction.WEST;

        else if (checkWordSimilarity(input, CODES_DIRECTION_SOUTH))
            return Direction.SOUTH;

        else if (checkWordSimilarity(input, CODES_DIRECTION_NORTH))
            return Direction.NORTH;

        //3d
        if (checkWordSimilarity(input, CODES_DIRECTION_3D_BACKWARD))
            return Direction.BACKWARD;

        else if (checkWordSimilarity(input, CODES_DIRECTION_3D_FORWARD))
            return Direction.FORWARD;

        else if (checkWordSimilarity(input, CODES_DIRECTION_3D_UPWARD))
            return Direction.UPWARD;

        else if (checkWordSimilarity(input, CODES_DIRECTION_3D_DOWNWARD))
            return Direction.DOWNWARD;

        else if (checkWordSimilarity(input, CODES_DIRECTION_3D_CENTER))
            return Direction.CENTER;

        else return Direction.NONE;
    }


    private static boolean checkWordSimilarity(char[] input, char[] target) {
        final int N = input.length;
        if (N == target.length) {
            int similarity = 0;

            for (int i = 0; i < N; i++)
                if (input[i] == target[i]) ++similarity;

            if (N - similarity < 1 + (N / 3) * 2)
                return true;
        }

        return false;
    }

    /**
     * 주어진 한글 문자열을 분리하여 음소의 배열로 반환한다.
     */
    private static char[] stringToSyllable(String string) {

        final int N = string.length();
        char[] array = new char[N * 3];

        for (int i = 0; i < N; i++) {
            int codePoint = string.codePointAt(i) - BASE_CODE_POINT;

            /*
            int first = codePoint / _21x28;
            int second = codePoint % _21x28 / _28;
            int last = codePoint % _28;
            */

            //초성
            int first = codePoint / _21x28;

            int firstX21X28 = first * _21x28;

            //중성
            int second = (codePoint - firstX21X28) / _28;
            //종성
            int last = codePoint - firstX21X28 - second * 28;

            int arrayIndex = i * 3;
            array[arrayIndex] = HANGUL_MAP[0][first];
            array[arrayIndex + 1] = HANGUL_MAP[1][second];
            array[arrayIndex + 2] = HANGUL_MAP[2][last];
        }

        return array;
    }

}
