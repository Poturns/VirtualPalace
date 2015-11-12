package kr.poturns.virtualpalace.controller;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.util.LongSparseArray;

import com.unity3d.player.UnityPlayer;

import kr.poturns.virtualpalace.annotation.UnityApi;
import kr.poturns.virtualpalace.input.IProcessorCommands;

/**
 * <b> ANDROID - UNITY 간 통신 클래스 </b>
 * <p/>
 * * Unity -> Android 콜백 요청시,
 * 1. [Unity] AndroidUnityBridge의 requestCallbackToAndroid (jsonMessage, callback) 호출.
 * 2. [Android] 요청한 작업 처리 후, respondCallbackToUnity (id, jsonResult) 호출.
 * 3. [Android] AndroidUnityBridge에서 Unity가 요청한 callback 메소드 실행 및 jsonResult 전달.
 * 4. [Unity] jsonResult 받음.
 * <p/>
 * * Android -> Unity 콜백 요청시,
 * 1. [Android] AndroidUnityBridge의 reqeustCallbackToUnity (jsonMessage, callback) 호출.
 * 2. [Unity] 요청한 작업 처리 후, AndroidUnityBridge의 respondCallbackToAndroid (id, jsonResult) 호출.
 * 3. [Unity] id에 해당하는 callback 메소드 실행 및 jsonResult 전달.
 *
 * @author Myungjin.Kim
 * @author Yeonho.Kim
 */
public final class AndroidUnityBridge {

    // * * * S I N G L E T O N * * * //
    private static AndroidUnityBridge sInstance;

    public static synchronized AndroidUnityBridge getInstance(PalaceApplication app) {
        if (sInstance == null)
            sInstance = new AndroidUnityBridge(app);
        return sInstance;
    }


    // * * * C O N S T A N T S * * * //
    private static final String TAG = "AndroidUnityBridge";
    public static final String BUNDLE_KEY_ID = "id";
    public static final String BUNDLE_KEY_MESSAGE_JSON = "json";


    private final BridgeDelegate UNITY_DELEGATE;
    private BridgeDelegate mOtherDelegate;
    private boolean useUnityDelegate = true;
    private BridgeDelegate mCurrentDelegate;


    // * * * C O N S T R U C T O R S * * * //
    private AndroidUnityBridge(PalaceApplication app) {
        UNITY_DELEGATE = new UnityDelegate(app);
        mCurrentDelegate = UNITY_DELEGATE;
    }


    // * * * M E T H O D S * * * //

    /**
     * 컨트롤러와의 상호작용을 주어진 delegate가 대신 처리한다.
     * delegate가 null일 경우 unity가 처리한다.
     */
    public synchronized void changeDelegate(BridgeDelegate delegate) {
        this.mOtherDelegate = delegate;
        if (delegate == null)
            restoreUnityDelegate();
        else
            mCurrentDelegate = mOtherDelegate;
    }

    /**
     * 컨트롤러와의 상호작용을 unity가 처리한다.
     */
    public synchronized void restoreUnityDelegate() {
        mOtherDelegate = null;
        useUnityDelegate = true;
        mCurrentDelegate = UNITY_DELEGATE;
    }

    /**
     * UNITY 에서 ANDROID 에 요청을 보낸다.
     *
     * @param jsonMessage 요청의 세부 사항이 Json형태로 기술되어 있는 문자열
     * @param callback    요청에 대한 응답을 받을 콜백
     * @return 요청이 접수되었을 경우, TRUE
     */
    @UnityApi
    public boolean requestCallbackToAndroid(String jsonMessage, IAndroidUnityCallback callback) {
        return mCurrentDelegate.requestCallbackToAndroid(jsonMessage, callback);
    }

    /**
     * UNITY 에서 요청한 ID 에 해당하는 결과를 콜백메소드로 반환한다.
     *
     * @param id         콜백의 id
     * @param jsonResult 요청에 대한 결과값이 Json형태로 기술된 문자열
     */
    public void respondCallbackToUnity(long id, String jsonResult) {
        mCurrentDelegate.respondCallbackToUnity(id, jsonResult);
    }

    /**
     * ANDROID 에서 UNITY 에 요청을 보낸다.
     *
     * @param jsonMessage 요청의 세부 사항이 Json형태로 기술되어 있는 문자열
     * @param callback    요청에 대한 응답을 받을 콜백
     * @return 요청이 접수되었을 경우, TRUE
     */
    public boolean requestCallbackToUnity(String jsonMessage, IAndroidUnityCallback callback) {
        return mCurrentDelegate.requestCallbackToUnity(jsonMessage, callback);
    }

    /**
     * ANDROID 에서 요청한 ID 에 해당하는 결과를 콜백메소드로 반환한다.
     *
     * @param id         콜백의 id
     * @param jsonResult 요청에 대한 결과값이 Json형태로 기술된 문자열
     */
    @UnityApi
    public void respondCallbackToAndroid(long id, String jsonResult) {
        mCurrentDelegate.respondCallbackToAndroid(id, jsonResult);
    }

    /**
     * UNITY 에서 단일 메시지를 ANDROID 로 전송한다.
     *
     * @param jsonMessage 전송할 Json 메시지
     */
    @UnityApi
    public boolean sendSingleMessageToAndroid(String jsonMessage) {
        return mCurrentDelegate.sendSingleMessageToAndroid(jsonMessage);
    }

