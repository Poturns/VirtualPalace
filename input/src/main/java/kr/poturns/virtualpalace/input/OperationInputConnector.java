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

    public static final int VALUE_TRUE = 1;
    public static final int VALUE_FALSE = 0;


    private final Context mContextF;
    /**
     * CONTROLLER 의 핸들러
     */
    private final Handler mControlHandlerF;
    /**
     * CONNECTOR 명칭
     */
    private final String mName;



    // * * * F I E L D S * * * //
    /**
     * 활성화 플래그 : CONTROLLER 가 제어.
     */
    private boolean isEnabled = false;



    // * * * C O N S T R U C T O R S * * * //
    public OperationInputConnector(Context context, String name) {
        mContextF = (context == null)? null : context.getApplicationContext();
        mName = name;

        GlobalApplication app = (mContextF instanceof GlobalApplication)? (GlobalApplication) mContextF : null;
        if (app != null) {
            app.setInputConnector(this, name);
        }

        mControlHandlerF = (app == null)? null : app.getControlHandler();
    }



    // * * * M E T H O D S * * * //
    /**
     * 단일 명령 메시지를 전송한다.
     *
     * @param inputRst
     * @return
     */
    boolean transferDataset(int[] inputRst) {
        if (mControlHandlerF == null || !isEnabled)
            return false;

        Message.obtain(mControlHandlerF, IControllerCommands.INPUT_SINGLE_COMMAND, inputRst).sendToTarget();
        return true;
    }

    /**
     * 다수의 명령 메시지를 전송한다.
     *
     * @param inputRstArray
     * @return
     */
    boolean transferDataset(int[][] inputRstArray) {
        if (mControlHandlerF == null || !isEnabled)
            return false;

        Message.obtain(mControlHandlerF, IControllerCommands.INPUT_MULTI_COMMANDS, inputRstArray).sendToTarget();
        return true;
    }

    /**
     * CONNECTOR 를 제어하기 위해 CONTROLLER 가 사용하는 메소드.
     * ( CONTROLLER 외 호출 금지 )
     *
     * @param app CONTROLLER에서 호출했음을 증명하기 위한 매개변수
     * @param key 설정 기능에 대한 키
     * @param value 설정 기능에 대한 값
     */
    public final void configureFromController(GlobalApplication app, int key, int value) {
        if (app == null)
            return;

        switch (key) {
            case KEY_ENABLE:
                isEnabled = (value == VALUE_TRUE)? true : false;
                break;
        }
    }



    // * * * G E T T E R S & S E T T E R S * * * //
    public String getName() {
        return mName;
    }
}
