package kr.poturns.virtualpalace;

import android.os.Bundle;
import android.view.KeyEvent;

import com.google.unity.GoogleUnityActivity;

import kr.poturns.virtualpalace.annotation.UnityApi;
import kr.poturns.virtualpalace.controller.AndroidUnityBridge;
import kr.poturns.virtualpalace.controller.PalaceApplication;
import kr.poturns.virtualpalace.input.IControllerCommands;
import kr.poturns.virtualpalace.input.OperationInputConnector;
import kr.poturns.virtualpalace.inputmodule.speech.SpeechInputDetector;
import kr.poturns.virtualpalace.inputmodule.wear.WearInputConnector;

/**
 * VirtualPalace 어플리케이션의 MainActivity
 */
public class UnityActivity extends GoogleUnityActivity {

    private AndroidUnityBridge mAndroidUnityBridge;
    private WearInputConnector mWearInputConnector;
    private SpeechInputDetector mSpeechInputDetector;
    private OperationInputConnector mSpeechInputConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PalaceApplication application = (PalaceApplication) getApplication();
        mAndroidUnityBridge = AndroidUnityBridge.getInstance(application);
        mWearInputConnector = new WearInputConnector(application);

        mSpeechInputDetector = new SpeechInputDetector(application);
        mSpeechInputDetector.setContinueRecognizing(true);
        mSpeechInputConnector = new OperationInputConnector(application, IControllerCommands.TYPE_INPUT_SUPPORT_VOICE);
        mSpeechInputDetector.setOperationInputConnector(mSpeechInputConnector);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWearInputConnector.connect();
        mSpeechInputDetector.startInputDetecting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWearInputConnector.disconnect();
        mSpeechInputDetector.stopInputDetecting();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PalaceApplication application = (PalaceApplication) getApplication();

        //TODO InputConnector 에서 disconnect 메소드를 지원하는 건 어떤지?
        application.setInputConnector(mWearInputConnector.getSupportType(), null);
        mWearInputConnector.destroy();
        mWearInputConnector = null;

        application.setInputConnector(mSpeechInputConnector.getSupportType(), null);
        mSpeechInputDetector.destroy();
        mSpeechInputDetector = null;
        mSpeechInputConnector = null;

        mAndroidUnityBridge = null;
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
