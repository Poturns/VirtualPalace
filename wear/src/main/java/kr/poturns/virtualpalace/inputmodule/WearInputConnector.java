package kr.poturns.virtualpalace.inputmodule;

import android.content.Context;

import kr.poturns.virtualpalace.communication.WearableCommunicator;
import kr.poturns.virtualpalace.input.OperationInputConnector;

/**
 * Created by Myungjin Kim on 2015-07-30.
 *
 * 웨어러블에서 발생한 Input 을 Mobile 장비로 전송하는 클래스
 */
public class WearInputConnector extends OperationInputConnector {

    private WearableCommunicator mWearableCommunicator;

    public WearInputConnector(Context context) {
        mWearableCommunicator = new WearableCommunicator(context);
    }

    public void onResume(){
        mWearableCommunicator.connect();
    }

    public void onPause(){
        mWearableCommunicator.disconnect();
    }

    public void onDestroy(){
        mWearableCommunicator.destroy();
    }

    public boolean send(String message) {
        return mWearableCommunicator.sendMessage(message);
    }

}
