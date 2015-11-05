package kr.poturns.virtualpalace.controller.data;

/**
 * Created by Yeonho on 2015-11-02.
 */
public interface IProtocolKeywords {

    String CATEGORY_EVENT = "event";

    String CATEGORY_INPUT = "input";

    String CATEGORY_REQUEST = "request";


    interface Event {
        String EVENT_INPUTMODE_CHANGED = "onInputModeChanged";

        String EVENT_SPEECH_STARTED = "onSpeechDetectionStarted";

        String EVENT_SPEECH_ENDED = "onSpeechDetectionEnded";

        String EVENT_TOAST_MESSAGE = "onToastMessage";

        String EVENT_DATA_UPDATED = "onDataUpdated";

        String KEY_TOAST_MESSAGE_TYPE = "type";

        String KEY_TOAST_MESSAGE_MSG = "message";

    }

    interface Request {
        String COMMAND_LIFECYCLE = "lifecycle";

        String COMMAND_USE_SPEECH = "use_speech";

        String COMMAND_SWITCH_INPUTMODE = "switch_inputmode";

        String COMMAND_DB_SELECT = "select_";

        String COMMAND_DB_INSERT = "insert_";

        String COMMAND_DB_UPDATE = "update_";

        String COMMAND_DB_DELETE = "delete_";

        String KEY_USE_SPEECH_MODE = "mode";

        String KEY_USE_SPEECH_MODE_COMMAND = "command";

        String KEY_USE_SPEECH_MODE_TEXT = "text";

        String KEY_USE_SPEECH_ACTION = "action";

        String KEY_USE_SPEECH_ACTION_START = "start";

        String KEY_USE_SPEECH_ACTION_STOP = "stop";

        /**
         * 현재 위치에서 일정 범위 내 존재하는 데이터를 찾는다.
         */
        String COMMAND_QUERY_NEAR_ITEMS = "query_all_near_items";
        /**
         * VR 아이템에 렌더링할 아이템 데이터를 찾는다.
         */
        String COMMAND_QUERY_VR_ITEMS = "query_all_vr_items";

        String COMMAND_QUERY_VR_BOOKCASES = "query_all_vr_bookcases";

        String KEY_CALLBACK_RESULT = "result";

        String KEY_CALLBACK_RESULT_SUCCESS = "success";

        String KEY_CALLBACK_RESULT_FAIL = "fail";

        String KEY_CALLBACK_RESULT_ERROR = "error";

        String KEY_CALLBACK_RETURN = "return";
    }


    interface Input {
        int KEY_COMMAND_DRAW = -1;

        String COMMAND_DRAW = "draw";

    }


}
