package kr.poturns.virtualpalace;

import android.os.Bundle;
import android.view.KeyEvent;

import com.google.unity.GoogleUnityActivity;

import kr.poturns.virtualpalace.annotation.UnityApi;
import kr.poturns.virtualpalace.controller.AndroidUnityBridge;
import kr.poturns.virtualpalace.controller.PalaceApplication;

/**
 * VirtualPalace 어플리케이션의 MainActivity
 */
public class UnityActivity extends GoogleUnityActivity {

    private AndroidUnityBridge mAndroidUnityBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAndroidUnityBridge = AndroidUnityBridge.getInstance((PalaceApplication) getApplication());
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }

        return super.onKeyDown(i, keyEvent);
    }

    @UnityApi
    public AndroidUnityBridge getAndroidUnityBridge() {
        return mAndroidUnityBridge;
    }
}
