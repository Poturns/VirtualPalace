package kr.poturns.virtualpalace.input;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * <b>검출된 INPUT 명령을 전송하는 CONNECTOR.</b>
 *
 * @author Yeonho.Kim
 */
public class OperationInputConnector {

    // * * * C O N S T A N T S * * * //
    /**
     * CONTROLLER 사용 KEY : 본 CONNECTOR 활성화 여부
     */
    public static final int KEY_ENABLE = 0x1;
    public static final int KEY_ACTIVATE = 0x2;

    public static final int VALUE_TRUE = 1;
    public static final int VALUE_FALSE = 0;


    private final Context mContextF;
    /**
     * CONTROLLER 의 핸들러
     */
    private final Handler mControllerInputHandlerF;
    /**
     * CONNECTOR 명칭
     */
    private final int mSupportTypeF;

    protected OperationInputDetector<?> mRegisteredDetector;


    // * * * F I E L D S * * * //
    /**
     * 사용 가능 플래그 : CONTROLLER 가 제어.
     * <p/>
     * - CONTROLLER 에서 해당 Connector 정보를 유지하고 있는지 여부를 나타낸다.
     * Enabled 플래그가 활성화 되어 있지 않을 경우, 데이터 전송 자체를 BLOCK 한다.
     */
    private boolean isEnabled = false;
    /**
     * 활성화 플래그 : CONTROLLER 가 제어.
     * <p/>
     * - CONTROLLER 에서 해당 Connector 정보를 유지하고는 있으나,
     * 일시적으로 명령을 받아들이는가 여부를 내부에서 판단한다.
     * Connector 에서는 활성화 플래그와 관계없이 데이터를 전송할 수 있다.
     */
    private boolean isActivated = false;


    // * * * C O N S T R U C T O R S * * * //
    public OperationInputConnector(Context context, int supportType) {
        mContextF = (context == null) ? null : context.getApplicationContext();
        mSupportTypeF = supportType;

        GlobalApplication app = (mContextF instanceof GlobalApplication) ? (GlobalApplication) mContextF : null;
        if (app != null) {
            app.setInputConnector(supportType, this);
        }

        mControllerInputHandlerF = (app == null) ? null : app.getInputHandler(supportType);
    }


    // * * * M E T H O D S * * * //

    /**
     * 단일 명령 메시지를 전송한다.
     *
     * @param inputRst 전송할 명령 메시지
     * @return 전송 결과
     */
    protected boolean transferDataset(int[] inputRst) {
        if (mControllerInputHandlerF == null || !isEnabled)
            return false;

        Message.obtain(mControllerInputHandlerF, IProcessorCommands.INPUT_SINGLE_COMMAND, mSupportTypeF, 0, inputRst).sendToTarget();
        return true;
    }

    /**
     * 다수의 명령 메시지를 전송한다.
     *
     * @param inputRstArray 전송할 명령 메시지 배열
     * @return 전송 결과
     */
    protected boolean transferDataset(int[][] inputRstArray) {
        if (mControllerInputHandlerF == null || !isEnabled)
            return false;

        Message.obtain(mControllerInputHandlerF, IProcessorCommands.INPUT_MULTI_COMMANDS, mSupportTypeF, 0, inputRstArray).sendToTarget();
        return true;
    }

    /**
     * 텍스트 결과를 전송한다.
     *
     * @param text 전송할 문자열
     * @param mode 음성 인식 모드
     * @return 전송 결과
     */
    protected boolean transferTextData(String text, int mode) {
        if (mControllerInputHandlerF == null || !isEnabled)
            return false;

        Message.obtain(mControllerInputHandlerF, IProcessorCommands.INPUT_TEXT_RESULT, mode, 0, text).sendToTarget();
        return true;
    }

    /**
     * CONNECTOR 를 제어하기 위해 CONTROLLER 가 사용하는 메소드.
     * ( CONTROLLER 외 호출 금지 )
     *
     * @param app   CONTROLLER에서 호출했음을 증명하기 위한 매개변수
     * @param key   설정 기능에 대한 키
     * @param value 설정 기능에 대한 값
     */
    public void configureFromController(GlobalApplication app, int key, int value) {
        if (app == null)
            return;

        switch (key) {
            case KEY_ENABLE:
                isEnabled = (value == VALUE_TRUE) ? true : false;
                break;

            case KEY_ACTIVATE:
                isActivated = (value == VALUE_TRUE) ? true : false;
                break;
        }
    }

    /**
     * Controller와의 연결을 해제한다.
     */
    public void disconnect() {
        if (mContextF instanceof GlobalApplication) {
            ((GlobalApplication) mContextF).setInputConnector(mSupportTypeF, null);
        }
    }

    /**
     * 이 Connector를 통해 메시지를 전송할 {@link OperationInputDetector}를 등록한다.
     * @param detector OperationInputDetector
     */
    public void setRegisteredDetector(OperationInputDetector<?> detector) {
        mRegisteredDetector = detector;
    }


    // * * * G E T T E R S & S E T T E R S * * * //
    public int getSupportType() {
        return mSupportTypeF;
    }
}
