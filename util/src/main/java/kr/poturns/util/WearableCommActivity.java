package kr.poturns.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

public class WearableCommActivity extends AppCompatActivity implements MessageApi.MessageListener {
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
        wearableCommHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wearableCommHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        wearableCommHelper.onDestroy();

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
    }

}