    /**
     * ANDROID 에서 단일 메시지를 UNITY 로 전송한다.
     *
     * @param json 전송할 메시지
     */
    public void sendSingleMessageToUnity(String json) {
        mCurrentDelegate.sendSingleMessageToUnity(json);
    }

    /**
     * ANDROID 에서 Input 메시지를 UNITY 로 전송한다.
     *
     * @param json 전송할 메시지
     */
    public void sendInputMessageToUnity(String json) {
        mCurrentDelegate.sendInputMessageToUnity(json);
    }


    // * * * I N N E R  C L A S S E S * * * //

    /**
     * Android-Unity간 요청에 대한 응답을 정의하는 클래스
     */
    public interface IAndroidUnityCallback {

        /**
         * 요청에 대한 응답
         *
         * @param json 요청에 대한 응답이 Json형태로 기술 된 문자열
         */
        void onCallback(String json);
    }

    /**
     * AndroidUnityBridge의 메소드의 실행을 받아볼 수 있는 클래스
     */
    public interface BridgeDelegate {
        /**
         * UNITY 에서 ANDROID 에 요청을 보낸다.
         *
         * @param jsonMessage 요청의 세부 사항이 Json형태로 기술되어 있는 문자열
         * @param callback    요청에 대한 응답을 받을 콜백
         * @return 요청이 접수되었을 경우, TRUE
         */
        boolean requestCallbackToAndroid(String jsonMessage, IAndroidUnityCallback callback);

        /**
         * UNITY 에서 요청한 ID 에 해당하는 결과를 콜백메소드로 반환한다.
         *
         * @param id         콜백의 id
         * @param jsonResult 요청에 대한 결과값이 Json형태로 기술된 문자열
         */
        void respondCallbackToUnity(long id, String jsonResult);

        /**
         * ANDROID 에서 UNITY 에 요청을 보낸다.
         *
         * @param jsonMessage 요청의 세부 사항이 Json형태로 기술되어 있는 문자열
         * @param callback    요청에 대한 응답을 받을 콜백
         * @return 요청이 접수되었을 경우, TRUE
         */
        boolean requestCallbackToUnity(String jsonMessage, IAndroidUnityCallback callback);

        /**
         * ANDROID 에서 요청한 ID 에 해당하는 결과를 콜백메소드로 반환한다.
         *
         * @param id         콜백의 id
         * @param jsonResult 요청에 대한 결과값이 Json형태로 기술된 문자열
         */
        @UnityApi
        void respondCallbackToAndroid(long id, String jsonResult);

        /**
         * UNITY 에서 단일 메시지를 ANDROID 로 전송한다.
         *
         * @param jsonMessage 전송할 Json 메시지
         */
        @UnityApi
        boolean sendSingleMessageToAndroid(String jsonMessage);

        /**
         * ANDROID 에서 Input 메시지를 UNITY 로 전송한다.
         *
         * @param json 전송할 메시지
         */
        void sendInputMessageToUnity(String json);

        /**
         * ANDROID 에서 Input 메시지를 UNITY 로 전송한다.
         *
         * @param json 전송할 메시지
         */
        void sendSingleMessageToUnity(String json);
    }

    public abstract static class BaseDelegate implements BridgeDelegate {
        private final LongSparseArray<IAndroidUnityCallback> mCallbackMapF;
        private final Object LOCK = new Object();

        protected PalaceMaster mMasterF;

        public BaseDelegate(PalaceApplication app) {
            mMasterF = PalaceMaster.getInstance(app);

            mCallbackMapF = new LongSparseArray<IAndroidUnityCallback>();
        }

        @Override
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
                    IProcessorCommands.REQUEST_CALLBACK_FROM_UNITY, bundle).sendToTarget();

            return true;
        }

        @Override
        public void respondCallbackToUnity(long id, String jsonResult) {
            IAndroidUnityCallback callback = mCallbackMapF.get(id);
            if (callback != null) {
                callback.onCallback(jsonResult);
                mCallbackMapF.remove(id);
            }
        }

        @Override
        public boolean requestCallbackToUnity(String jsonMessage, IAndroidUnityCallback callback) {
            long id;
            synchronized (LOCK) {
                id = System.currentTimeMillis();

                mCallbackMapF.put(id, callback);
            }

            //TODO id를 반영하게 만들기

            sendSingleMessageToAndroid(jsonMessage);
            return true;
        }

        @Override
        public void respondCallbackToAndroid(long id, String jsonResult) {
            IAndroidUnityCallback callback = mCallbackMapF.get(id);
            if (callback != null) {
                callback.onCallback(jsonResult);
                mCallbackMapF.remove(id);
            }
        }

        @Override
        public boolean sendSingleMessageToAndroid(String jsonMessage) {
            Message.obtain(mMasterF.getRequestHandler(), IProcessorCommands.REQUEST_MESSAGE_FROM_UNITY, jsonMessage).sendToTarget();
            return true;
        }

    }

    private static class UnityDelegate extends BaseDelegate {

        public UnityDelegate(PalaceApplication app) {
            super(app);
        }

        @Override
        public void sendSingleMessageToUnity(String json) {
            UnityPlayer.UnitySendMessage("StateScript", "HandleMessageFromController", json);
        }

        @Override
        public void sendInputMessageToUnity(String json) {
            UnityPlayer.UnitySendMessage("StateScript", "HandleInputsFromController", json);
        }
    }

}

