package kr.poturns.virtualpalace.inputmodule.wear;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

import java.io.IOException;

import kr.poturns.virtualpalace.communication.WearMessageObject;
import kr.poturns.virtualpalace.communication.WearableCommunicator;
import kr.poturns.virtualpalace.input.IProcessorCommands;
import kr.poturns.virtualpalace.input.OperationInputConnector;
import kr.poturns.virtualpalace.util.ThreadUtils;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * * Wear module로 부터 전송받은 데이터를 컨트롤러에게 전달하는 OperationInputConnector
 */
public class WearInputConnector extends OperationInputConnector implements MessageApi.MessageListener {
    private static final String TAG = "WearInputConnector";
    private WearableCommunicator mWearableCommunicator;

    public WearInputConnector(Context context) {
        super(context, IProcessorCommands.TYPE_INPUT_SUPPORT_WATCH | IProcessorCommands.TYPE_INPUT_SUPPORT_MOTION);
        mWearableCommunicator = new WearableCommunicator(context);
    }

    /**
     * GoogleApiClient 및 Controller  와 연결한다.
     */
    public void connect() {
        mWearableCommunicator.connect();
        mWearableCommunicator.setMessageListener(this);
    }

    /**
     * GoogleApiClient 및 Controller 와 연결 해제한다.
     */
    @Override
    public void disconnect() {
        mWearableCommunicator.disconnect();
        mWearableCommunicator.setMessageListener(null);
    }


    /**
     * GoogleApiClient 와 연결 해제하고, 리소스를 정리한다.
     */
    public void destroy() {
        mWearableCommunicator.destroy();
        mWearableCommunicator = null;
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        String messagePath = messageEvent.getPath();
        if (WearableCommunicator.MESSAGE_PATH_INPUT_DATA.equals(messagePath)) {
            sendInputMessage(messageEvent.getData());
        } else if (WearableCommunicator.MESSAGE_PATH_STRING_MESSAGE.equals(messagePath)) {
            //TODO 다른 메소드 이용하기 - 현재 이 메소드는 음성인식 전용
            transferTextData(new String(messageEvent.getData()));
        }
    }

    private void sendInputMessage(final byte[] wearableMessage) {
        ThreadUtils.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    WearMessageObject messageObject = WearMessageObject.fromBytes(wearableMessage);
                    if (messageObject.dataSet.length > 1)
                        transferDataset(messageObject.dataSet);

                    else if (messageObject.dataSet.length == 1)
                        transferDataset(messageObject.dataSet[0]);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "주어진 바이트 배열로 부터 메시지를 생성할 수 없습니다.");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, "WearMessageObject 클래스 정보를 찾을 수 없습니다.");
                }
            }
        });
    }

}