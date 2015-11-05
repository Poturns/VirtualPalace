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

import kr.poturns.virtualpalace.augmented.AugmentedItem;
import kr.poturns.virtualpalace.augmented.AugmentedOutput;
import kr.poturns.virtualpalace.controller.data.IProtocolKeywords;
import kr.poturns.virtualpalace.controller.data.ITable;
import kr.poturns.virtualpalace.controller.data.ResourceItem;
import kr.poturns.virtualpalace.controller.data.ResourceTable;
import kr.poturns.virtualpalace.controller.data.SceneLifeCycle;
import kr.poturns.virtualpalace.input.IOperationInputFilter;
import kr.poturns.virtualpalace.input.IProcessorCommands;
import kr.poturns.virtualpalace.inputmodule.speech.SpeechController;
import kr.poturns.virtualpalace.inputmodule.speech.SpeechInputConnector;
import kr.poturns.virtualpalace.inputmodule.speech.SpeechInputDetector;
import kr.poturns.virtualpalace.util.ThreadUtils;

/**
 * <b> EXTERNAL CONTROLLER : 컨트롤러의 중개 기능을 다룬다 </b>
 * <p>
 * 프로토콜 및 통신 작업에 대한 Router 역할을 수행한다.
 * 다른 모듈들과 별도로 프로세싱하기 위하여 별도 Thread 에서 Handler 를 운영한다.
 * </p>
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
    private long mTextResultCallbackId = -1;

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
    protected void dispatchEvent(String eventName, JSONObject contents) {

        if (IProtocolKeywords.Event.EVENT_TOAST_MESSAGE.equalsIgnoreCase(eventName) ||
                IProtocolKeywords.Event.EVENT_TOAST_MESSAGE.equalsIgnoreCase(eventName) ||
                IProtocolKeywords.Event.EVENT_INPUTMODE_CHANGED.equalsIgnoreCase(eventName) ||
                IProtocolKeywords.Event.EVENT_DATA_UPDATED.equalsIgnoreCase(eventName) ||
                IProtocolKeywords.Event.EVENT_SPEECH_STARTED.equalsIgnoreCase(eventName) ||
                IProtocolKeywords.Event.EVENT_SPEECH_ENDED.equalsIgnoreCase(eventName)) {

            try {
                JSONObject event = new JSONObject();
                event.put(eventName, contents);

                Message.obtain(EventProcessHandler, 0, event).sendToTarget();
            } catch (JSONException e) { ; }
        }
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

        int[][] outputs = new int[list.size()][4];
        for(int i=0; i<list.size(); i++) {
            AugmentedOutput item = list.get(i);

            outputs[i][0] = IOperationInputFilter.Operation.DRAW_AR_ITEM;
            outputs[i][1] = 1; //item.resID;
            outputs[i][2] = item.screenX;
            outputs[i][3] = item.screenY;
        }

        Message.obtain(InputProcessHandler, IProcessorCommands.INPUT_MULTI_COMMANDS, outputs).sendToTarget();
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
            if (from < IProcessorCommands.TYPE_INPUT_SUPPORT_MAJOR_LIMIT && (mActivatedConnectorSupportFlag & from) != from)
                return;

            switch (msg.what) {
                case INPUT_SYNC_COMMAND:
                    send();
                    break;

                case INPUT_SINGLE_COMMAND:
                    int[] cmd = (int[]) msg.obj;
                    accumulate(cmd);
                    break;

                case INPUT_MULTI_COMMANDS:
                    int[][] commands = (int[][]) msg.obj;
                    for (int[] command : commands)
                        accumulate(command);
                    break;

                case INPUT_TEXT_RESULT:
                    JSONObject result = new JSONObject();
                    try {
                        int mode = msg.arg1;
                        result.put(IProtocolKeywords.Request.KEY_USE_SPEECH_MODE, mode);

                        String detectedText = (String) msg.obj;
                        if (detectedText == null)
                            throw new Exception();

                        result.put(IProtocolKeywords.Request.KEY_SPEECH_RESULT, detectedText);

                    } catch (Exception e) {
                    } finally {
                        dispatchEvent(IProtocolKeywords.Event.EVENT_SPEECH_ENDED, result);

                        if (msg.arg1 == SpeechController.MODE_TEXT && mTextResultCallbackId > -1) {
                            try {
                                result.put(IProtocolKeywords.Request.KEY_CALLBACK_RESULT, true);

                                JSONObject callbackResult = new JSONObject();
                                callbackResult.put(IProtocolKeywords.Request.COMMAND_USE_SPEECH, result);

                                AndroidUnityBridge.getInstance(App).respondCallbackToUnity(mTextResultCallbackId, callbackResult.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();

                            } finally {
                                mTextResultCallbackId = -1;
                            }
                        }
                    }
                    break;
            }
        }

        /**
         * @param command
         */
        private void accumulate(int[] command) {
            long current = System.currentTimeMillis();

            // JsonMessage 객체를 초기화한 후 첫 입력 값이거나,
            // 예상 전송 시간을 넘길 때까지 명령이 생략되었을 경우,
            // 메시지 전송을 예약한다.
            if (singleMessage.length() == 0 || current > mExpectedFlushTime) {
                mExpectedFlushTime = current + INTERVAL;
                sendEmptyMessageDelayed(INPUT_SYNC_COMMAND, INTERVAL);
            }

            String cmdStr = String.valueOf(command[0]);
            int value;

            switch (command[0]) {
                // 여러 개의 AR Drawing 아이템이 전달될 경우,
                // 이전 데이터를 무시하지 않기 위하여 AR 렌더링 명령이 전달되면 메시지를 즉시 전송한다.
                case DRAW_AR_ITEM:
                    try {
                        int compressed = command[1];
                        compressed = compressed * 10000 + command[2];
                        compressed = compressed * 10000 + command[3];

                        singleMessage.put(cmdStr, compressed);
                        removeMessages(INPUT_SYNC_COMMAND);
                        send();

                    } catch (JSONException e) { ; }
                    break;

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
                                    value = curr_d * IOperationInputFilter.Direction.SEPARATION + (old_a + curr_a * 10);

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
            return (mod == 0) ? 0 : mod - 5;
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
            levelX *= (axisX > 0 ? 1 : -1);

            int absY = Math.abs(axisY);
            while (absY / levelY >= 10)
                levelY++;
            levelY *= (axisY > 0 ? 1 : -1);

            int absZ = Math.abs(axisZ);
            while (absZ / levelZ >= 10)
                levelZ++;
            levelZ *= (axisZ > 0 ? 1 : -1);


            // Direction Level 을 적용한다.
            int x = (axisX == 0) ? 0 : Math.min(9, Math.max(1, levelX + 5));
            int y = (axisY == 0) ? 0 : Math.min(9, Math.max(1, levelY + 5));
            int z = (axisZ == 0) ? 0 : Math.min(9, Math.max(1, levelZ + 5));

            // 차원 수에 맞는 Direction 값을 만든다.
            int direction = x + (z == 5 ? 0 : z * 100);
            direction += ((y == 5 && direction < 100) ? 0 : y * 10);

            // 각 축별 Amount 를 계산하기 때문에 1의 자리수는 사용하지 않고,
            // 10의 자리수부터 차례대로 축별 Amount 를 기록한다.
            int amount = ((axisX / levelX) * 10) + ((axisY / levelY) * 100) + ((axisZ / levelZ) * 1000);

            return direction * IOperationInputFilter.Direction.SEPARATION + amount;
        }


        /**
         * AndroidUnityBridge를 통해 메시지를 전송한다.
         */
        private void send() {
            if (singleMessage.length() == 0)
                return;

            Log.d("PalaceMast_Input", "Input Message : " + singleMessage.length() + " commands transfered.\n" + singleMessage.toString());

            // 동기화를 위한 Thread Blocking으로 인해 Message 처리를 지연시킬 수 있으므로,
            // Thread Pool을 이용한 순차적 전송으로 Input 메시지를 전송한다.
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
                                // Text 음성 인식의 경우, Callback 반환을 지연시킨다.
                                mTextResultCallbackId = id;
                                return;

                            } catch (Exception e2) {
                                e2.printStackTrace();
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
            if (runnable != null)
                ThreadUtils.THREAD_POOL_EXECUTOR.execute(runnable);
        }

        /**
         * JSON 메시지를 분류하여, 요청한 기능을 수행한다.
         *
         * @param jsonMessage
         * @return
         */
        private JSONObject process(String jsonMessage) throws JSONException, WaitForCallbackException {
            final JSONObject ReturnResult = new JSONObject();
            Log.d("PalaceMaster_Request", "Request Message : " + jsonMessage);

            final JSONObject RequestMessage = new JSONObject(jsonMessage);
            Iterator<String> keys = RequestMessage.keys();

            // Request 메시지 내에 있는 명령 단위 처리.
            while (keys.hasNext()) {
                String command = keys.next();

                JSONObject contents = RequestMessage.optJSONObject(command);
                JSONObject partialReturn = new JSONObject();
                boolean result = false;

                try {
                    if (COMMAND_LIFECYCLE.equalsIgnoreCase(command)) {
                        Iterator<String> scenes = contents.keys();
                        while (scenes.hasNext()) {
                            String scene = scenes.next();
                            onLifeCycle(scene, SceneLifeCycle.valueOf(contents.getString(scene)));

                            result = true;
                        }
                    } else if (COMMAND_SWITCH_INPUTMODE.equalsIgnoreCase(command)) {
                        boolean switched = true;

                        Iterator<String> inputs = contents.keys();
                        while (inputs.hasNext()) {
                            String inputName = inputs.next();

                            int inputType = Integer.parseInt(inputName);
                            switched &= contents.getBoolean(inputName) ?
                                    activateInputConnector(inputType) :
                                    deactivateInputConnector(inputType);

                            result = switched;
                        }
                    } else if (COMMAND_USE_SPEECH.equalsIgnoreCase(command)) {
                        String mode = contents.getString(KEY_USE_SPEECH_MODE);
                        String action = contents.getString(KEY_USE_SPEECH_ACTION);

                        result = requestSpeechDetection(mode, action);

                    } else if (COMMAND_QUERY_VR_BOOKCASES.equalsIgnoreCase(command)) {
                        result = queryVRContainerItems(partialReturn);

                    } else if (COMMAND_QUERY_VR_ITEMS.equalsIgnoreCase(command)) {
                        result = queryVirtualRenderingItems(partialReturn);

                    } else if (COMMAND_SAVE_VR_ITEMS.equalsIgnoreCase(command)) {
                        JSONArray contentArray = RequestMessage.getJSONArray(command);
                        result = saveVirtualRenderingItems(contentArray);

                    } else if (COMMAND_QUERY_NEAR_ITEMS.equalsIgnoreCase(command)) {
                        result = queryNearAugmentedItems(partialReturn);

                    } else if (COMMAND_SAVE_NEW_AR_ITEM.equalsIgnoreCase(command)) {
                        ResourceItem res = new ResourceItem();
                        res.title = contents.optString(ResourceTable.TITLE.name());
                        res.contents = contents.optString(ResourceTable.CONTENTS.name());
                        res.res_type = 1;   // IMAGE

                        AugmentedItem aug = new AugmentedItem();
                        aug.screenX = contents.optInt(AugmentedItem.SCREEN_X);
                        aug.screenY = contents.optInt(AugmentedItem.SCREEN_Y);

                        result = requestNewAugmentedItem(res, aug);

                    } else {
                        String table;
                        if (command.endsWith("_ar") || command.endsWith("_AR"))
                            table = ITable.TABLE_AUGMENTED;
                        else if (command.endsWith("_res") || command.endsWith("_RES"))
                            table = ITable.TABLE_RESOURCE;
                        else if (command.endsWith("_vr") || command.endsWith("_VR"))
                            table = ITable.TABLE_VIRTUAL;
                        else if (command.endsWith("_bookcase") || command.endsWith("_BOOKCASE"))
                            table = ITable.TABLE_VR_CONTAINER;
                        else // 잘못된 명령일 경우, PASS !
                            continue;

                        // DO SQL
                        String lowerCaseCommand = command.toLowerCase();
                        if (lowerCaseCommand.startsWith(COMMAND_DB_SELECT))
                            result = selectLocalData(RequestMessage.getJSONObject(command), table, partialReturn);

                        else if (lowerCaseCommand.startsWith(COMMAND_DB_INSERT))
                            result = insertNewLocalData(RequestMessage.getJSONObject(command), table);

                        else if (lowerCaseCommand.startsWith(COMMAND_DB_UPDATE))
                            result = updateLocalData(RequestMessage.getJSONObject(command), table);

                        else if (lowerCaseCommand.startsWith(COMMAND_DB_DELETE))
                            result = deleteLocalData(RequestMessage.getJSONObject(command), table);
                    }

                    partialReturn.put(KEY_CALLBACK_RESULT, result ?
                            KEY_CALLBACK_RESULT_SUCCESS : KEY_CALLBACK_RESULT_FAIL);

                } catch (WaitForCallbackException e) {
                    // 음성인식 콜백이 요청되었을 때,
                    // 동시에 전달된 다른 명령들에 대한 콜백은 무시되는 문제가 있음.
                    // but, 음성인식 콜백의 경우 해당 callback ID에 대한 콜백메소드가
                    // 호출이 되면 안되므로 음성인식 콜백 요청은 단일 명령으로만 동작해야한다.
                    throw e;

                } catch (Exception e) {
                    try {
                        partialReturn.put(KEY_CALLBACK_RESULT, KEY_CALLBACK_RESULT_ERROR);
                    } catch (JSONException e2) { }
                }

                ReturnResult.put(command, partialReturn);
                // while loop END
            }

            return ReturnResult;
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
            AndroidUnityBridge.getInstance(App).sendSingleMessageToUnity(msg.obj.toString());
        }
    }

    @Deprecated
    // 임시 TEST 코드
    public JSONObject testProcess(String json) throws Exception {
        return RequestProcessHandler.process(json);
    }



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