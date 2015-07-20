package kr.poturns.vp;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import kr.poturns.util.SpeechToTextHelper;

public class SpeechToTextFragment extends Fragment implements SpeechToTextHelper.STTListener {

    private TextView textView;
    private Button speechButton;
    private StringBuilder sb = new StringBuilder();
    private SpeechToTextHelper speechToTextHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        speechToTextHelper = new SpeechToTextHelper(getActivity(), this);

        final View view = inflater.inflate(R.layout.fragment_stt, container, false);
        textView = (TextView) view.findViewById(android.R.id.text1);
        speechButton = (Button) view.findViewById(R.id.speech_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speechToTextHelper.isInVoiceRecognition())
                    speechToTextHelper.pause();
                else
                    speechToTextHelper.start();
            }
        });

        View playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1010 && resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            onResults(false, bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS), bundle.getFloatArray(RecognizerIntent.EXTRA_CONFIDENCE_SCORES));
            Uri audioUri = data.getData();

            Log.d("STT", "" + audioUri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        speechToTextHelper.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        speechToTextHelper.destroy();
    }

    void printText(String msg) {
        textView.setText(sb.append(msg).toString());
    }

    //음성 인식 준비가 되었으면
    @Override
    public void onReadyForSpeech() {
        printText("onReadyForSpeech\n=============\n");

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
        speechButton.setText("START");
    }

    //더 많은 소리를 받을 때
    @Override
    public void onBufferReceived(byte[] buffer) {
        printText("=============\nonBufferReceived\n=============\n");

    }

    @Override
    public void onResults(boolean isPartial, ArrayList<String> resultList, float[] confidences) {
        printText((isPartial ? "partial result" : "result") + " : \n" + resultList.toString() + "\n");
        printText("confidences : " + Arrays.toString(confidences) + "\n\n");
    }

    @Override
    public void onResults(boolean isPartial, String resultJSON) {

    }

    //에러가 발생하면
    @Override
    public void onError(int error) {
        printText("onError : " + error + "\n=============\n\n");
        speechButton.setText("START");
    }

}
