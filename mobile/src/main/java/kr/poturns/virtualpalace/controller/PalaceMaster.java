package kr.poturns.virtualpalace.controller;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import org.json.JSONException;
import org.json.JSONObject;

import kr.poturns.virtualpalace.input.IControllerCommands;
import kr.poturns.virtualpalace.input.IOperationInputFilter;

/**
 * <b> EXTERNAL CONTROLLER : 컨트롤러의 중개 기능을 다룬다 </b>
 * <p>
 *
 * </p>
 *
 * @author Yeonho.Kim
 */
public class PalaceMaster extends PalaceCore {

    // * * * S I N G L E T O N * * * //
    private static PalaceMaster sInstance;

    public static PalaceMaster getInstance(PalaceApplication app) {
        if (sInstance == null)
            sInstance = new PalaceMaster(app);
        return sInstance;
    }


    // * * * C O N S T A N T S * * * //
    private final InputHandler mInputHandlerF;
    private final RequestHandler mRequestHandlerF;
    //private final GoogleServiceAssistant mGoogleServiceAssistantF;


    // * * * F I E L D S * * * //



    // * * * C O N S T R U C T O R S * * * //
    private PalaceMaster(PalaceApplication app) {
        super(app);

        mInputHandlerF = new InputHandler();
        mRequestHandlerF = new RequestHandler();

        //mGoogleServiceAssistantF = new GoogleServiceAssistant(app, mLocalArchiveF.getSystemStringValue(LocalArchive.ISystem.ACCOUNT));
    }


    // * * * I N N E R  C L A S S E S * * * //
    /**
     * <b><INPUT 명령을 처리하는 핸들러.</b>
     * <p>
     * INPUT 명령이 다양한 출처 및 스레드로부터 발생하기 때문에,
     * {@link Handler}의 Message-Queue 를 이용한다.
     * </p>
     *
     * @author Yeonho.Kim
     */
    private final class InputHandler extends Handler implements IControllerCommands, IOperationInputFilter.Operation {

        private Object inputLock = new Object();
        private JSONObject singleMessage = new JSONObject();

        private InputHandler() {
            init();
        }

        private void init() {
            singleMessage = new JSONObject();
        }

        @Override
        public void handleMessage(Message msg) {
            int from = msg.arg1;

            switch(msg.what) {
                case INPUT_SYNC_COMMAND:
                    send();
                    sendEmptyMessageDelayed(INPUT_SYNC_COMMAND, 400);   // ms : 2.5 FPS
                    break;

                case INPUT_SINGLE_COMMAND:
                    int[] cmd = (int[]) msg.obj;
                    doPackOnScanning(cmd);
                    break;

                case INPUT_MULTI_COMMANDS:
                    int[][] cmds = (int[][]) msg.obj;
                    for (int[] command : cmds)
                       doPackOnScanning(command);
                    break;

                default:
                    return;
            }
        }

        /**
         *
         * @param command
         */
        private void doPackOnScanning(int[] command) {
            int cmd = command[0];
            int value = 0;

            String cmdStr = String.valueOf(cmd);
            switch (cmd) {
                // 하드웨어 버튼은 안드로이드에서 처리하도록 한다.
                //case KEY_OK:      // SELECT 로 처리
                //case KEY_BACK:    // CANCEL 로 처리
                case KEY_MENU:
                    new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
                    break;
                case KEY_HOME:
                    new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
                    break;
                case KEY_VOLUME_UP:
                    new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP);
                    break;
                case KEY_VOLUME_DOWN:
                    new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN);
                    break;

                // 단위 명령 : 명령 호출 자체에 의미.
                case SELECT:
                case CANCEL:
                case DEEP:
                case CONFIG:
                case TERMINATE:
                    synchronized (inputLock) {
                        try {
                            try {
                                value = singleMessage.getInt(cmdStr) + 1;
                            } catch (JSONException e) {
                                value = 1;
                            }

                            singleMessage.put(cmdStr, value);

                        } catch (JSONException e) { ; }
                    } break;

                // 복합 명령 : 명령 호출 외 부가 정보들이 포함됨.
                case GO:
                case TURN:
                case FOCUS:
                case ZOOM:
                    synchronized (inputLock) {
                        try {
                            try {
                                value = singleMessage.getInt(cmdStr);

                                // 명령이 이미 존재할 경우, 기존 값에 합산한다.
                                int old_d = value / IOperationInputFilter.Direction.SEPARATION;
                                int old_a = value % IOperationInputFilter.Direction.SEPARATION;
                                int curr_d = command[1];
                                int curr_a = command[2];

                                // 각 축 별로 값을 분해하여, 합산한다.
                                int axisX = filterD(old_d) * old_a + filterD(curr_d) * curr_a;
                                old_d /= 10;
                                curr_d /= 10;
                                int axisY = filterD(old_d) * old_a + filterD(curr_d) * curr_a;
                                old_d /= 10;
                                curr_d /= 10;
                                int axisZ = filterD(old_d) * old_a + filterD(curr_d) * curr_a;

                                value = convertV(axisX, axisY, axisZ);

                            } catch (JSONException e) {
                                // 초기 값 설정
                                // VALUE = CODE * 1000 + AMOUNT
                                value = command[1] * IOperationInputFilter.Direction.SEPARATION + command[2];
                            }

                            singleMessage.put(cmdStr, value);

                        } catch (JSONException e) { ; }
                    } break;

