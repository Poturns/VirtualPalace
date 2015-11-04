package kr.poturns.virtualpalace.controller;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import kr.poturns.virtualpalace.augmented.AugmentedOutput;
import kr.poturns.virtualpalace.controller.data.IProtocolKeywords;
import kr.poturns.virtualpalace.controller.data.ITable;
import kr.poturns.virtualpalace.controller.data.SceneLifeCycle;
import kr.poturns.virtualpalace.controller.data.VirtualTable;
import kr.poturns.virtualpalace.input.IProcessorCommands;
import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.util.ThreadUtils;

/**
 * <b> EXTERNAL CONTROLLER : 컨트롤러의 중개 기능을 다룬다 </b>
 * <p>
 *     프로토콜 및 통신 작업에 대한 Router 역할을 수행한다.
 *     다른 모듈들과 별도로 프로세싱하기 위하여 별도 Thread 에서 Handler 를 운영한다.
 * </p>
 *
 *
 * @author Yeonho.Kim
 */
public class PalaceMaster extends PalaceEngine {

    // * * * S I N G L E T O N * * * //
    private static PalaceMaster sInstance;

    public static PalaceMaster getInstance(PalaceApplication app) {
        if (sInstance == null) {
            sInstance = new PalaceMaster(app);
        }
        return sInstance;
    }


    // * * * C O N S T A N T S * * * //
    private final HandlerThread ControllerThread;
    private final EventProcessor EventProcessHandler;
    private final InputProcessor InputProcessHandler;
    private final RequestProcessor RequestProcessHandler;


    // * * * F I E L D S * * * //
    private long mTextResultRequestId = -1;

    // * * * C O N S T R U C T O R S * * * //
    private PalaceMaster(PalaceApplication app) {
        super(app);

        ControllerThread = new HandlerThread(getClass().getName());
        ControllerThread.start();

        EventProcessHandler = new EventProcessor(ControllerThread.getLooper());
        InputProcessHandler = new InputProcessor(ControllerThread.getLooper());
        RequestProcessHandler = new RequestProcessor(ControllerThread.getLooper());
    }


    // * * * M E T H O D S * * * //
    @Override
    protected void destroy() {
        ControllerThread.quit();

        // 순차적 destroy
        super.destroy();

        // Singleton Release
        sInstance = null;
    }

    @Override
    protected void dispatchEvent(String eventName, Object contents) {
        // TODO : Accumulate
        JSONObject event = new JSONObject();
        try {
            event.put(eventName, contents);

            Message.obtain(EventProcessHandler, 0, event).sendToTarget();

        } catch (JSONException e) { ; }
    }

