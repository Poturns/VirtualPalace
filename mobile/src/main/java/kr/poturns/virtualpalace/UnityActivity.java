package kr.poturns.virtualpalace;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.google.unity.GoogleUnityActivity;

import kr.poturns.virtualpalace.annotation.UnityApi;
import kr.poturns.virtualpalace.controller.AndroidUnityBridge;
import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.input.GlobalApplication;
import kr.poturns.virtualpalace.input.IControllerCommands;
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

        // connector 와 InputHandler를 동작하게 하기
        int wearSupportType =
                IControllerCommands.TYPE_INPUT_SUPPORT_WATCH |
                        IControllerCommands.TYPE_INPUT_SUPPORT_MOTION;

        GlobalApplication app = (GlobalApplication) getApplication();
        Handler inputHandler = app.setInputConnector(wearSupportType, wearInputConnector);
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
