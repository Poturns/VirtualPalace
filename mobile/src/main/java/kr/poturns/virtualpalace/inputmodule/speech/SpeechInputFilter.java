package kr.poturns.virtualpalace.inputmodule.speech;

import java.util.ArrayList;

import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * STT 를 통해 얻어진 결과를 적절한 방향 / 명령 으로 해석하는 클래스
 */
public class SpeechInputFilter implements IOperationInputFilter<ArrayList<String>> {

    private static final String WORD_OPERATION_KEY_OK = "ok";
    private static final String WORD_OPERATION_KEY_BACK = "뒤로";
    private static final String WORD_OPERATION_KEY_HOME = "홈";
    private static final String WORD_OPERATION_KEY_VOLUME_UP = "소리 크게";
    private static final String WORD_OPERATION_KEY_VOLUME_DOWN = "소리 작게";

    private static final String WORD_OPERATION_GO = "가";
    private static final String WORD_OPERATION_TURN = "돌아";
    private static final String WORD_OPERATION_FOCUS = "포커스";
    private static final String WORD_OPERATION_ZOOM = "확대";
    private static final String WORD_OPERATION_SELECT = "선택";

    //private static final String WORD_DIRECTION_NONE = "";
    private static final String WORD_DIRECTION_EAST = "동쪽";
    private static final String WORD_DIRECTION_WEST = "서쪽";
    private static final String WORD_DIRECTION_SOUTH = "남쪽";
    private static final String WORD_DIRECTION_NORTH = "북쪽";

    private static final String WORD_DIRECTION_3D_CENTER = "가운데로";
    private static final String WORD_DIRECTION_3D_FORWARD = "앞으로";
    private static final String WORD_DIRECTION_3D_BACKWARD = "뒤로";
    private static final String WORD_DIRECTION_3D_UPWARD = "위로";
    private static final String WORD_DIRECTION_3D_DOWNWARD = "아래로";

    @Override
    public int isGoingTo(ArrayList<String> strings) {
        return 0;
    }

    @Override
    public int isTurningTo(ArrayList<String> strings) {
        return 0;
    }

    @Override
    public int isFocusingTo(ArrayList<String> strings) {
        return 0;
    }

    @Override
    public int isZoomingTo(ArrayList<String> strings) {
        return 0;
    }

    @Override
    public boolean isSelecting(ArrayList<String> strings) {
        return false;
    }

    @Override
    public boolean isCanceling(ArrayList<String> strings) {
        return false;
    }

    @Override
    public int isKeyPressed(ArrayList<String> strings) {
        return 0;
    }

    @Override
    public int isSpecialOperation(ArrayList<String> strings) {
        return 0;
    }

    private int checkOperation(ArrayList<String> strings) {
        final int N = strings.size();
        for (int i = 0; i < N; i++) {
            String s = strings.get(i);

            if (WORD_OPERATION_GO.equals(s))
                return Operation.GO;

            else if (WORD_OPERATION_FOCUS.equals(s))
                return Operation.FOCUS;

            else if (WORD_OPERATION_SELECT.equals(s))
                return Operation.SELECT;

            else if (WORD_OPERATION_TURN.equals(s))
                return Operation.TURN;

            else if (WORD_OPERATION_ZOOM.equals(s))
                return Operation.ZOOM;
        }
        return Direction.NONE;
    }

    private int checkOperationForSpecial(ArrayList<String> strings) {
        final int N = strings.size();
        for (int i = 0; i < N; i++) {
            String s = strings.get(i);

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
        }
        return Direction.NONE;
    }

    private int checkDirectionFor3D(ArrayList<String> strings) {
        final int N = strings.size();
        for (int i = 0; i < N; i++) {
            String s = strings.get(i);

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
        }
        return Direction.NONE;
    }

    private int checkDirectionFor2D(ArrayList<String> strings) {
        final int N = strings.size();
        for (int i = 0; i < N; i++) {
            String s = strings.get(i);

            if (WORD_DIRECTION_EAST.equals(s))
                return Direction.EAST;

            else if (WORD_DIRECTION_WEST.equals(s))
                return Direction.WEST;

            else if (WORD_DIRECTION_SOUTH.equals(s))
                return Direction.SOUTH;

            else if (WORD_DIRECTION_NORTH.equals(s))
                return Direction.NORTH;
        }
        return Direction.NONE;
    }


}