    /**
     * AR에서 감지한 데이터를 Unity 에서 출력할 수 있도록 전송한다.
     *
     * @param list
     */
    public void drawAugmentedItems(List<AugmentedOutput> list) {
        Pair<String, SceneLifeCycle> current = getCurrentLifeCycle();
        if (!SceneLifeCycle.AR.equalsIgnoreCase(current.first) ||
                SceneLifeCycle.onLoaded != current.second) {
            // AR Scene이 준비되지 않았을 경우엔 Item Drawing 요청이 전달되지 않는다.
            return;
        }

        JSONObject message = new JSONObject();
        try {
            JSONArray array = new JSONArray();

            for (AugmentedOutput item : list) {
                try {
                    JSONObject obj = new JSONObject();

                    obj.put(VirtualTable.RES_ID.toString(), item.resID);
                    obj.put(IProcessorCommands.JsonKey.SCREEN_X, item.screenX);
                    obj.put(IProcessorCommands.JsonKey.SCREEN_Y, item.screenY);

                    array.put(obj);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            message.put(IProcessorCommands.JsonKey.DRAWING_AR, array);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        AndroidUnityBridge.getInstance(App).sendSingleMessageToUnity(message.toString());
    }



    // * * * I N N E R  C L A S S E S * * * //
    /**
     * <b><INPUT 명령을 처리하는 핸들러.</b>
     * <p>
     * INPUT 명령이 다양한 출처 및 스레드로부터 발생하기 때문에,{@link Handler}의 Message-Queue 를 이용한다.
     * INPUT 처리는 PalaceMaster 스레드가 자체적으로 처리한다.
     * </p>
     *
     * @author Yeonho.Kim
     */
    private final class InputProcessor extends Handler implements IProtocolKeywords.Input, IProcessorCommands, IOperationInputFilter.Operation {
        private static final long INTERVAL = 400;   // ms : 2.5 FPS

        private final Object inputLock = new Object();
        private JSONObject singleMessage;
        private long mExpectedFlushTime;

        private InputProcessor(Looper looper) {
            super(looper);
            init();
        }

        private void init() {
            singleMessage = new JSONObject();
        }

        @Override
        public void handleMessage(Message msg) {
            // 활성화되지 않은 InputConnector 로부터 전달된 메시지는 처리하지 않는다.
            int from = msg.arg1;
            if (from < IControllerCommands.TYPE_INPUT_SUPPORT_MAJOR_LIMIT && (mActivatedConnectorSupportFlag & from) != from)
                return;

            switch(msg.what) {
                case INPUT_SYNC_COMMAND:
                    send();
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

                case INPUT_TEXT_RESULT:
                    String result;
                    try {
                        String text = (String) msg.obj;
                        if (text == null)
                            throw new Exception();

                        JSONObject obj = new JSONObject();
                        obj.put(JsonKey.RECOGNIZE_TEXT_RESULT,  text);
                        obj.put(JsonKey.RESULT, "success");
                        result = obj.toString();

                    } catch (Exception e) {
                        result = "{'result' : 'error'}";
                    }

                    final String sendResult = result;
                    ThreadUtils.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUnityBridge.getInstance(App).respondCallbackToUnity(mTextResultRequestId, sendResult);
                            mTextResultRequestId = -1;
                        }
                    });
                    break;
            }
        }

        /**
         *
         * @param command
         */
        private void doPackOnScanning(int[] command) {
            long current = System.currentTimeMillis();

            // JsonMessage 객체를 초기화한 후 첫 입력 값이거나,
            // 예상 전송 시간을 넘길 때까지 명령이 생략되었을 경우
            // 메시지 전송을 예약한다.
            if (singleMessage.length() == 0 || current > mExpectedFlushTime) {
                mExpectedFlushTime = current + INTERVAL;
                sendEmptyMessageDelayed(INPUT_SYNC_COMMAND, INTERVAL);
            }

            int cmd = command[0];
            int value;

            String cmdStr = String.valueOf(cmd);
            switch (cmd) {
                case KEY_COMMAND_DRAW:
                    try {
                        JSONObject obj = new JSONObject();


                        singleMessage.put(cmdStr, obj);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    } break;

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

                                if (old_d == curr_d && old_a % 10 > 0) {
                                    // VALUE 에 1의 자리수가 존재할 경우,
                                    // SEPARATION 미만의 수는 해당 명령이 발생한 횟수를 의미한다. (10의 자리수부터 판단)
                                    value = curr_d * IOperationInputFilter.Direction.SEPARATION+ (old_a + curr_a * 10);

                                } else {
                                    // VALUE 에 1의 자리수가 존재하지 않을 경우,
                                    // 각 축 별로 값을 분해하여, 합산한다.
                                    // 이때, 10의 자리수부터 차례대로 각 축별 이벤트 발생 횟수를 의미한다.
                                    int axisX = filterD(old_d) * old_a + filterD(curr_d) * curr_a;
                                    old_d /= 10;
                                    curr_d /= 10;
                                    int axisY = filterD(old_d) * old_a + filterD(curr_d) * curr_a;
                                    old_d /= 10;
                                    curr_d /= 10;
                                    int axisZ = filterD(old_d) * old_a + filterD(curr_d) * curr_a;

                                    value = convertV(axisX, axisY, axisZ);
                                }


                            } catch (JSONException e) {
                                // 초기 값 설정
                                // VALUE = CODE * 100000 + AMOUNT * 10 + 1
                                value = command[1] * IOperationInputFilter.Direction.SEPARATION + command[2] * 10 + 1;
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
            int levelX = 1, levelY = 1, levelZ = 1;

            // 각 축별 횟수는 각 자리수마다 할당되므로, 10 미만의 수이어야 한다.
            // Amount 가 10을 넘어갈 경우, Direction Level 을 상승시켜 횟수를 감소시킨다.
            int absX = Math.abs(axisX);
            while (absX / levelX >= 10)
                levelX++;
            levelX *= (axisX > 0? 1 : -1);

            int absY = Math.abs(axisY);
            while (absY/ levelY >= 10)
                levelY++;
            levelY *= (axisY > 0? 1 : -1);

            int absZ = Math.abs(axisZ);
            while (absZ / levelZ >= 10)
                levelZ++;
            levelZ *= (axisZ > 0? 1 : -1);


            // Direction Level 을 적용한다.
            int x = (axisX == 0)? 0 : Math.min(9, Math.max(1,levelX + 5));
            int y = (axisY == 0)? 0 : Math.min(9, Math.max(1,levelY + 5));
            int z = (axisZ == 0)? 0 : Math.min(9, Math.max(1,levelZ + 5));

            // 차원 수에 맞는 Direction 값을 만든다.
            int direction = x + (z == 5? 0 : z * 100);
            direction += ((y == 5 && direction < 100)? 0 : y * 10);

            // 각 축별 Amount 를 계산하기 때문에 1의 자리수는 사용하지 않고,
            // 10의 자리수부터 차례대로 축별 Amount 를 기록한다.
            int amount = ((axisX / levelX) * 10) + ((axisY / levelY) * 100) + ((axisZ / levelZ) * 1000);

            return direction * IOperationInputFilter.Direction.SEPARATION + amount;
        }


        /**
         *
         */
        private void send() {
            if (singleMessage.length() == 0)
                return;

            Log.d("PalaceMast_Input","Input Message : " + singleMessage.length() + " transfered. " + singleMessage.toString());

            // Input은 순차적으로 전송
            ThreadUtils.SERIAL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (inputLock) {
                        AndroidUnityBridge.getInstance(App).sendInputMessageToUnity(singleMessage.toString());

                        // Send 후 JsonMessage 초기화.
                        init();
                    }
                }
            });

        }
    }

    /**
     * <b><REQUEST 명령을 처리하는 핸들러.</b>
     * <p>
     * REQUEST 명령이 연쇄적인 콜백메소드 호출을 통해 이뤄지므로, {@link Handler}의 Message-Queue 를 이용한다.
     * </p>
     *
     * @author Yeonho.Kim
     */
    private final class RequestProcessor extends Handler implements IProcessorCommands, IProcessorCommands.JsonKey, IProtocolKeywords.Request {

        private RequestProcessor(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Runnable runnable = null;
            switch (msg.what) {
                case REQUEST_MESSAGE_FROM_ANDROID:
                case REQUEST_MESSAGE_FROM_UNITY: {
                    final String jsonMessage = (String) msg.obj;

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                process(jsonMessage);
                            } catch (Exception e) { }
                        }
                    };
                } break;

                case REQUEST_CALLBACK_FROM_UNITY: {
                    Bundle bundle = (Bundle) msg.obj;
                    final long id = bundle.getLong(AndroidUnityBridge.BUNDLE_KEY_ID);
                    final String jsonMessage = bundle.getString(AndroidUnityBridge.BUNDLE_KEY_MESSAGE_JSON);

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            JSONObject result;
                            try {
                                result = process(jsonMessage);

                            } catch (WaitForCallbackException e1) {
                                mTextResultRequestId = id;
                                return;

                            } catch (Exception e2) {
                                result = new JSONObject();
                            }

                            AndroidUnityBridge.getInstance(App).respondCallbackToUnity(id, result.toString());
                        }
                    };
                } break;

                default:
                    return;
            }

            // Input이 아닌 기타 Message는 ThreadPool에서 병렬로 메시지를 전송한다.
            if(runnable != null)
                ThreadUtils.THREAD_POOL_EXECUTOR.execute(runnable);
        }

        /**
         * JSON 메시지를 분류하여, 요청한 기능을 수행한다.
         *
         * @param jsonMessage
         * @return
         */
        private JSONObject process(String jsonMessage) throws JSONException, WaitForCallbackException {
            JSONObject jsonResult = new JSONObject();
            Log.d("PalaceMaster_Request", "Request Message : " + jsonMessage);

            JSONObject message = new JSONObject(jsonMessage);
            Iterator<String> keys = message.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject eachResult = new JSONObject();
                boolean result = false;

                try {
                    if (COMMAND_LIFECYCLE.equalsIgnoreCase(key)) {
                        JSONObject obj = message.getJSONObject(key);
                        String scene = obj.keys().next();
                        onLifeCycle(scene, SceneLifeCycle.valueOf(obj.getString(scene)));

                    } else if (COMMAND_SWITCH_INPUTMODE.equalsIgnoreCase(key)) {
                        JSONArray array = message.getJSONArray(key);
                        for (int i=0; i<array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String inputName = obj.keys().next();
                            int inputType = Integer.parseInt(inputName);

                            result = obj.getBoolean(inputName)?
                                    activateInputConnector(inputType) :
                                    deactivateInputConnector(inputType);
                        }

                    } else if (COMMAND_USE_SPEECH.equalsIgnoreCase(key)) {
                        JSONObject obj = message.getJSONObject(key);
                        String mode = obj.getString(KEY_USE_SPEECH_MODE);
                        String action = obj.getString(KEY_USE_SPEECH_ACTION);

                        int supportType = IProcessorCommands.TYPE_INPUT_SUPPORT_VOICE;
                        result = requestTextResultByVoiceRecognition(supportType);

                        if (result)
                            throw new WaitForCallbackException();


                    } else {
                        // FIND TABLE
                        String table = null;
                        if (key.endsWith("_ar") || key.endsWith("_AR"))
                            table = ITable.TABLE_AUGMENTED;
                        else if (key.endsWith("_vr") || key.endsWith("_VR"))
                            table = ITable.TABLE_VIRTUAL;
                        else if (key.endsWith("_res") || key.endsWith("_RES"))
                            table = ITable.TABLE_RESOURCE;
                        else if (key.endsWith("_bookcase") || key.endsWith("_BOOKCASE"))
                            table = ITable.TABLE_VR_CONTAINER;
                        else
                            continue;

                        // DO SQL
                        String keyLowerCase = key.toLowerCase();
                        if (keyLowerCase.startsWith(COMMAND_DB_SELECT)) {
                            result = selectLocalData(message.getJSONObject(key), table, jsonResult);

                        } else if (keyLowerCase.startsWith(COMMAND_DB_INSERT)) {
                            result = insertNewLocalData(message.getJSONObject(key), table);

                        } else if (keyLowerCase.startsWith(COMMAND_DB_UPDATE)) {
                            result = updateLocalData(message.getJSONObject(key), table);

                        } else if (keyLowerCase.startsWith(COMMAND_DB_DELETE)) {
                            result = deleteLocalData(message.getJSONObject(key), table);
                        }
                    }

                    eachResult.put(RESULT, result ? "success" : "fail");

                } catch (JSONException e){
                    try {
                        eachResult.put(RESULT, "error");

                    } catch (JSONException e2) { }
                }

                jsonResult.put(key, eachResult);
            }

            return jsonResult;
        }
    }


    /**
     *
     */
    private final class EventProcessor extends Handler implements IProtocolKeywords.Event {

        private EventProcessor(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            JSONObject obj = new JSONObject();
            try {
                obj.put(IProtocolKeywords.CATEGORY_EVENT, (JSONObject) msg.obj);

                AndroidUnityBridge.getInstance(App).sendSingleMessageToUnity(obj.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    // 임시 TEST 코드
    public JSONObject testProcess(String json) throws Exception {
        return RequestProcessHandler.process(json);
    }

    /**
     * Callback Exception
     */
    private class WaitForCallbackException extends Exception {}



    // * * * G E T T E R  S & S E T T E R S * * * //
    public Handler getInputHandler(int supportType) {
        if (AttachedInputConnectorMap.get(supportType) == null)
            return null;

        return InputProcessHandler;
    }

    public Handler getRequestHandler() {
        return RequestProcessHandler;
    }

}