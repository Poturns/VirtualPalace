package kr.poturns.virtualpalace.inputmodule.speech;

import android.speech.SpeechRecognizer;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Myungjin Kim on 2015-10-03.
 * <p/>
 * 음성인식 모듈을 제어하는 인터페이스
 */
public interface SpeechController {

    /**
     * 음성인식 명령어 모드
     */
    int MODE_COMMAND = 0;
    /**
     * 음성인식 메모 모드
     */
    int MODE_TEXT = 1;


    /**
     * 음성인식 모드
     */
    @IntDef(value = {MODE_COMMAND, MODE_TEXT})
    @Retention(RetentionPolicy.SOURCE)
    @interface RecognitionMode {
    }

    /**
     * 음성인식 모드를 설정한다.
     */
    void setRecognitionMode(@RecognitionMode int mode);

    /**
     * 음성인식 수행이 끝나도 계속 음성인식을 수행하는지 여부를 설정한다.
     *
     * @param continueRecognizing True : 음성인식을 계속 수행
     */
    void setContinueRecognizing(boolean continueRecognizing);


    /**
     * 음성 인식을 실행한다.
     */
    void startSpeechListening();

    /**
     * 음성 인식을 중단한다.
     */
    void stopSpeechListening();

    /**
     * 음성 인식을 취소한다.
     */
    void cancelSpeechListening();

    /**
     * 음성입력을 중단하고, 관련된 자원을 모두 회수한다.
     */
    void destroy();


    /**
     * 음성인식 결과를 전달받을 리스너를 등록한다
     * <p/>
     * RecognitionMode에 따라 음성인식 결과가 전달이 되지 않을 수 있다.
     *
     * @param speechListener 음성인식 결과를 전달받을 리스너
     */
    void setSpeechListener(OnSpeechDataListener speechListener);

    /**
     * 음성인식 결과를 전달받는 리스너
     */
    interface OnSpeechDataListener {
        /**
         * 음성인식 결과를 전달받는다.
         *
         * @param speechResult 음성인식 결과
         */
        void onResult(SpeechResult speechResult);

        /**
         * 음성인식이 실패하였을 때, 호출된다
         *
         * @param cause 실패 이유
         * @see SpeechRecognizer
         */
        void onError(int cause);
    }

}
