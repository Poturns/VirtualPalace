package kr.poturns.virtualpalace.inputmodule.speech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 음성인식을 도와주는 Helper 클래스
 */
public class SpeechInputHelper implements RecognitionListener {
    private static final String TAG = "SpeechInputHelper";
    public static final int ACTIVITY_REQUEST_CODE = 9567;

    private SpeechRecognizer mRecognizer;
    private Intent speechIntent;
    private OnSpeechDataListener listener;

    private final Context mContext;

    public SpeechInputHelper(Context context) {
        this.mContext = context;
        initSTT();
    }

    public static interface OnSpeechDataListener {
        void onResult(ArrayList<String> results);
    }

    public void setSpeechListener(OnSpeechDataListener listener) {
        this.listener = listener;
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
     * 음성인식 결과는 {@link OnSpeechDataListener}를 통해 전달된다.
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
     * {@link #startActivityForResult(Activity)} 로 부터 시작된 음성 입력 Activity 가 종료 된 후, 음성입력 결과를 해석한다.
     *
     * @return 음성인식 결과 데이터들이 담겨져 있는 객체
     */
    public static SpeechResults onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ACTIVITY_REQUEST_CODE) {
            Bundle resultsBundle = data.getExtras();

            ArrayList<String> results = getRecognitionResult(resultsBundle);
            float[] confidences = getConfidenceResult(resultsBundle);
            Uri audioUri = data.getData();

            return new SpeechResults(results, confidences, audioUri);
        }

        return null;
    }


    /**
     * 결과를 전달한다.
     */
    private void deliverSttResult(Bundle results) {
        if (listener != null)
            listener.onResult(getRecognitionResult(results));
    }


    // ******* RecognitionListener

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(TAG, "=onReadyForSpeech=");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "=onBeginningOfSpeech=");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.i(TAG, "=onRmsChanged : " + rmsdB + "=");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        //Log.i(TAG, "=onBufferReceived : " + Arrays.toString(buffer) + "=");
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "=onEndOfSpeech=");
    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "=onError : " + error + "=");
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
