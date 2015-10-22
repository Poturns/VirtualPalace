package kr.poturns.virtualpalace.communication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Map;
import java.util.Set;

/**
 * Created by Myungjin Kim on 2015-07-30.
 * <p/>
 * Wearable Device 와 Mobile Device 간 통신을 도와주는 클래스
 */
public class WearableCommunicator implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<MessageApi.SendMessageResult> {
    private static final String TAG = "WearableCommunicator";
    //public static final String CAPABILITY_NAME_SEND_DATA = "send_data";
    /**
     * Input 데이터를 전송할 때 사용하는 path
     */
    public static final String MESSAGE_PATH_INPUT_DATA = "/input_data";
    /**
     * 문자열 데이터를 전송할 때 사용하는 path
     */
    public static final String MESSAGE_PATH_STRING_MESSAGE = "/string_message";

    private GoogleApiClient mGoogleApiClient;
    private MessageApi.MessageListener mMessageListener;

    /**
     * 모듈의 전송 방식들.
     *
     * @see kr.poturns.virtualpalace.communication.R.array#android_wear_capabilities
     */
    private final String[] CAPABILITY_NAMES;
    /**
     * 연결된 노드의 ID map , key는 capability path이다.
     */
    private final Map<String, String> NODE_ID_MAP;
    private final Context context;

    public WearableCommunicator(Context context) {
        this.context = context;

        CAPABILITY_NAMES = context.getResources().getStringArray(R.array.android_wear_capabilities);
        NODE_ID_MAP = new ArrayMap<String, String>(CAPABILITY_NAMES.length);

        initGoogleApiClient();

    }

    /**
     * 연결된 디바이스에서 전송된 메시지를 수신할 {@link com.google.android.gms.wearable.MessageApi.MessageListener}를 등록한다.
     *
     * @param messageListener 메시지를 수신할 messageListener
     */
    public void setMessageListener(MessageApi.MessageListener messageListener) {

        if (mGoogleApiClient != null && messageListener != null)
            Wearable.MessageApi.removeListener(mGoogleApiClient, this.mMessageListener);

        this.mMessageListener = messageListener;

        if (isConnected())
            Wearable.MessageApi.addListener(mGoogleApiClient, messageListener);

    }

    //*************** Lifecycle helper method ***************

    /**
     * GoogleApiClient 와 연결한다.
     */
    public final void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * GoogleApiClient 와 연결 해제한다.
     */
    public final void disconnect() {
        if (mGoogleApiClient != null) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, mMessageListener);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * GoogleApiClient 와 연결 해제하고, 리소스를 정리한다.
     */
    public final void destroy() {
        if (mGoogleApiClient != null) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, mMessageListener);
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }

    }

    /**
     * GoogleApiClient 와 연결 되었는 지 확인한다.
     *
     * @return 연결 여부
     */
    public final boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }


    //*************** google api Listener ***************

    /**
     * GoogleApiClient 객체를 생성하고, 초기화한다.
     */
    protected final void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    public final void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected: " + connectionHint);

        if (mMessageListener != null)
            Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);

        setupCapability();
    }

    @Override
    public final void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result);
    }

    /**
     * GoogleApiClient 에서 연결 가능한 노드를 설정한다.
     */
    private void setupCapability() {
        Wearable.CapabilityApi
                .getAllCapabilities(mGoogleApiClient, CapabilityApi.FILTER_REACHABLE)
                .setResultCallback(mCapabilityResultCallback);
    }

    private final ResultCallback<CapabilityApi.GetAllCapabilitiesResult> mCapabilityResultCallback = new ResultCallback<CapabilityApi.GetAllCapabilitiesResult>() {
        @Override
        public void onResult(CapabilityApi.GetAllCapabilitiesResult allCapabilitiesResult) {
            if (allCapabilitiesResult.getStatus().isSuccess())
                updateCapability(allCapabilitiesResult.getAllCapabilities());
            else
                Log.e(TAG, "Wearable.CapabilityApi.getAllCapabilities() : failed.");
        }
    };

    /**
     * GoogleApiClient 에서 연결 가능한 노드 정보를 업데이트 한다.
     */
    private void updateCapability(Map<String, CapabilityInfo> capabilityInfoMap) {

        for (String capabilityName : CAPABILITY_NAMES) {
            if (capabilityInfoMap.containsKey(capabilityName)) {
                CapabilityInfo capabilityInfo = capabilityInfoMap.get(capabilityName);
                Set<Node> connectedNodes = capabilityInfo.getNodes();

                NODE_ID_MAP.put('/' + capabilityName, pickBestNodeId(connectedNodes));
            }
        }

    }

    /**
     * 가장 근처에 있는 노드를 선택한다.
     */
    private static String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;

        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    /**
     * 상대 Device에 일반 문자열 메시지를 보낸다.
     *
     * @param message 보낼 메시지
     * @see #MESSAGE_PATH_STRING_MESSAGE
     */
    public final void sendMessage(String message) {
        sendMessage(MESSAGE_PATH_STRING_MESSAGE, message);
    }

    /**
     * 상대 Device 에 메시지를 보낸다.
     *
     * @param path    메시지를 보낼 노드 path
     * @param message 보낼 메시지
     */
    public final void sendMessage(String path, String message) {
        sendMessage(NODE_ID_MAP.get(path), path, message.getBytes());
    }

    /**
     * 상대 Device 에 Input 데이터를 보낸다.
     *
     * @param message 보낼 메시지
     * @see #MESSAGE_PATH_INPUT_DATA
     */
    public final void sendMessage(byte[] message) {
        sendMessage(MESSAGE_PATH_INPUT_DATA, message);
    }

    /**
     * 상대 Device 에 메시지를 보낸다.
     *
     * @param path    메시지를 보낼 노드 path
     * @param message 보낼 메시지
     */
    public final void sendMessage(String path, byte[] message) {
        sendMessage(NODE_ID_MAP.get(path), path, message);
    }


    /**
     * 상대 Device 에 메시지를 보낸다.
     *
     * @param nodeId  메시지를 보낼 노드 ID
     * @param path    메시지를 보낼 노드 path
     * @param message 보낼 메시지
     */
    private void sendMessage(final String nodeId, final String path, final byte[] message) {
        if (nodeId != null) {
            Wearable.MessageApi
                    .sendMessage(mGoogleApiClient, nodeId, path, message)
                    .setResultCallback(this);
        }
    }

    @Override
    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
        if (!sendMessageResult.getStatus().isSuccess()) {
            // Failed to send message
            Log.e(TAG, "send message : fail - " + sendMessageResult);
        } else {
            Log.d(TAG, "send message : success - " + sendMessageResult);
        }
    }

}

