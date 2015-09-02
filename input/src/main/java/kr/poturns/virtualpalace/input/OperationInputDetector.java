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
    public int turningAmount = 10;  // degree
    /**
     * 이동 계수 :
     */
    public int goingAmount = 1;    // meter
    /**
     * 화면조정 계수 :
     */
    public int zoomingAmount = 2;    // scale
    /**
     * 포커싱 계수 :
     */
    public int focusingAmount = 1;   // meter

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
        boolean detected =
                (isKeyPressed(unit) > Operation.NONE) ||
                        isSelecting(unit) || isCanceling(unit) ||
                        (isGoingTo(unit) > Direction.NONE) ||
                        (isZoomingTo(unit) > Direction.NONE) ||
                        (isTurningTo(unit) > Direction.NONE) ||
                        (isFocusingTo(unit) > Direction.NONE) ||
                        (isSpecialOperation(unit) > Operation.NONE);

        if (mDetectedCommand != null) {
            if (isBatchProcessing)
                mOperationBatchQueue.push(mDetectedCommand);
            else
            if (mConnector != null)
                mConnector.transferDataset(mDetectedCommand);

            mDetectedCommand = null;
        }

        return detected;
    }

    /**
     * 호출될 것으로 예상되는 Operation 을 가장 먼저 감지한다.
     * 감지하지 못했을 경우, {@link #detect(Object)} 메소드를 수행한다.
     *
     * @param unit 입력
     * @param expectedOperation 예상 Operation
     * @return
     */
    public final synchronized boolean detect(InputUnit unit, int expectedOperation) {

        return detect(unit);
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
        int operation = mInputFilter.isSpecialOperation(inputUnit);
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