package kr.poturns.virtualpalace.controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
                    for (int[] command : optimize(cmds))
                       doPackOnScanning(command);
                    break;

                default:
                    return;
            }
        }

        /**
         * INPUT 명령 최적화
         * @param commands
         * @return
         */
        private int[][] optimize(int[][] commands) {
            // TODO :
            return commands;
        }

        /**
         *
         * @param command
         */
        private void doPackOnScanning(int[] command) {
            // TODO : 작업.
            switch (command[0]) {
                case GO:
                case FOCUS:
                case TURN:

                    break;
            }

            synchronized (inputLock) {
                try {
                    singleMessage.put("", "");

                } catch (JSONException e) {
                    Log.e("", "");
                }
            }
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
