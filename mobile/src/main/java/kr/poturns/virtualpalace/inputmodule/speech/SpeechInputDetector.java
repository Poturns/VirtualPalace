package kr.poturns.virtualpalace.inputmodule.speech;

import android.content.Context;

import kr.poturns.virtualpalace.input.OperationInputDetector;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 음성 형태의 입력을 처리하는 InputModule
 */
public class SpeechInputDetector extends OperationInputDetector<String> implements SpeechController.OnSpeechDataListener, SpeechController {
    //private static final String TAG = "SpeechInputDetector";
    private SpeechInputHelper mSpeechInputHelper;
    private int mRecognitionMode;
    private SpeechController.OnSpeechDataListener listener;

    public SpeechInputDetector(Context context) {
        super(new SpeechInputFilter());
        this.mSpeechInputHelper = new SpeechInputHelper(context);
        mSpeechInputHelper.setSpeechListener(this);
    }

    @Override
    public void setSpeechListener(OnSpeechDataListener speechListener) {
        listener = speechListener;
    }

    @Override
    public void setContinueRecognizing(boolean continueRecognizing) {
        mSpeechInputHelper.setContinueRecognizing(continueRecognizing);
    }

    @Override
    public void setRecognitionMode(int mode) {
        this.mRecognitionMode = mode;
    }

    @Override
    public void startSpeechListening() {
        mSpeechInputHelper.startListening();
    }

    @Override
    public void stopSpeechListening() {
        mSpeechInputHelper.stopListening();
    }

    @Override
    public void cancelSpeechListening() {
        mSpeechInputHelper.cancelListening();
    }

    @Override
    public void destroy() {
        mSpeechInputHelper.destroy();
        mSpeechInputHelper.setSpeechListener(null);
        mSpeechInputHelper = null;
    }

    @Override
    public void onResult(SpeechResult speechResult) {

        switch (mRecognitionMode) {
            default:
            case MODE_COMMAND:
                detect(speechResult.result);
                break;

            case MODE_MEMO:
                if (listener != null)
                    listener.onResult(speechResult);
                break;
        }
    }


    @Override
    public void onError(int cause) {
        switch (mRecognitionMode) {
            default:
            case MODE_COMMAND:
                break;

            case MODE_MEMO:
                if (listener != null)
                    listener.onError(cause);
                break;
        }
    }


}
