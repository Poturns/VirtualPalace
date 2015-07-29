package kr.poturns.virtualpalace.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public class NetworkAgent extends BaseAgent {

    private final Context mContextF;
    private final ConnectivityManager mConnectivityManagerF;
    private final WifiManager mWifiManagerF;


    public NetworkAgent(Context context) {
        mContextF = context;
        mConnectivityManagerF = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManagerF = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void startListening() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        mContextF.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void stopListening() {
        mContextF.unregisterReceiver(mReceiver);
    }

    @Override
    public int getAgentType() {
        return TYPE_AGENT_NETWORK;
    }

    /**
     * @return
     */
    @Override
    public double[] getLatestData() {
        return new double[] {
            mLatestMeasuredTimestamp
        };
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mLatestMeasuredTimestamp = System.currentTimeMillis();
        }
    };
}
