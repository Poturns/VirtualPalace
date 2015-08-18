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

    public void startInputDetecting() {
        mSpeechInputHelper.startListening();
    }

    public void stopInputDetecting() {
        mSpeechInputHelper.stopListening();
    }

    public void cancelInputDetecting() {
        mSpeechInputHelper.cancelListening();
    }

    public void destroy() {
        mSpeechInputHelper.destroy();
    }

    @Override
    public void onResult(SpeechResults speechResults) {
        if (speechResults.results.isEmpty())
            return;

        detect(speechResults.results);
    }


}
