package kr.poturns.virtualpalace.inputmodule;

import android.content.Context;

import java.io.IOException;

import kr.poturns.virtualpalace.communication.WearMessageObject;
import kr.poturns.virtualpalace.communication.WearableCommunicator;
import kr.poturns.virtualpalace.input.OperationInputConnector;

/**
 * Created by Myungjin Kim on 2015-08-18.
 * <p/>
 * 웨어러블에서 발생한 Input 을 Mobile 장비로 전송하는 OperationInputConnector
 */
public class WearInputConnector extends OperationInputConnector {
    private WearableCommunicator mWearableCommunicator;

    public WearInputConnector(Context context) {
        super(context, "Wear");

        this.mWearableCommunicator = new WearableCommunicator(context);
    }

    /**
     * GoogleApiClient 와 연결한다.
     */
    public void connect() {
        mWearableCommunicator.connect();
    }

    /**
     * GoogleApiClient 와 연결 해제한다.
     */
    public void disconnect() {
        mWearableCommunicator.disconnect();
    }

    /**
     * GoogleApiClient 와 연결 해제하고, 리소스를 정리한다.
     */
    public void destroy() {
        mWearableCommunicator.destroy();
    }

    @Override
    protected boolean transferDataset(int[] inputRst) {
        return transferDataset(new int[][]{inputRst});
    }

    @Override
    protected boolean transferDataset(int[][] inputRstArray) {
        if (!mWearableCommunicator.isConnected())
            return false;

        WearMessageObject messageObject = new WearMessageObject();
        messageObject.dataSet = inputRstArray;

        try {
            mWearableCommunicator.sendMessage(messageObject.toBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
