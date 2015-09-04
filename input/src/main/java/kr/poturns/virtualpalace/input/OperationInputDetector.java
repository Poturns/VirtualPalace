package kr.poturns.virtualpalace.input;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <b>INPUT 데이터를 검출하는 DETECTOR.</b>
 * <p>
 * {@link IOperationInputFilter}를 통해서 INPUT 데이터로부터 명령 메시지를 추출한다.
 * {@link OperationInputConnector}를 등록하여, 추출한 명령 메시지를 전송한다.
 *
 * 명령 계수를 수정하여 각 INPUT DETECTOR 마다 명령수행 수치를 변경할 수 있다.
 * 기본적으로 검출된 데이터를 일정 주기 간격으로 일괄 처리한다.
 * </p>
 *
 * @author Yeonho.Kim
 */
public class OperationInputDetector<InputUnit> implements IOperationInputFilter<InputUnit> {

    // * * * C O N S T A N T S * * * //
    private static final long TRANSFER_PERIOD = 200; // 단위 : ms  >> 5 FPS

    /**
     * Operation 일괄 처리 Queue.
     */
    private final LinkedList<int[]> mOperationBatchQueue = new LinkedList<int[]>();

    private final Timer mTransferTimerF = new Timer();


    // * * * F I E L D S * * * //
    /**
     * 회전 계수 :
     */
    protected int turningAmount = 10;  // degree
    /**
     * 이동 계수 :
     */
    protected int goingAmount = 1;    // meter
    /**
     * 화면조정 계수 :
     */
    protected int zoomingAmount = 2;    // scale
    /**
     * 포커싱 계수 :
     */
    protected int focusingAmount = 1;   // meter

    /**
     * 입력 검출 필터
     */
    private IOperationInputFilter<InputUnit> mInputFilter;
    /**
     * 데이터 전송 커넥터
     */
    private OperationInputConnector mConnector;
    /**
     * 검출된 명령 데이터
     */
    private int[] mDetectedCommand;
    /**
     * 주기적 일괄 전송 플래그
     */
    private boolean isBatchProcessing;



    // * * * C O S T R U C T O R S * * * //
    public OperationInputDetector() {
        this(null);
    }

    public OperationInputDetector(IOperationInputFilter<InputUnit> filter) {
        setOperationInputFilter(filter);
        setBatchProcessing(true);
    }


    // * * * M E T H O D S * * * //
    /**
     * {@link IOperationInputFilter}를 등록한다.
     * Generic Type이 {@link OperationInputDetector}와 일치해야한다.
     *
     * @param filter
     */
    public void setOperationInputFilter(IOperationInputFilter<InputUnit> filter) {
        mInputFilter = filter;
    }

    /**
     * 명령 일괄 처리 여부를 설정한다.
     * OFF시, {@link #mOperationBatchQueue}를 비우고, 자동 전송모드으로 전환된다.
     *
     * @param on
     */
    public void setBatchProcessing(boolean on) {
        isBatchProcessing = on;

        if (isBatchProcessing) {
            mTransferTimerF.schedule(new TimerTask() {
                @Override
                public void run() {
                    flushOperationQueue();
                }
            }, TRANSFER_PERIOD, TRANSFER_PERIOD);

        } else {
            mTransferTimerF.cancel();
            flushOperationQueue();
        }
    }

    /**
     * {@link OperationInputConnector}를 등록한다.
     *
     * @param connector
     */
    public void setOperationInputConnector(OperationInputConnector connector) {
        // 기존에 등록되어 있던 Connector 처리.
        flushOperationQueue();

        mConnector = connector;
    }

    /**
     * 보유하고 있는 명령 메시지를 일괄 전송한 후, QUEUE 를 초기화한다.
     * {@link OperationInputConnector}가 등록되어 있지 않더라도 QUEUE 를 비운다.
     *
     * @return
     */
    public synchronized boolean flushOperationQueue() {
        boolean able = ! (mConnector == null || mOperationBatchQueue.isEmpty());
        if (able) {
            int[][] operations = new int[mOperationBatchQueue.size()][];
            mOperationBatchQueue.toArray(operations);
            mConnector.transferDataset(operations);
        }

        mOperationBatchQueue.clear();
        return able;
    }

    /**
     * 전달받은 입력 데이터에서 명령을 검출한다.
     *
     * @param unit
     * @throws NullPointerException
     *          {@link IOperationInputFilter}가 등록되지 않았을 경우
     */
    public final synchronized boolean detect(InputUnit unit) {
        // 통계적 관점에서 파악한 후, 기본적인 우선순위를 정한다.
        int[] priority = {
                Operation.KEY_HOME, // 하드웨어 키 명령 대표
                Operation.SELECT,
                Operation.CANCEL,
                Operation.GO,
                Operation.TURN,
                Operation.FOCUS,
                Operation.ZOOM,
                Operation.DEEP  // (3) 특수 명령 대표
        };

        return detect(unit, priority);
    }

