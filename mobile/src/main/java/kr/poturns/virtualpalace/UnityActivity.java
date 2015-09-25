package kr.poturns.virtualpalace;

import android.os.Bundle;
import android.view.KeyEvent;

import com.google.unity.GoogleUnityActivity;

import kr.poturns.virtualpalace.annotation.UnityApi;
import kr.poturns.virtualpalace.controller.AndroidUnityBridge;
import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.controller.PalaceMaster;
import kr.poturns.virtualpalace.input.GlobalApplication;
import kr.poturns.virtualpalace.input.IControllerCommands;
import kr.poturns.virtualpalace.input.OperationInputConnector;
import kr.poturns.virtualpalace.inputmodule.wear.WearInputConnector;

/**
 * VirtualPalace 어플리케이션의 MainActivity
 */
public class UnityActivity extends GoogleUnityActivity {

    private AndroidUnityBridge mAndroidUnityBridge;
    private WearInputConnector wearInputConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAndroidUnityBridge = AndroidUnityBridge.getInstance((PalaceApplication) getApplication());
        wearInputConnector = new WearInputConnector(getApplicationContext());

        //TODO connector 와 InputHandler를 동작하게 하기
        wearInputConnector.configureFromController((GlobalApplication) getApplication(), OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_TRUE);
        PalaceMaster.getInstance((PalaceApplication) getApplication()).getInputHandler().sendEmptyMessage(IControllerCommands.INPUT_SYNC_COMMAND);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wearInputConnector.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wearInputConnector.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wearInputConnector.destroy();
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }

        return super.onKeyDown(i, keyEvent);
    }

    /**
     * AndroidUnityBridge 객체를 반환한다.
     */
    @UnityApi
    public AndroidUnityBridge getAndroidUnityBridge() {
        return mAndroidUnityBridge;
    }
}
