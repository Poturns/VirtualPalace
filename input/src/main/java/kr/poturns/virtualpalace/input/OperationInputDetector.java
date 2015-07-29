package kr.poturns.virtualpalace.input;

import android.content.Context;
import android.util.Pair;

import java.util.LinkedList;

/**
 * Created by YeonhoKim on 2015-07-06.
 */
public class OperationInputDetector<InputUnit> implements IOperationInputFilter<InputUnit> {

    private final Context mContextF;

    private IOperationInputFilter<InputUnit> mInputFilter;

    private OperationInputConnector mConnector;

    private boolean isOnAutoCommunication;

    private final LinkedList<Pair<Integer, Object>> mOperationQueue;

    public OperationInputDetector(Context context) {
        this(context, null);
    }

    public OperationInputDetector(Context context, IOperationInputFilter<InputUnit> filter) {
        mContextF = context;
        mInputFilter = filter;

        mOperationQueue = new LinkedList<Pair<Integer, Object>>();
    }

    protected Context getContext() {
        return mContextF;
    }

    public void setOperationInputFilter(IOperationInputFilter<InputUnit> filter) {
        mInputFilter = filter;
    }

    public void setOperationInputConnector(OperationInputConnector connector) {
        setOperationInputConnector(connector, false);
    }

    public void setOperationInputConnector(OperationInputConnector connector, boolean auto) {
        mConnector = connector;
        isOnAutoCommunication = auto;
    }

    public final void detect(InputUnit unit) {
        if (mInputFilter == null)
            return;


    }

    public synchronized boolean flushOperations() {
        if (mConnector == null)
            return false;

        Pair<Integer, Object>[] operations = new Pair[mOperationQueue.size()];
        mOperationQueue.toArray(operations);
        mOperationQueue.clear();

        // TODO : mOperationConnector . send()
        return true;
    }

    public void cancelLastOperation() {
        mOperationQueue.pollLast();
    }

    /**
     * Return 방향으로 이동한다.
     *
     * @param inputUnit input
     * @return 방향
     * @throws NullPointerException {@link IOperationInputFilter}가 등록되지 않았을 경우
     */
    @Override
    public int isGoingTo(InputUnit inputUnit) {
        int d = mInputFilter.isGoingTo(inputUnit);
        mOperationQueue.push(new Pair<Integer, Object>(OPERATION_GO, d));

        if (isOnAutoCommunication) {

        }
        return d;
    }

    /**
     * Return 방향으로 회전한다.
     *
     * @param inputUnit input
     * @return 방향
     * @throws NullPointerException {@link IOperationInputFilter}가 등록되지 않았을 경우
     */
    @Override
    public int isTurningTo(InputUnit inputUnit) {
        int d = mInputFilter.isTurningTo(inputUnit);
        mOperationQueue.push(new Pair<Integer, Object>(OPERATION_TURN, d));

        if (isOnAutoCommunication) {

        }
        return d;
    }

    /**
     * Return 방향으로 포커싱(Focusing)한다.
     *
     * @param inputUnit input
     * @return 방향
     * @throws NullPointerException {@link IOperationInputFilter}가 등록되지 않았을 경우
     */
    @Override
    public int isFocusingTo(InputUnit inputUnit) {
        int d = mInputFilter.isFocusingTo(inputUnit);
        mOperationQueue.push(new Pair<Integer, Object>(OPERATION_FOCUS, d));

        if (isOnAutoCommunication) {

        }
        return d;
    }

    /**
     * Return 방향으로 화면조정(Zooming)한다.
     *
     * @param inputUnit input
     * @return 방향
     * @throws NullPointerException {@link IOperationInputFilter}가 등록되지 않았을 경우
     */
    @Override
    public int isZoomingTo(InputUnit inputUnit) {
        int d = mInputFilter.isZoomingTo(inputUnit);
        mOperationQueue.push(new Pair<Integer, Object>(OPERATION_ZOOM, d));

        if (isOnAutoCommunication) {

        }
        return d;
    }

    /**
     * 선택 이벤트를 발생한다.
     *
     * @param inputUnit input
     * @return 선택 여부
     * @throws NullPointerException {@link IOperationInputFilter}가 등록되지 않았을 경우
     */
    @Override
    public boolean isSelecting(InputUnit inputUnit) {
        boolean result = mInputFilter.isSelecting(inputUnit);
        mOperationQueue.push(new Pair<Integer, Object>(OPERATION_SELECT, result));

        if (isOnAutoCommunication) {

        }
        return result;
    }
}