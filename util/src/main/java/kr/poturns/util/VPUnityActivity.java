package kr.poturns.util;

import android.os.Bundle;

import com.unity3d.player.UnityPlayerActivity;


public class VPUnityActivity extends UnityPlayerActivity {
    InputHandleHelperProxy inputHandleHelperProxy;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        inputHandleHelperProxy = new InputHandleHelperProxy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        inputHandleHelperProxy.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        inputHandleHelperProxy.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        inputHandleHelperProxy.onDestroy();
        inputHandleHelperProxy = null;
    }

    public InputHandleHelperProxy getInputHandleHelperProxy() {
        return inputHandleHelperProxy;
    }

}