    /**
     * 예상되는 Operation 들의 bit 코드를 우선적으로 판별한다.
     * 판별하지 못했을 경우, 수행되지 않았던 나머지 Operation 을 순차적으로 판별하여,
     * 전달받은 입력 데이터에서 명령을 검출한다.
     *
     * @param unit 입력
     * @param expected 예상 Operations (OR 중첩 가능)
     * @return
     */
    public final synchronized boolean detect(InputUnit unit, int expected) {
        int rest = ((Operation.TERMINATE << 2) -1) ^ expected;
        boolean result = distinguishExpectedFirst(unit, expected) | distinguishExpectedFirst(unit, rest);

        onDetected();
        return result;
    }

    /**
     * Operation 을 판별할 우선 순위 배열을 작성하여
     * 전달받은 입력 데이터에서 명령을 검출한다.
     * (※ 우선순위 배열에 추가된 Operation 에 한하여 판별작업을 수행한다.)
     *
     * @param unit 입력
     * @param priority 우선순위 배열
     * @return
     */
    public final synchronized boolean detect(InputUnit unit, int[] priority) {
        boolean result = false;
        for(int operation : priority) {
            if (result = distinguishOnce(unit, operation))
                break;
        }

        onDetected();
        return result;
    }

    /**
     * 예상되는 Operation 들을 우선적으로 판별한다.
     *
     * @param unit 입력
     * @param expected 예상 Operations (OR 중첩 가능)
     * @return
     */
    private final synchronized  boolean distinguishExpectedFirst(InputUnit unit, int expected) {
        for(int picker = 1; picker <= expected; picker = picker << 1) {
            if ((expected & picker) > 0) {
                if (distinguishOnce(unit, picker))
                    return true;

                // 이하 Operation 들은 한 번의 메소드 호출로 판별되기 때문에,
                // 다음 단계로 점프하도록 한다.
                switch (picker) {
                    case Operation.KEY_HOME:
                    case Operation.KEY_MENU:
                    case Operation.KEY_VOLUME_UP:
                    case Operation.KEY_VOLUME_DOWN:
                        picker = Operation.KEY_VOLUME_DOWN;
                        break;

                    case Operation.DEEP:
                    case Operation.SEARCH:
                    case Operation.CONFIG:
                    case Operation.TERMINATE:
                        picker = Operation.TERMINATE;
                        break;
                }
            }
        }
        return false;
    }

    /**
     * 대상이 되는 하나의 Operation 에 대해 판별작업을 수행한다.
     *
     * @param unit 입력
     * @param checkOperation 대상 Operation
     * @return
     */
    private final synchronized  boolean distinguishOnce(InputUnit unit, int checkOperation) {
        switch(checkOperation) {
            case Operation.SELECT:
                return isSelecting(unit);

            case Operation.CANCEL:
                return isCanceling(unit);

            case Operation.GO:
                return isGoingTo(unit) > Direction.NONE;

            case Operation.TURN:
                return isTurningTo(unit) > Direction.NONE;

            case Operation.FOCUS:
                return isFocusingTo(unit) > Direction.NONE;

            case Operation.ZOOM:
                return isZoomingTo(unit) > Direction.NONE;

            // Operation.KEY_OK:
            // Operation.KEY_BACK :
            case Operation.KEY_HOME:
            case Operation.KEY_MENU:
            case Operation.KEY_VOLUME_DOWN:
            case Operation.KEY_VOLUME_UP:
                return isKeyPressed(unit) > Operation.NONE;

            case Operation.DEEP:
            case Operation.SEARCH:
            case Operation.CONFIG:
            case Operation.TERMINATE:
                return isSpecialOperation(unit) > Operation.NONE;
        }

        return false;
    }

    /**
     * 명령을 검출한 이후, 수행할 기능을 정의한다.
     * 일괄 작업이 설정되어있을 경우 검출한 데이터를 {@link #mOperationBatchQueue}에 추가하고,
     * 그렇지 않을 경우 즉시 연결된 {@link OperationInputConnector}를 통해 데이터를 전송한다.
     * 이때, Connector 가 연결되어 있지 않을 경우, 검출된 명령은 사라진다.
     */
    private void onDetected() {
        if (mDetectedCommand != null) {
            if (isBatchProcessing)
                mOperationBatchQueue.push(mDetectedCommand);
            else
            if (mConnector != null)
                mConnector.transferDataset(mDetectedCommand);

            mDetectedCommand = null;
        }
    }

