package kr.poturns.virtualpalace;

import android.view.KeyEvent;

import com.google.unity.GoogleUnityActivity;

public class UnityActivity extends GoogleUnityActivity {

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }

        return super.onKeyDown(i, keyEvent);
    }
}
