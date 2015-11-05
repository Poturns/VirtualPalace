package kr.poturns.virtualpalace.inputmodule.speech;

import android.content.Context;

import java.util.List;

import kr.poturns.virtualpalace.input.OperationInputDetector;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 음성 형태의 입력을 처리하는 InputModule
 */
public class SpeechInputDetector extends OperationInputDetector<List<String>> implements SpeechController.OnSpeechDataListener, SpeechController {
    //private static final String TAG = "SpeechInputDetector";
    private SpeechInputHelper mSpeechInputHelper;
    private int mRecognitionMode;
    //TODO 리스너를 활용하지 않고, OperationConnector의 text전송 메소드 사용하기
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
    public void setRecognitionMode(@RecognitionMode int mode) {
        this.mRecognitionMode = mode;

        switch (mode){
            // Text 입력 모드의 경우, 지속적인 인식요청은 하지 않고
            // 잠시 후에 음성인식을 시작한다.
            case MODE_TEXT:
                mSpeechInputHelper.cancelListening();
                mSpeechInputHelper.setContinueRecognizing(false);
                mSpeechInputHelper.delayedStartRecognition();
                break;

            // Input 입력 모드의 경우, Input을 꾸준히 입력받기 위해 지속적인 인식요청을 하고
            // 잠시 후에 음성인식을 시작한다.
            default:
            case MODE_COMMAND:
                mSpeechInputHelper.cancelListening();
                mSpeechInputHelper.setContinueRecognizing(true);
                mSpeechInputHelper.delayedStartRecognition();
                break;


        }
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
    public void onResult(SpeechResult speechResult, int mode) {

        switch (mRecognitionMode) {
            default:
            case MODE_COMMAND:
                detect(speechResult.result);

                // Command 인식 시에도 음성인식이 제대로 됬음을 알려주기 위해서
                // 인식한 텍스트를 Listener로 전송. >> break 주석처리. - Y H
                //break;

            // Text 입력 모드의 경우, 따로 정의된 리스너에 입력을 전송하고, 지속적인 인식요청은 하지 않고
            // 잠시 후에 음성인식을 시작한다.
            case MODE_TEXT:
                if (listener != null)
                    listener.onResult(speechResult, mRecognitionMode);

                setRecognitionMode(MODE_COMMAND);

                break;
        }
    }


    @Override
    public void onError(int cause, int mode) {
        switch (mRecognitionMode) {
            default:
            case MODE_COMMAND:
                break;

            case MODE_TEXT:
                if (listener != null)
                    listener.onError(cause, mRecognitionMode);

                setRecognitionMode(MODE_COMMAND);
                break;
        }
    }


}
