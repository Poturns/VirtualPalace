package kr.poturns.virtualpalace.input;

import android.content.Context;

import java.util.LinkedList;

/**
 *
 * @author YeonhoKim
 */
public class OperationInputDetector<InputUnit> implements IOperationInputFilter<InputUnit> {

    protected final Context mContextF;

    private IOperationInputFilter<InputUnit> mInputFilter;

    private OperationInputConnector mConnector;

    private boolean isOnAutoTransfer = false;

    private boolean isBatchProcessing = true;

    /**
     * Operation 일괄 처리 Queue.
     */
    private final LinkedList<int[]> mOperationBatchQueue;

    public OperationInputDetector(Context context) {
        this(context, null);
    }

    public OperationInputDetector(Context context, IOperationInputFilter<InputUnit> filter) {
        mContextF = context;
        mInputFilter = filter;

        mOperationBatchQueue = new LinkedList<int[]>();
    }

    /**
     * 입력 명령 일괄 처리 여부를 설정한다.
     * OFF시, {@link #mOperationBatchQueue}를 비우고, 자동 전송모드으로 전환된다.
     *
     * @param on
     */
    public void setBatchProcessing(boolean on) {
        isBatchProcessing = on;
        isOnAutoTransfer = !on || isOnAutoTransfer;

        if (!isBatchProcessing)
            mOperationBatchQueue.clear();
    }

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
     * {@link OperationInputConnector}를 등록한다.
     * 자동 전송 모드 = false
     *
     * @param connector
     * @see #setOperationInputConnector(OperationInputConnector, boolean)
     */
    public void setOperationInputConnector(OperationInputConnector connector) {
        setOperationInputConnector(connector, false);
    }

    /**
     * {@link OperationInputConnector}를 등록한다.
     * 자동 전송 모드가 설정되어 있으면, Connector에 자동으로 데이터를 전송하도록 한다.
     *
     * @param connector
     * @param auto
     */
    public void setOperationInputConnector(OperationInputConnector connector, boolean auto) {
        // 기존에 등록되어 있던 Connector 처리.
        if (mConnector != null) {
            mConnector.bindDetector(null);
            flushOperations();
        }

        // 새 Connector 및 Auto 속성 설정.
        mConnector = connector;
        isOnAutoTransfer = auto || !isBatchProcessing;

        if (auto && connector != null)
            connector.bindDetector(this);
    }

    /**
     * 전달받은 입력 데이터에서 명령을 감지한다.
     * @param unit
     */
    public boolean detect(InputUnit unit) {
        if (mInputFilter == null)
            return false;

        boolean detected =
                isSelecting(unit) ||
                (isGoingTo(unit) > DIRECTION_NONE) ||
                (isZoomingTo(unit) > DIRECTION_NONE) ||
                (isTurningTo(unit) > DIRECTION_NONE) ||
                (isFocusingTo(unit) > DIRECTION_NONE) ;

        return detected;
    }


    /**
     *
     * @return
     */
    public synchronized boolean flushOperations() {
        if (mConnector == null)
            return false;

        int[][] operations = new int[mOperationBatchQueue.size()][];
        mOperationBatchQueue.toArray(operations);
        mOperationBatchQueue.clear();

        mConnector.transferMultipleDataset(operations);
        return true;
    }

    /**
     *
     */
    public void cancelLastOperation() {

        mOperationBatchQueue.pollLast();
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
        if (direction > DIRECTION_NONE) {
            int[] rst = new int[]{
                    OPERATION_GO,
                    direction
            };

            if (isBatchProcessing)
                mOperationBatchQueue.push(rst);
            else
                mConnector.transferSingleDataset(rst);
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
        if (direction > DIRECTION_NONE) {
            int[] rst = new int[]{
                    OPERATION_TURN,
                    direction
            };

            if (isBatchProcessing)
                mOperationBatchQueue.push(rst);
            else
                mConnector.transferSingleDataset(rst);
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
        if (direction > DIRECTION_NONE) {
            int[] rst = new int[]{
                    OPERATION_FOCUS,
                    direction
            };

            if (isBatchProcessing)
                mOperationBatchQueue.push(rst);
            else
                mConnector.transferSingleDataset(rst);
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
        if (direction > DIRECTION_NONE) {
            int[] rst = new int[]{
                    OPERATION_ZOOM,
                    direction
            };

            if (isBatchProcessing)
                mOperationBatchQueue.push(rst);
            else
                mConnector.transferSingleDataset(rst);
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
        int[] rst = new int[]{
                OPERATION_SELECT
        };

        if (isBatchProcessing)
            mOperationBatchQueue.push(rst);
        else
            mConnector.transferSingleDataset(rst);
        return result;
    }
}