package kr.poturns.util;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

public class WearableCommActivity extends Activity implements MessageApi.MessageListener {
    private WearableCommHelper wearableCommHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wearableCommHelper = new WearableCommHelper(this);
        wearableCommHelper.setMessageListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wearableCommHelper.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wearableCommHelper.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        wearableCommHelper.release();

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
    }

}
