package kr.poturns.virtualpalace.inputmodule.speech;

import android.content.Context;

import kr.poturns.virtualpalace.input.GlobalApplication;
import kr.poturns.virtualpalace.input.OperationInputConnector;
import kr.poturns.virtualpalace.input.OperationInputDetector;

/**
 * Created by YeonhoKim on 2015-10-03.
 */
public class SpeechInputConnector extends OperationInputConnector {

    public static final int KEY_SWITCH_MODE = 0x3;

    public SpeechInputConnector(Context context, int supportType) {
        super(context, supportType);
    }

    @Override
    public void setRegisteredDetector(OperationInputDetector<?> detector) {
        if (detector == null)
            mRegisteredDetector = null;

        if (detector instanceof SpeechInputDetector)
            mRegisteredDetector = detector;
    }

    /**
     * CONNECTOR 를 제어하기 위해 CONTROLLER 가 사용하는 메소드.
     * ( CONTROLLER 외 호출 금지 )
     *
     * @param app   CONTROLLER에서 호출했음을 증명하기 위한 매개변수
     * @param key   설정 기능에 대한 키
     * @param value 설정 기능에 대한 값
     */
    @Override
    public void configureFromController(GlobalApplication app, int key, int value) {
        if (app == null)
            return;

        switch (key) {
            case KEY_SWITCH_MODE:
                switch (value) {
                    //TODO:
                    case 0:
                        //((SpeechInputDetector) mRegisteredDetector).startInputDetecting();
                        break;

                    case 1:
                        //((SpeechInputDetector) mRegisteredDetector).stopInputDetecting();
                        break;
                }
                break;

            default:
                super.configureFromController(app, key, value);
        }

    }
}