                // 단일 명령 : 단위 시간내 여러번 호출되더라도, 한번만 적용된다.
                case SEARCH:
                case SWITCH_MODE:
                    // 어떤 모드로 전환할 것인가. (DEFAULT: 지정된 다음 순서 = 0)
                    synchronized (inputLock) {
                        try {
                            singleMessage.put(cmdStr, command[2]);

                        } catch (JSONException e) { ; }
                    }
                    break;
            }
        }

        private int filterD(int direction) {
            int mod = direction % 10;
            return (mod == 0)? 0 : mod - 5;
        }

        /**
         * 축별 벡터 수치 값을 방량과 크기가 적용된 값으로 변환한다.
         * ( 값 = 방향값 * {#IOperationInputFilter.Direction.SEPARATION} + 크기값 )
         *
         * @param axisX X축 벡터수치
         * @param axisY Y축 벡터수치
         * @param axisZ Z축 벡터수치
         * @return
         */
        private int convertV(int axisX, int axisY, int axisZ) {
            int v_x = (axisX == 0)? Integer.MAX_VALUE : Math.abs(axisX);
            int v_y = (axisY == 0)? Integer.MAX_VALUE : Math.abs(axisY);
            int v_z = (axisZ == 0)? Integer.MAX_VALUE : Math.abs(axisZ);

            int unit = Math.min(v_x, Math.min(v_y, v_z));

            float f_x = axisX / (float) unit;
            float f_y = axisY / (float) unit;
            float f_z = axisZ / (float) unit;

            int x = (axisX == 0)? 0 : Math.min(9, Math.max(1,
                    (int)(f_x + (f_x > 0? 5.5 : 4.5))));
            int y = (axisY == 0)? 0 : Math.min(9, Math.max(1,
                    (int)(f_y + (f_y > 0? 5.5 : 4.5))));
            int z = (axisZ == 0)? 0 : Math.min(9, Math.max(1,
                    (int)(f_z + (f_z > 0? 5.5 : 4.5))));

            int operation = x + (z == 5? 0 : z * 100);
            operation += ((y == 5 && operation < 100)? 0 : y * 10);
            return operation * IOperationInputFilter.Direction.SEPARATION + unit;
        }


        /**
         *
         */
        private void send() {
            if (singleMessage.length() == 0)
                return;

            synchronized (inputLock) {
                // TODO : Unity 어느 곳으로 보내는가!
                AndroidUnityBridge.getInstance(mAppF).sendSingleMessageToUnity(null, null, singleMessage.toString());
                init();
            }
        }
    }

    /**
     * <b><REQUEST 명령을 처리하는 핸들러.</b>
     * <p>
     * REQUEST 명령이 연쇄적인 콜백메소드 호출을 통해 이뤄지므로,
     * {@link Handler}의 Message-Queue 를 이용한다.
     * </p>
     *
     * @author Yeonho.Kim
     */
    private final class RequestHandler extends Handler implements IControllerCommands {

        // TODO : THREAD 관리 및 처리.

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_MESSAGE_FROM_UNITY:
                    process( (String) msg.obj );
                    break;

                case REQUEST_CALLBACK_FROM_UNITY:
                    Bundle bundle = (Bundle) msg.obj;
                    long id = bundle.getLong(AndroidUnityBridge.BUNDLE_KEY_ID);
                    String jsonMessage = bundle.getString(AndroidUnityBridge.BUNDLE_KEY_MESSAGE_JSON);

                    JSONObject result = process(jsonMessage);
                    AndroidUnityBridge.getInstance(mAppF).respondCallbackToUnity(id, result.toString());
                    break;

            }
        }

        private JSONObject process(String jsonMessage) {
            JSONObject jsonResult = new JSONObject();

            try {
                JSONObject message = new JSONObject(jsonMessage);
                // TODO : 작업.

            } catch (JSONException e){

            }

            return jsonResult;
        }
    }


    // * * * G E T T E R S & S E T T E R S * * * //
    public Handler getInputHandler() {

        return mInputHandlerF;
    }

    public Handler getRequestHandler() {

        return mRequestHandlerF;
    }

}
