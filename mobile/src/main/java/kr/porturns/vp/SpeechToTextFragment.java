package kr.porturns.vp;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class SpeechToTextFragment extends Fragment implements RecognitionListener {

    private TextView textView;
    private Button speechButton;
    private StringBuilder sb = new StringBuilder();
    private boolean isInVoice = false;

    private SpeechRecognizer mRecognizer;
    private Intent speechIntent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initSTT();

        final View view = inflater.inflate(R.layout.fragment_stt, container, false);
        textView = (TextView) view.findViewById(android.R.id.text1);
        speechButton = (Button) view.findViewById(R.id.speech_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInVoice)
                    mRecognizer.stopListening();
                else
                    mRecognizer.startListening(speechIntent);
            }
        });

        return view;
    }


    private void initSTT() {
        //음성인식 intent생성
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //데이터 설정
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        //음성인식 언어 설정
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        //음성인식 객체
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        //음성인식 리스너 등록
        mRecognizer.setRecognitionListener(this);
    }


    void printText(String msg) {
        textView.setText(sb.append(msg).toString());
    }

    private ArrayList<String> getRecognitionResult(Bundle result) {
        return result.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    }

    private float[] getConfidenceResult(Bundle result) {
        return result.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
    }

    //음성 인식 준비가 되었으면
    @Override
    public void onReadyForSpeech(Bundle params) {
        printText("onReadyForSpeech\n=============\n");

        isInVoice = true;

        speechButton.setText("VOICE");
    }

    //입력이 시작되면
    @Override
    public void onBeginningOfSpeech() {
        printText("onBeginningOfSpeech\n=============\n");
    }

    //음성 입력이 끝났으면
    @Override
    public void onEndOfSpeech() {
        printText("onEndOfSpeech\n=============\n\n");

        isInVoice = false;

        speechButton.setText("START");
    }

    //입력 소리 변경 시
    @Override
    public void onRmsChanged(float rmsdB) {

    }

    //더 많은 소리를 받을 때
    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    //에러가 발생하면
    @Override
    public void onError(int error) {
        printText("onError : " + error + "\n=============\n\n");

        isInVoice = false;

        speechButton.setText("START");
    }

    //음성 인식 결과 받음
    @Override
    public void onResults(Bundle results) {
        ArrayList<String> recognitionResults = getRecognitionResult(results);

        printText("result : \n" + recognitionResults.toString() + "\n");
        printText("confidences : " + Arrays.toString(getConfidenceResult(results)) + "\n\n");
    }

    //인식 결과의 일부가 유효할 때
    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> recognitionResults = getRecognitionResult(partialResults);

        printText("partial result : \n" + recognitionResults.toString() + "\n");
        printText("confidences : " + Arrays.toString(getConfidenceResult(partialResults)) + "\n\n");
    }

    //미래의 이벤트를 추가하기 위해 미리 예약되어진 함수
    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
