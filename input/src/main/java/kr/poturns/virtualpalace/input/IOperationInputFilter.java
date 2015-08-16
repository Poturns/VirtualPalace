package kr.poturns.virtualpalace.input;

/**
 * Created by YeonhoKim on 2015-07-06.
 */
public interface IOperationInputFilter<InputUnit> {

    public static final int DIRECTION_NONE = 0;
    public static final int DIRECTION_EAST = 1;
    public static final int DIRECTION_WEST = 2;
    public static final int DIRECTION_SOUTH = 3;
    public static final int DIRECTION_NORTH = 4;

    public static final int DIRECTION__3D_CENTER = 0;
    public static final int DIRECTION_3D_FORWARD = 5;
    public static final int DIRECTION_3D_BACKWARD = 6;
    public static final int DIRECTION_3D_UPWARD = 7;
    public static final int DIRECTION_3D_DOWNWARD = 8;

    public static final int OPERATION_KEY_OK = 1;
    public static final int OPERATION_KEY_BACK = 2;
    public static final int OPERATION_KEY_HOME = 3;
    public static final int OPERATION_KEY_VOLUME_UP = 4;
    public static final int OPERATION_KEY_VOLUME_DOWN = 5;

    public static final int OPERATION_GO = 11;
    public static final int OPERATION_TURN = 12;
    public static final int OPERATION_FOCUS = 13;
    public static final int OPERATION_ZOOM = 14;
    public static final int OPERATION_SELECT = 15;

    /**
     * Return 방향으로 이동한다.
     * @param unit 입력
     * @return 이동 할 방향
     */
    int isGoingTo(InputUnit unit);

    /**
     * Return 방향으로 회전한다.
     * @param unit 입력
     * @return 회전 할 방향
     */
    int isTurningTo(InputUnit unit);

    /**
     * Return 방향으로 포커싱(Focusing)한다.
     * @param unit 입력
     * @return 포커싱 할 방향
     */
    int isFocusingTo(InputUnit unit);

    /**
     * Return 방향으로 화면조정(Zooming)한다.
     * @param unit 입력
     * @return 화면 조정 할 방향
     */
    int isZoomingTo(InputUnit unit);

    /**
     * 선택 이벤트를 발생한다.
     * @param unit 입력
     * @return 선택 이벤트의 발생 여부
     */
    boolean isSelecting(InputUnit unit);

}
