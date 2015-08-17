package kr.poturns.virtualpalace.input;

/**
 * <b>INPUT 모듈에서 처리해야할 기능을 정의하는 인터페이스.</b>
 * <p>
 * INPUT 모듈에서 생성하는 INPUT 데이터 단위를 GENERIC 타입으로 지정한다.
 * </p>
 *
 * @author Yeonho.Kim
 */
public interface IOperationInputFilter<InputUnit> {

    // * * * D I R E C T I O N  C O N S T A N T S * * * //
    /**
     * NO - DIRECTION
     */
    public static final int DIRECTION_NONE = -1;
    /**
     * DIRECTION : 동쪽
     */
    public static final int DIRECTION_EAST = 1;
    /**
     * DIRECTION : 오른쪽
     */
    public static final int DIRECTION_RIGHT = 1;
    /**
     * DIRECTION : 서쪽
     */
    public static final int DIRECTION_WEST = 2;
    /**
     * DIRECTION : 왼쪽
     */
    public static final int DIRECTION_LEFT = 2;
    /**
     * DIRECTION : 북쪽
     */
    public static final int DIRECTION_NORTH = 3;
    /**
     * DIRECTION : 위쪽
     */
    public static final int DIRECTION_UP = 3;
    /**
     * DIRECTION : 남쪽
     */
    public static final int DIRECTION_SOUTH = 4;
    /**
     * DIRECTION : 아래쪽
     */
    public static final int DIRECTION_DOWN = 4;
    /**
     * DIRECTION : 3D 중앙
     */
    public static final int DIRECTION_3D_CENTER = 0;
    /**
     * DIRECTION : 3D 앞쪽
     */
    public static final int DIRECTION_3D_FORWARD = 5;
    /**
     * DIRECTION : 3D 뒷쪽
     */
    public static final int DIRECTION_3D_BACKWARD = 6;
    /**
     * DIRECTION : 3D 하늘 방향
     */
    public static final int DIRECTION_3D_UPWARD = 7;
    /**
     * DIRECTION : 3D 땅 방향
     */
    public static final int DIRECTION_3D_DOWNWARD = 8;


    // * * * O P E R A T I O N  C O N S T A N T S * * * //
    /**
     * NO - OPERATION
     */
    public static final int OPERATION_NONE = -1;
    /**
     * OK 하드웨어 키를 누른 효과를 나타낸다.
     */
    public static final int OPERATION_KEY_OK = 1;
    /**
     * BACK 하드웨어 키를 누른 효과를 나타낸다.
     */
    public static final int OPERATION_KEY_BACK = 2;
    /**
     * HOME 하드웨어 키를 누른 효과를 나타낸다.
     */
    public static final int OPERATION_KEY_HOME = 3;
    /**
     * VOL.UP 하드웨어 키를 누른 효과를 나타낸다.
     */
    public static final int OPERATION_KEY_VOLUME_UP = 4;
    /**
     * VOL.DOWN 하드웨어 키를 누른 효과를 나타낸다.
     */
    public static final int OPERATION_KEY_VOLUME_DOWN = 5;

    /**
     * OPERATION : 전진한다.
     */
    public static final int OPERATION_GO = 11;
    /**
     * OPERATION : 회전한다.
     */
    public static final int OPERATION_TURN = 12;
    /**
     * OPERATION : Focus를 이동한다.
     */
    public static final int OPERATION_FOCUS = 13;
    /**
     * OPERATION : 화면 확대/축소 전환을 한다.
     */
    public static final int OPERATION_ZOOM = 14;
    /**
     * OPERATION : 선택한다.
     */
    public static final int OPERATION_SELECT = 15;
    /**
     * OPERATION : 취소한다.
     */
    public static final int OPERATION_CANCEL = 16;


    // * * * O P E R A T I O N  F I L T E R  C A L L B A C K S * * * //
    /**
     * Return 방향으로 이동한다.
     * @param unit
     * @return
     */
    int isGoingTo(InputUnit unit);

    /**
     * Return 방향으로 회전한다.
     * @param unit
     * @return
     */
    int isTurningTo(InputUnit unit);

    /**
     * Return 방향으로 포커싱(Focusing)한다.
     * @param unit
     * @return
     */
    int isFocusingTo(InputUnit unit);

    /**
     * Return 방향으로 화면조정(Zooming)한다.
     * @param unit
     * @return
     */
    int isZoomingTo(InputUnit unit);

    /**
     * 선택 이벤트를 발생한다.
     * @param unit
     * @return
     */
    boolean isSelecting(InputUnit unit);

}
