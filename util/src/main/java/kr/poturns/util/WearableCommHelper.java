package kr.poturns.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class WearableCommHelper extends InputHandleHelper.ContextInputHandleHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "WearableCommHelper";
    //public static final String DATA_TRANSFER_CAPABILITY_NAME = "data_transfer";
    public static final String DATA_TRANSFER_MESSAGE_PATH = "/data_transfer";
    //public static final String SEND_STRING_CAPABILITY_NAME = "send_string";
    public static final String SEND_STRING_MESSAGE_PATH = "/send_string";

    public interface MessageListener {
        void onMessageReceived(String path, Object data);

        void onMessageReceived(String path, String data);
    }

    private GoogleApiClient mGoogleApiClient;

    //private String dataTransferNodeId = null, sendStringNodeId = null;

    private final Handler mHandler;
    private final String[] CAPABILITY_NAMES;
    private final ArrayMap<String, String> NODE_ID_MAP;
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private final InternalMessageListener internalMessageListener;

    public WearableCommHelper(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        super(context);
        this.connectionCallbacks = connectionCallbacks;
        mHandler = new Handler(Looper.getMainLooper());

        CAPABILITY_NAMES = context.getResources().getStringArray(R.array.android_wear_capabilities);
        //ResourcesUtils.get(context, "android_wear_capabilities", "kr.poturns.util");
        NODE_ID_MAP = new ArrayMap<String, String>(CAPABILITY_NAMES.length);

        internalMessageListener = new InternalMessageListener(null);

        initGoogleApiClient();

    }

    static class InternalMessageListener implements MessageApi.MessageListener {
        MessageListener messageListener;

        public InternalMessageListener(MessageListener listener) {
            this.messageListener = listener;
        }

        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            if (messageListener != null) {
                try {
                    String path = messageEvent.getPath();

                    if (path.equals(WearableCommHelper.DATA_TRANSFER_MESSAGE_PATH)) {
                        messageListener.onMessageReceived(path, IOUtils.<MovementData>fromByteArray(messageEvent.getData()));
                        return;
                    }
                    else if (path.equals(WearableCommHelper.SEND_STRING_MESSAGE_PATH)) {
                        messageListener.onMessageReceived(path, new String(messageEvent.getData()));
                        return;
                    }

                    messageListener.onMessageReceived(path, messageEvent.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public WearableCommHelper(Context context) {
        this(context, null);
    }

    public void setMessageListener(MessageListener messageListener) {
        internalMessageListener.messageListener = messageListener;
        /*
        if (mGoogleApiClient != null && messageListener != null)
            Wearable.MessageApi.removeListener(mGoogleApiClient, internalMessageListener);

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            Wearable.MessageApi.addListener(mGoogleApiClient, internalMessageListener);
        */
    }

    //*************** Lifecycle helper method ***************

    @Override
    public final void resume() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public final void pause() {
        if (mGoogleApiClient != null) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, internalMessageListener);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public final void destroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }

    }

    public final boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }


    //*************** google api Listener ***************

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
        // Now you can use the Data Layer API

        if (connectionCallbacks != null)
            connectionCallbacks.onConnected(connectionHint);

        if (internalMessageListener != null)
            Wearable.MessageApi.addListener(mGoogleApiClient, internalMessageListener);

        setupDataTransfer();
    }

    @Override
    public final void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
        if (connectionCallbacks != null)
            connectionCallbacks.onConnectionSuspended(cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result);
    }

    private void setupDataTransfer() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                CapabilityApi.GetAllCapabilitiesResult result = Wearable.CapabilityApi.getAllCapabilities(mGoogleApiClient, CapabilityApi.FILTER_REACHABLE).await();

                updateDataTransferCapability(result.getAllCapabilities());
            }
        });
    }

    private void updateDataTransferCapability(Map<String, CapabilityInfo> capabilityInfoMap) {

        for (String capabilityName : CAPABILITY_NAMES) {
            if (capabilityInfoMap.containsKey(capabilityName)) {
                CapabilityInfo capabilityInfo = capabilityInfoMap.get(capabilityName);
                Set<Node> connectedNodes = capabilityInfo.getNodes();

                NODE_ID_MAP.put('/' + capabilityName, pickBestNodeId(connectedNodes));
            }
        }
        /*
        if (capabilityInfoMap.containsKey(DATA_TRANSFER_CAPABILITY_NAME)) {
            CapabilityInfo capabilityInfo = capabilityInfoMap.get(DATA_TRANSFER_CAPABILITY_NAME);
            Set<Node> connectedNodes = capabilityInfo.getNodes();

            dataTransferNodeId = pickBestNodeId(connectedNodes);
        }

        if (capabilityInfoMap.containsKey(SEND_STRING_CAPABILITY_NAME)) {
            CapabilityInfo capabilityInfo = capabilityInfoMap.get(SEND_STRING_CAPABILITY_NAME);
            Set<Node> connectedNodes = capabilityInfo.getNodes();

            sendStringNodeId = pickBestNodeId(connectedNodes);
        }
*/
    }

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

    private boolean sendMessage(final String nodeId, final String path, final Object object) {
        if (nodeId != null) {
            AsyncTask.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                byte[] data = object instanceof String ? ((String) object).getBytes() : IOUtils.toByteArray(object);

                                Wearable.MessageApi
                                        .sendMessage(mGoogleApiClient, nodeId, path, data)
                                        .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                                            @Override
                                            public void onResult(final MessageApi.SendMessageResult sendMessageResult) {
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        onMessageResult(sendMessageResult);
                                                    }
                                                });
                                            }
                                        });

                            } catch (final IOException e) {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        onMessageError(e);
                                    }
                                });

                            }

                        }

                    }

            );

            return true;
        } else {
            return false;
        }
    }

    public final boolean sendMessage(final String path, final Object object) {
        return sendMessage(NODE_ID_MAP.get(path), path, object);
        //return sendMessage(path.equals(DATA_TRANSFER_MESSAGE_PATH) ? dataTransferNodeId : sendStringNodeId, path, object);
    }

    protected void onMessageResult(MessageApi.SendMessageResult sendMessageResult) {
        if (!sendMessageResult.getStatus().isSuccess()) {
            // Failed to send message
            Log.e(TAG, "send message : fail - " + sendMessageResult);
        } else {
            Log.d(TAG, "send message : success - " + sendMessageResult);
        }
    }

    protected void onMessageError(Throwable t) {
        t.printStackTrace();
    }


     /*

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(Wearable.API)
                .build();
    }

    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle connectionHint) {
            Log.d(TAG, "onConnected: " + connectionHint);
            // Now you can use the Data Layer API

            Wearable.MessageApi.addListener(mGoogleApiClient, MainActivity.this);

            setupDataTransfer();
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "onConnectionSuspended: " + cause);
        }
    };

    private final GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = (ConnectionResult result) -> Log.d(TAG, "onConnectionFailed: " + result);


    private void setupDataTransfer() {
        CapabilityApi.GetCapabilityResult result = Wearable.CapabilityApi.getCapability(mGoogleApiClient, DATA_TRANSFER_CAPABILITY_NAME, CapabilityApi.FILTER_REACHABLE).await();

        updateDataTransferCapability(result.getCapability());
    }

    private void updateDataTransferCapability(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();

        dataTransferNodeId = pickBestNodeId(connectedNodes);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;

        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    public void sendMessage(Object object) {
        if (dataTransferNodeId != null) {

            AsyncTask.execute(() -> {
                try {
                    byte[] data = object instanceof String ? ((String)object).getBytes() : IOUtils.toByteArray(object);

                    Wearable.MessageApi
                            .sendMessage(mGoogleApiClient, dataTransferNodeId, DATA_TRANSFER_MESSAGE_PATH, data)
                            .setResultCallback((MessageApi.SendMessageResult sendMessageResult) -> runOnUiThread(() -> onMessageResult(sendMessageResult)));

                } catch (IOException e) {
                    runOnUiThread(() -> onMessageError(e));
                }
            });

        } else {
            showToast("Unable to send message");
        }
    }

    private void onMessageResult(MessageApi.SendMessageResult sendMessageResult) {
        if (!sendMessageResult.getStatus().isSuccess()) {
            // Failed to send message
            Log.e(TAG, "send message : fail");
        } else {
            Log.d(TAG, "send message : success");
        }
    }

    private void onMessageError(Throwable t) {
        t.printStackTrace();
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }
    */
}
