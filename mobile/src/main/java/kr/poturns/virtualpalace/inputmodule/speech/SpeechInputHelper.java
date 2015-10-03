package kr.poturns.virtualpalace.inputmodule.speech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 음성인식을 도와주는 Helper 클래스
 */
public class SpeechInputHelper implements RecognitionListener {
    private static final String TAG = "SpeechInputHelper";
    public static final int ACTIVITY_REQUEST_CODE = 9567;
    private static final int HANDLER_MSG_START_LISTENING = 1234;

    private SpeechRecognizer mRecognizer;
    private Intent speechIntent;
    private SpeechController.OnSpeechDataListener listener;

    /**
     * 현재 음성인식을 수행중인지 여부
     */
    private boolean isInRecognizing = false;

    /**
     * 지속적인 음성인식을 수행할 지 여부
     */
    private boolean isContinueRecognizing = false;
    /**
     * 지속적인 음성인식을 수행할 때, 그 시간 간격
     */
    private long delay = 1000;
    private Handler mHandler;

    private final Context mContext;

    public SpeechInputHelper(Context context) {
        this.mContext = context.getApplicationContext();
        mHandler = new SpeechHandler(this);
        initSTT();
    }

    /**
     * 음성인식 결과를 전달받을 리스너를 등록한다.
     *
     * @param listener 음성인식 결과를 전달받을 리스너
     */
    public void setSpeechListener(SpeechController.OnSpeechDataListener listener) {
        this.listener = listener;
    }

    /**
     * 음성인식 수행이 끝나도 계속 음성인식을 수행하는지 여부를 설정한다.
     *
     * @param continueRecognizing True : 음성인식을 계속 수행
     */
    public void setContinueRecognizing(boolean continueRecognizing) {
        isContinueRecognizing = continueRecognizing;
    }

    /**
     * 음성인식 수행이 끝나도 계속 음성인식을 수행하는 경우, 얼마나 지연된 뒤 수행될 지 결정한다.
     *
     * @param delay 음성인식이 끝나고 다음 음성인식을 수행하기 까지 대기할 시간.
     */
    public void setContinueRecognizingDelay(long delay) {
        this.delay = delay;
    }

    private void initSTT() {
        //음성인식 intent 생성
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //데이터 설정
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
        //음성인식 언어 설정
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        speechIntent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        speechIntent.putExtra("android.speech.extra.GET_AUDIO", true);

        //음성인식 객체
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        //음성인식 리스너 등록
        mRecognizer.setRecognitionListener(this);
    }

    // STT 관리 메소드

    /**
     * 음성인식을 시작한다.
     * <p/>
     * 음성인식 결과는 {@link SpeechController.OnSpeechDataListener}를 통해 전달된다.
     */
    public void startListening() {
        mRecognizer.startListening(speechIntent);
    }

    /**
     * 음성인식을 중지한다.
     */
    public void stopListening() {
        mRecognizer.stopListening();
    }

    /**
     * 음성인식을 취소한다.
     */
    public void cancelListening() {
        mRecognizer.cancel();
    }

    /**
     * 음성인식 객체를 해제한다.
     */
    public void destroy() {
        mRecognizer.stopListening();
        mRecognizer.destroy();
        mRecognizer = null;

        mHandler = null;
    }


    // STT 음성 데이터 관련 메소드

    /**
     * 음성 데이터를 얻을 수 있는 음성 인식 Activity 를 시작한다.
     * <p/>
     * 음성 인식 결과를 해석하려면, {@link SpeechInputHelper#onActivityResult(int, int, Intent)}를 호출하면 된다.
     *
     * @param activity {@link Activity#onActivityResult(int, int, Intent)} 를 통해 결과를 얻을 Activity
     */
    public void startActivityForResult(Activity activity) {
        activity.startActivityForResult(speechIntent, ACTIVITY_REQUEST_CODE);
    }


    /**
     * {@link #startActivityForResult(Activity)} 로 부터 시작된 음성 입력 Activity 가 종료 된 후,
     * {@link Activity#onActivityResult(int, int, Intent)}를 통해 전달되는 결과에서 음성인식 데이터를 가져온다.
     *
     * @return 음성인식 결과 데이터들이 담겨져 있는 객체
     */
    public static SpeechResult onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ACTIVITY_REQUEST_CODE) {
            Bundle resultsBundle = data.getExtras();

            ArrayList<String> results = getRecognitionResult(resultsBundle);
            Uri audioUri = data.getData();

            return new SpeechResult(results.get(0), audioUri);
        }

        return null;
    }


    // ******* RecognitionListener

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(TAG, "=onReadyForSpeech=");

        isInRecognizing = true;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "=onBeginningOfSpeech=");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //LogShard.i(TAG, "=onRmsChanged : " + rmsdB + "=");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        //LogShard.i(TAG, "=onBufferReceived : " + Arrays.toString(buffer) + "=");
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "=onEndOfSpeech=");
    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "=onError : " + error + "=");

        if (listener != null)
            listener.onError(error);
        isInRecognizing = false;

        if (isContinueRecognizing)
            delayedStartRecognition();
    }

    @Override
    public void onResults(Bundle results) {
        deliverSttResult(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        deliverSttResult(partialResults);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(TAG, "=onEvent : " + eventType + " , extra : " + params + "=");
    }


    /**
     * 결과를 전달한다.
     */
    private void deliverSttResult(Bundle results) {
        if (listener != null)
            listener.onResult(new SpeechResult(getRecognitionResult(results).get(0),null));

        isInRecognizing = false;

        if (isContinueRecognizing)
            delayedStartRecognition();
    }

    /**
     * 일정시간 대기 후, 음성인식을 시작한다.
     */
    public void delayedStartRecognition() {
        if (mHandler.hasMessages(HANDLER_MSG_START_LISTENING)) {
            mHandler.removeMessages(HANDLER_MSG_START_LISTENING);
        }
        mHandler.sendEmptyMessageDelayed(HANDLER_MSG_START_LISTENING, delay);
    }

    /**
     * 지연된 음성인식 처리를 위한 Handler
     */
    private static class SpeechHandler extends Handler {
        private WeakReference<SpeechInputHelper> mRef;

        public SpeechHandler(SpeechInputHelper speechInputHelper) {
            super(Looper.getMainLooper());
            mRef = new WeakReference<SpeechInputHelper>(speechInputHelper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SpeechInputHelper helper = mRef.get();
            if (helper == null)
                return;

            switch (msg.what) {
                case HANDLER_MSG_START_LISTENING:
                    if (helper.isInRecognizing) {
                        helper.delayedStartRecognition();
                    } else {
                        helper.startListening();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * {@link RecognitionListener}에서 얻어진 {@link Bundle}에서 음성인식 결과 리스트를 가져온다.
     */
    static ArrayList<String> getRecognitionResult(Bundle results) {
        return results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    }

    /**
     * {@link RecognitionListener}에서 얻어진 {@link Bundle}에서 Confidence 점수 배열을 가져온다.
     */
    static float[] getConfidenceResult(Bundle results) {
        return results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
    }



}
