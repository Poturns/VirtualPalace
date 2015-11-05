package kr.poturns.virtualpalace.inputmodule.speech;


import android.net.Uri;

import java.util.List;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * 음성인식 결과 데이터를 지니는 클래스
 */
public class SpeechResult {
    /**
     * 음성인식 결과
     */
    public final List<String> result;
    /**
     * 음성 데이터
     */
    public final Uri audioUri;

    SpeechResult(List<String> result, Uri audioUri) {
        this.result = result;
        this.audioUri = audioUri;
    }

}
