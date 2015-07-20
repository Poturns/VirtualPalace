package kr.poturns.virtualpalace.input;

import android.content.Context;

import java.util.LinkedList;

import kr.poturns.virtualpalace.util.Pair;

/**
 * Created by YeonhoKim on 2015-07-06.
 */
public class OperationInputDetector<InputUnit> implements IOperationInputFilter<InputUnit> {

    private final Context mContextF;

    private IOperationInputFilter mInputFilter;

    private OperationInputConnector mConnector;

    private boolean isOnAutoCommunication;

    private final LinkedList<Pair<Operation, Object>> mOperationQueue;

    public OperationInputDetector(Context context) {
        this(context, null);
    }

    public OperationInputDetector(Context context, IOperationInputFilter<InputUnit> filter) {
        mContextF = context;
        mInputFilter = filter;

        mOperationQueue = new LinkedList<Pair<Operation, Object>>();
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

        Pair<Operation, Object>[] operations = new Pair[mOperationQueue.size()];
        mOperationQueue.toArray(operations);
        mOperationQueue.clear();

        // TODO : mOperationConnector . send()
        return true;
    }

    public void cancelLastOperation() {
        mOperationQueue.pollLast();
    }

    /**
     * Return �������� �̵��Ѵ�.
     *
     * @param inputUnit
     * @exception {@link IOperationInputFilter}�� ��ϵ��� �ʾ��� ���, {@link NullPointerException}�� �߻��Ѵ�.
     * @return
     */
    @Override
    public Direction isGoingTo(InputUnit inputUnit) {
        Direction d = mInputFilter.isGoingTo(inputUnit);
        mOperationQueue.push(new Pair<Operation, Object>(Operation.GO, d));

        if (isOnAutoCommunication) {

        }
        return d;
    }

    /**
     * Return �������� ȸ���Ѵ�.
     *
     * @param inputUnit
     * @exception {@link IOperationInputFilter}�� ��ϵ��� �ʾ��� ���, {@link NullPointerException}�� �߻��Ѵ�.
     * @return
     */
    @Override
    public Direction isTurningTo(InputUnit inputUnit) {
        Direction d = mInputFilter.isTurningTo(inputUnit);
        mOperationQueue.push(new Pair<Operation, Object>(Operation.TURN, d));

        if (isOnAutoCommunication) {

        }
        return d;
    }

    /**
     * Return �������� ��Ŀ��(Focusing)�Ѵ�.
     *
     * @param inputUnit
     * @exception {@link IOperationInputFilter}�� ��ϵ��� �ʾ��� ���, {@link NullPointerException}�� �߻��Ѵ�.
     * @return
     */
    @Override
    public Direction isFocusingTo(InputUnit inputUnit) {
        Direction d = mInputFilter.isFocusingTo(inputUnit);
        mOperationQueue.push(new Pair<Operation, Object>(Operation.FOCUS, d));

        if (isOnAutoCommunication) {

        }
        return d;
    }

    /**
     * Return �������� ȭ������(Zooming)�Ѵ�.
     *
     * @param inputUnit
     * @exception {@link IOperationInputFilter}�� ��ϵ��� �ʾ��� ���, {@link NullPointerException}�� �߻��Ѵ�.
     * @return
     */
    @Override
    public Direction isZoomingTo(InputUnit inputUnit) {
        Direction d = mInputFilter.isZoomingTo(inputUnit);
        mOperationQueue.push(new Pair<Operation, Object>(Operation.ZOOM, d));

        if (isOnAutoCommunication) {

        }
        return d;
    }

    /**
     * ���� �̺�Ʈ�� �߻��Ѵ�.
     *
     * @param inputUnit
     * @exception {@link IOperationInputFilter}�� ��ϵ��� �ʾ��� ���, {@link NullPointerException}�� �߻��Ѵ�.
     * @return
     */
    @Override
    public boolean isSelecting(InputUnit inputUnit) {
        boolean result = mInputFilter.isSelecting(inputUnit);
        mOperationQueue.push(new Pair<Operation, Object>(Operation.SELECT, result));

        if (isOnAutoCommunication) {

        }
        return result;
    }
}