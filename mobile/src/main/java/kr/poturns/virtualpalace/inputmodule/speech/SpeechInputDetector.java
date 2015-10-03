package kr.poturns.virtualpalace.inputmodule.speech;

import android.content.Context;

import java.util.ArrayList;

import kr.poturns.virtualpalace.input.OperationInputDetector;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 음성 형태의 입력을 처리하는 InputModule
 */
public class SpeechInputDetector extends OperationInputDetector<ArrayList<String>> implements SpeechInputHelper.OnSpeechDataListener {
    //private static final String TAG = "SpeechInputDetector";
    private SpeechInputHelper mSpeechInputHelper;

    public SpeechInputDetector(Context context) {
        super(new SpeechInputFilter());
        this.mSpeechInputHelper = new SpeechInputHelper(context);
        mSpeechInputHelper.setSpeechListener(this);
    }

    /**
     * 음성인식 수행이 끝나도 계속 음성인식을 수행하는지 여부를 설정한다.
     *
     * @param continueRecognizing True : 음성인식을 계속 수행
     */
    public void setContinueRecognizing(boolean continueRecognizing) {
        mSpeechInputHelper.setContinueRecognizing(continueRecognizing);
    }

    /**
     * 음성 인식을 통한 명령 감지를 실행한다.
     */
    public void startInputDetecting() {
        mSpeechInputHelper.startListening();
    }

    /**
     * 음성 인식을 통한 명령 감지를 중단한다.
     */
    public void stopInputDetecting() {
        mSpeechInputHelper.stopListening();
    }

    /**
     * 음성 인식을 취소한다.
     */
    public void cancelInputDetecting() {
        mSpeechInputHelper.cancelListening();
    }

    /**
     * 음성입력을 중단하고, 관련된 자원을 모두 회수한다.
     */
    public void destroy() {
        mSpeechInputHelper.destroy();
        mSpeechInputHelper.setSpeechListener(null);
        mSpeechInputHelper = null;
    }

    @Override
    public void onResult(SpeechResults speechResults) {
        if (speechResults.results.isEmpty())
            return;

        detect(speechResults.results);
    }

    @Override
    public void onError(int cause) {
    }


}
