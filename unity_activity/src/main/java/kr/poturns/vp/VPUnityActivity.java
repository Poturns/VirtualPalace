package kr.poturns.vp;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.unity3d.player.UnityPlayerActivity;

import kr.poturns.util.InputHandleHelperProxy;

public class VPUnityActivity extends UnityPlayerActivity {
    InputHandleHelperProxy inputHandleHelperProxy;
    OnBackPressListener onBackPressListener;

    public interface OnBackPressListener{
        boolean onBackPressed();
    }

    Toast exitToast;
    long backPressTime;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        inputHandleHelperProxy = new InputHandleHelperProxy(this);
        exitToast = Toast.makeText(this, "press 'back' again to exit.", Toast.LENGTH_SHORT);
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

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }

        return super.onKeyDown(i, keyEvent);
    }

    @Override
    public void onBackPressed() {
        long backTime = System.currentTimeMillis();

        if(onBackPressListener != null && onBackPressListener.onBackPressed())
            return;

        if (backTime - backPressTime > 2000) {
            exitToast.show();
            backPressTime = backTime;
        } else {
            exitToast.cancel();
            finish();
        }

    }

    public InputHandleHelperProxy getInputHandleHelperProxy() {
        return inputHandleHelperProxy;
    }

    public void setOnBackPressListener(OnBackPressListener l){
        this.onBackPressListener = l;
    }
}
