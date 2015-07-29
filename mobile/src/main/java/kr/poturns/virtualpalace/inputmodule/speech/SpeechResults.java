package kr.poturns.virtualpalace.inputmodule.speech;


import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 음성인식 결과 데이터를 지니는 클래스
 */
public class SpeechResults {
    /**
     * 음성인식 결과
     */
    public final ArrayList<String> results;
    /**
     * 음성인식 결과의 정확도
     */
    public final float[] confidences;
    /**
     * 음성 데이터
     */
    public final Uri audioUri;

    SpeechResults(ArrayList<String> results, float[] confidences, Uri audioUri) {
        this.results = results;
        this.confidences = confidences;
        this.audioUri = audioUri;
    }

}
