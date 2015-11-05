package kr.poturns.virtualpalace.inputmodule.speech;

import android.content.Context;

import kr.poturns.virtualpalace.input.GlobalApplication;
import kr.poturns.virtualpalace.input.IProcessorCommands;
import kr.poturns.virtualpalace.input.OperationInputConnector;
import kr.poturns.virtualpalace.input.OperationInputDetector;

/**
 * Created by YeonhoKim on 2015-10-03.
 *
 * 음성인식 모듈과 연동되어 음성인식과 관련한 요청을 전송할 수 있는 OperationInputConnector
 */
public class SpeechInputConnector extends OperationInputConnector implements SpeechController.OnSpeechDataListener {

    public static final int KEY_SWITCH_MODE = 0x3;
    public static final int KEY_ACTIVE_RECOGNIZE = 0x4;

    public SpeechInputConnector(Context context) {
        super(context, IProcessorCommands.TYPE_INPUT_SUPPORT_VOICE);
    }

    @Override
    public void setRegisteredDetector(OperationInputDetector<?> detector) {
        if (detector == null)
            mRegisteredDetector = null;

        if (detector instanceof SpeechInputDetector) {
            mRegisteredDetector = detector;
            ((SpeechInputDetector) mRegisteredDetector).setSpeechListener(this);
        }
    }

    @Override
    public void configureFromController(GlobalApplication app, int key, int value) {
        if (app == null)
            return;

        switch (key) {
            case KEY_SWITCH_MODE:
                switch (value) {
                    case SpeechController.MODE_COMMAND:
                        ((SpeechInputDetector) mRegisteredDetector).setRecognitionMode(SpeechController.MODE_COMMAND);
                        break;

                    case SpeechController.MODE_TEXT:
                        ((SpeechInputDetector) mRegisteredDetector).setRecognitionMode(SpeechController.MODE_TEXT);
                        break;
                }
                break;

            case KEY_ACTIVE_RECOGNIZE:
                switch (value){
                    case 1:
                        ((SpeechInputDetector) mRegisteredDetector).startSpeechListening();
                        break;

                    case 0:
                        ((SpeechInputDetector) mRegisteredDetector).stopSpeechListening();
                        break;
                }
                break;


            default:
                super.configureFromController(app, key, value);
        }

    }

    @Override
    public void onResult(SpeechResult speechResult, int mode) {
        transferTextData(speechResult.result.get(0), mode);
    }

    @Override
    public void onError(int cause, int mode) {

        transferTextData(null, mode);
    }
}
