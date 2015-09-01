package kr.poturns.virtualpalace.controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;

import kr.poturns.virtualpalace.input.IControllerCommands;

/**
 *  <b> ANDROID - UNITY 간 통신 클래스 </b>
 *
 *  @author Myungjin.Kim
 *  @author Yeonho.Kim
 */
class AndroidUnityBridge {

    // * * * S I N G L E T O N * * * //
    private static AndroidUnityBridge sInstance;

    public static AndroidUnityBridge getInstance(PalaceApplication app) {
        if (sInstance == null)
            sInstance = new AndroidUnityBridge(app);
        return sInstance;
    }


    // * * * C O N S T A N T S * * * //
    public static final String BUNDLE_KEY_ID = "id";
    public static final String BUNDLE_KEY_MESSAGE_JSON = "json";

    private final Object LOCK = new Object();
    private final PalaceMaster mMasterF;
    private final Handler mRequestHandlerF;
    private final HashMap<Long, IAndroidUnityCallback> mCallbackMapF;


    // * * * C O N S T R U C T O R S * * * //
    private AndroidUnityBridge(PalaceApplication app) {
        mMasterF = PalaceMaster.getInstance(app);
        mRequestHandlerF = mMasterF.getRequestHandler();

        mCallbackMapF = new HashMap<Long, IAndroidUnityCallback>();
    }



    // * * * M E T H O D S * * * //
    /**
     * UNITY 에서 ANDROID 에 메시지와 함께 콜백메소드를 요청한다.
     *
     * @param jsonMessage
     * @param callback
     * @return 요청이 접수되었을 경우, TRUE
     * @deprecated ( UNITY 에서만 호출하도록 한다. )
     */
    public boolean requestCallbackToAndroid(String jsonMessage, IAndroidUnityCallback callback) {
        long id;
        synchronized (LOCK) {
            id = System.currentTimeMillis();
            mCallbackMapF.put(id, callback);
        }

        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ID, id);
        bundle.putString(BUNDLE_KEY_MESSAGE_JSON, jsonMessage);

        Message.obtain(mMasterF.getRequestHandler(),
                IControllerCommands.REQUEST_CALLBACK_FROM_UNITY, bundle).sendToTarget();

        return true;
    }

    /**
     * UNITY 에서 요청한 ID 에 해당하는 결과를 콜백메소드로 반환한다.
     *
     * @param id
     * @param jsonResult
     */
    public synchronized void respondCallbackToUnity(long id, String jsonResult) {
        IAndroidUnityCallback callback = mCallbackMapF.remove(id);
        if (callback != null)
            callback.onCallback(jsonResult);
    }

    /**
     * ANDROID 에서 UNITY 에 메시지와 함께 콜백메소드를 요청한다.
     *
     * @param jsonMessage
     * @param callback
     * @return 요청이 접수되었을 경우, TRUE
     */
    public boolean requestCallbackToUnity(String jsonMessage, IAndroidUnityCallback callback) {
        long id;
        synchronized (LOCK) {
            id = System.currentTimeMillis();

            mCallbackMapF.put(id, callback);
        }

        //UnityPlayer.sendMessage();
        return true;
    }

    /**
     * ANDROID 에서 요청한 ID 에 해당하는 결과를 콜백메소드로 반환한다.
     *
     * @param id
     * @param jsonResult
     * @deprecated ( UNITY 에서만 호출하도록 한다. )
     */
    public synchronized void respondCallbackToAndroid(long id, String jsonResult) {
        IAndroidUnityCallback callback = mCallbackMapF.get(id);
        if (callback != null)
            callback.onCallback(jsonResult);
    }

    /**
     * UNITY 에서 단일 메시지를 ANDROID 로 전송한다.
     *
     * @param jsonMessage
     * @return 메시지가 정상적으로 전송되었을 때, TRUE
     * @deprecated ( UNITY 에서만 호출하도록 한다. )
     */
    public synchronized boolean sendSingleMessageToAndroid(String jsonMessage) {
        Message.obtain(mMasterF.getRequestHandler(),
                IControllerCommands.REQUEST_MESSAGE_FROM_UNITY, jsonMessage).sendToTarget();

        return true;
    }

    /**
     * ANDROID 에서 단일 메시지를 UNITY 로 전송한다.
     *
     * @param target
     * @param func
     * @param param
     */
    public synchronized void sendSingleMessageToUnity(String target, String func, String param) {
        //UnityPlayer.sendMessage(target, func, param);
    }



    // * * * I N N E R  C L A S S E S * * * //
    /**
     *
     */
    public interface IAndroidUnityCallback {

        public void onCallback(String json);
    }

}

