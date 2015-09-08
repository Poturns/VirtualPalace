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

    /**
     * <b>DIRECTION  CONSTANTS</b>
     *
     * 1-차원 : x축 > 1의 자리
     * 2-차원 : y축 > 10의 자리
     * 3-차원 : z축 > 100의 자리
     *
     * ( 0 = 해당 축에 값이 존재하지 않는 경우 )
     * ( 유효한 수의 범위 = 1 ~ 9 )
     * ( 각 축의 영점 = 5 )
     *
     * Example ) 2차원일 경우, (16방위표)
     *               N
     *               9-
     *
     *               7-    * => 77 .. NE 북동쪽.
     *
     * W 1-    3-    5-    7-    9- E (x축)
     *
     *               3-
     *
     *               1-    * => 17 .. SSE 남남동쪽
     *               S
     *              (y축)
     *
     * Example ) 3차원일 경우, 스마트폰 카메라 시야를 기준으로 축을 설정함.
     *
     */
    interface Direction {
        /**
         * 0차원 : NO- DIRECTION
         */
        public static final int NONE = 0;  // 000 이지만, Java 에서 8진수로 인식하기에 앞 0 생략.
        /**
         * N 차원 : 중앙
         */
        public static final int CENTER = 5;
        /**
         * 1차원 : 오른쪽
         */
        public static final int RIGHT = 7;  // 07 이지만, Java 에서 8진수로 인식하기에 앞 0 생략.
        /**
         * 1차원 : 왼쪽
         */
        public static final int LEFT = 3;  // 03 이지만, Java 에서 8진수로 인식하기에 앞 0 생략.
        /**
         * 1차원 : 위쪽
         */
        public static final int UP = 70;
        /**
         * 1차원 : 아래쪽
         */
        public static final int DOWN = 30;
        /**
         * 2차원 : 동쪽
         */
        public static final int EAST = 57;
        /**
         * 2차원 : 서쪽
         */
        public static final int WEST = 53;
        /**
         * 2차원 : 남쪽
         */
        public static final int SOUTH = 35;
        /**
         * 2차원 : 북쪽
         */
        public static final int NORTH = 75;
        /**
         * 3차원 : 앞쪽
         */
        public static final int FORWARD = 755;
        /**
         * 3차원 : 뒷쪽
         */
        public static final int BACKWARD = 355;
        /**
         * 3차원 : 하늘 방향
         */
        public static final int UPWARD = 575;
        /**
         * 3차원 : 땅 방향
         */
        public static final int DOWNWARD = 535;
        /**
         * 3차원 : 좌측 방향
         */
        public static final int LEFTWARD = 553;
        /**
         * 3차원 : 우측 방향
         */
        public static final int RIGHTWARD = 557;
    }



    // * * * O P E R A T I O N  C O N S T A N T S * * * //
    interface Operation {
        /**
         * NO - OPERATION
         */
        public static final int NONE = 0x0;
        /**
         * OK/Confirm 키를 누른 효과를 나타낸다.
         */
        public static final int KEY_OK = 0x1;
        /**
         * BACK 하드웨어 키를 누른 효과를 나타낸다.
         */
        public static final int KEY_BACK = 0x2;
        /**
         * MENU 하드웨어 키를 누른 효과를 나타낸다.
         */
        public static final int KEY_MENU = 0x4;
        /**
         * HOME 하드웨어 키를 누른 효과를 나타낸다.
         */
        public static final int KEY_HOME = 0x8;
        /**
         * VOL.UP 하드웨어 키를 누른 효과를 나타낸다.
         */
        public static final int KEY_VOLUME_UP = 0x10;
        /**
         * VOL.DOWN 하드웨어 키를 누른 효과를 나타낸다.
         */
        public static final int KEY_VOLUME_DOWN = 0x20;

        /**
         * (1) 결정 기능 : 선택한다.    >> OK / Confirm 하드웨어 키를 눌렀을 때와 동일.
         */
        public static final int SELECT = 0x1;
        /**
         * (1) 결정 기능 : 취소한다.    >> BACK 하드웨어 키를 눌렀을 때와 동일.
         */
        public static final int CANCEL = 0x2;
        /**
         * (2) 기본 기능 : 전진한다.
         */
        public static final int GO = 0x100;
        /**
         * (2) 기본 기능 : 회전한다.
         */
        public static final int TURN = 0x200;
        /**
         * (2) 기본 기능 : Focus 를 이동한다.
         */
        public static final int FOCUS = 0x400;
        /**
         * (2) 기본 기능 : 화면 확대/축소 전환을 한다.
         */
        public static final int ZOOM = 0x800;
        /**
         * (3) 특수 기능 : OPERATION : 숨겨진 기능을 수행한다. (LongClick, 오른쪽클릭 등의 효과)
         */
        public static final int DEEP = 0x1000;
        /**
         * (3) 특수 기능 : 검색한다.
         */
        public static final int SEARCH = 0x2000;
        /**
         * (3) 특수 기능 : 설정/수정한다.
         */
        public static final int CONFIG = 0x4000;
        /**
         * (3) 특수 기능 : 모드를 전환한다.
         */
        public static final int SWITCH_MODE = 0x8000;
        /**
         * (3) 특수 기능 : 종료한다.
         */
        public static final int TERMINATE = 0x10000;
    }



    // * * * O P E R A T I O N  F I L T E R  C A L L B A C K S * * * //
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

    /**
     * 취소 이벤트를 발생한다.
     * @param unit 입력
     * @return 취소 이벤트의 발생 여부
     */
    boolean isCanceling(InputUnit unit);

    /**
     * Return 에 해당하는 특수 기능 코드를 반환한다.
     * @param unit 입력
     * @return {#Operation} 내 KEY.. OPERATIONS.
     */
    int isKeyPressed(InputUnit unit);

    /**
     * Return 에 해당하는 특수 기능 코드를 반환한다.
     * @param unit 입력
     * @return 특수 기능 코드
     */
    int isSpecialOperation(InputUnit unit);

}
