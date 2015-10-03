package kr.poturns.virtualpalace;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;

import java.io.InputStream;

import kr.poturns.virtualpalace.inputmodule.speech.SpeechInputHelper;
import kr.poturns.virtualpalace.inputmodule.speech.SpeechResult;

/**
 * Created by Myungjin Kim on 2015-07-30.
 */
public abstract class VoiceActivity extends Activity {

    private SpeechInputHelper mSpeechInputHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSpeechInputHelper = new SpeechInputHelper(this);

    }

    private void startVoiceRecognition() {
        mSpeechInputHelper.startActivityForResult(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SpeechResult speechResult = SpeechInputHelper.onActivityResult(requestCode, resultCode, data);

        if (speechResult != null) {


            ContentResolver contentResolver = getContentResolver();
            try {
                InputStream filestream = contentResolver.openInputStream(speechResult.audioUri);
                // read audio
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }


}
