package kr.poturns.virtualpalace.input;

/**
 * Created by YeonhoKim on 2015-07-06.
 */
public interface IOperationInputFilter<InputUnit> {

    /**
     * 방향 상수
     */
    public enum Direction {
        // 무(無) 방향
        NONE,

        // 3D 방향
        UPWARD,     // 위
        DOWNWARD,   // 아래
        FORWARD,    // 앞
        BACKWARD,   // 뒤

        // 2D 방향
        EAST,       // 동
        WEST,       // 서
        SOUTH,      // 남
        NORTH,      // 북
        N_E,        // 북동
        N_W,        // 북서
        S_E,        // 남동
        S_W,        // 남서
        CENTER      // 중앙
    }

    /**
     * 명령어 상수
     */
    public enum Operation {
        GO,
        TURN,
        FOCUS,
        ZOOM,
        SELECT
    }

    /**
     * Return 방향으로 이동한다.
     * @param unit
     * @return
     */
    Direction isGoingTo(InputUnit unit);

    /**
     * Return 방향으로 회전한다.
     * @param unit
     * @return
     */
    Direction isTurningTo(InputUnit unit);

    /**
     * Return 방향으로 포커싱(Focusing)한다.
     * @param unit
     * @return
     */
    Direction isFocusingTo(InputUnit unit);

    /**
     * Return 방향으로 화면조정(Zooming)한다.
     * @param unit
     * @return
     */
    Direction isZoomingTo(InputUnit unit);

    /**
     * 선택 이벤트를 발생한다.
     * @param unit
     * @return
     */
    boolean isSelecting(InputUnit unit);

}