    /**
     * Return 방향으로 이동한다.
     *
     * @param inputUnit input
     * @return 방향
     * @throws NullPointerException
     *          {@link IOperationInputFilter}가 등록되지 않았을 경우
     *          {@link OperationInputConnector}가 등록되지 않았을 경우
     */
    @Override
    public int isGoingTo(InputUnit inputUnit) {
        int direction = mInputFilter.isGoingTo(inputUnit);
        if (direction > Direction.NONE) {
            mDetectedCommand = new int[]{
                    Operation.GO,
                    direction,
                    goingAmount
            };
        }
        return direction;
    }

    /**
     * Return 방향으로 회전한다.
     *
     * @param inputUnit input
     * @return 방향
     * @throws NullPointerException
     *          {@link IOperationInputFilter}가 등록되지 않았을 경우
     *          {@link OperationInputConnector}가 등록되지 않았을 경우
     */
    @Override
    public int isTurningTo(InputUnit inputUnit) {
        int direction = mInputFilter.isTurningTo(inputUnit);
        if (direction > Direction.NONE) {
            mDetectedCommand = new int[]{
                    Operation.TURN,
                    direction,
                    turningAmount
            };
        }
        return direction;
    }

    /**
     * Return 방향으로 포커싱(Focusing)한다.
     *
     * @param inputUnit input
     * @return 방향
     * @throws NullPointerException
     *          {@link IOperationInputFilter}가 등록되지 않았을 경우
     *          {@link OperationInputConnector}가 등록되지 않았을 경우
     */
    @Override
    public int isFocusingTo(InputUnit inputUnit) {
        int direction = mInputFilter.isFocusingTo(inputUnit);
        if (direction > Direction.NONE) {
            mDetectedCommand = new int[]{
                    Operation.FOCUS,
                    direction,
                    focusingAmount
            };
        }
        return direction;
    }

    /**
     * Return 방향으로 화면조정(Zooming)한다.
     *
     * @param inputUnit input
     * @return 방향
     * @throws NullPointerException
     *          {@link IOperationInputFilter}가 등록되지 않았을 경우
     *          {@link OperationInputConnector}가 등록되지 않았을 경우
     */
    @Override
    public int isZoomingTo(InputUnit inputUnit) {
        int direction = mInputFilter.isZoomingTo(inputUnit);
        if (direction > Direction.NONE) {
            mDetectedCommand = new int[]{
                    Operation.ZOOM,
                    direction,
                    zoomingAmount
            };
        }
        return direction;
    }

    /**
     * 선택 이벤트를 발생한다.
     *
     * @param inputUnit input
     * @return 선택 여부
     * @throws NullPointerException
     *          {@link IOperationInputFilter}가 등록되지 않았을 경우
     *          {@link OperationInputConnector}가 등록되지 않았을 경우
     */
    @Override
    public boolean isSelecting(InputUnit inputUnit) {
        boolean result = mInputFilter.isSelecting(inputUnit);
        if (result) {
            mDetectedCommand = new int[]{
                    Operation.SELECT,
                    0,
                    0
            };
        }
        return result;
    }

    /**
     * 취소 이벤트를 발생한다.
     *
     * @param inputUnit 입력
     * @return 취소 이벤트의 발생 여부
     */
    @Override
    public boolean isCanceling(InputUnit inputUnit) {
        boolean result = mInputFilter.isCanceling(inputUnit);
        if (result) {
            mDetectedCommand = new int[]{
                    Operation.CANCEL,
                    0,
                    0
            };
        }
        return result;
    }

    /**
     * Return 에 해당하는 특수 기능 코드를 반환한다.
     *
     * @param inputUnit 입력
     * @return {#Operation} 내 KEY.. OPERATIONS.
     */
    @Override
    public int isKeyPressed(InputUnit inputUnit) {
        int operation = mInputFilter.isKeyPressed(inputUnit);
        if (operation < Operation.GO) {
            mDetectedCommand = new int[]{
                    operation,
                    0,
                    0
            };
        } else
            operation = Operation.NONE;

        return operation;
    }

    /**
     * Return 에 해당하는 특수 기능 코드를 반환한다.
     *
     * @param inputUnit 입력
     * @return 특수 기능 코드
     */
    @Override
    public int isSpecialOperation(InputUnit inputUnit) {
        // blockMask에 추가된 Operation은 InputUnit을 통해 감지가 되어도, 정상적으로 전달되지 않는다.
        int blockMask = 0;
        int operation = mInputFilter.isSpecialOperation(inputUnit) ^ blockMask;

        if (operation < Operation.DEEP)
            operation = Operation.NONE;

        else {
            mDetectedCommand = new int[]{
                    operation,
                    0,
                    0
            };
        }

        return operation;
    }
}