package kr.poturns.virtualpalace.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * <b> 네트워크 센서 AGENT </b>
 *
 * @author Yeonho.Kim
 */
public class NetworkSensorAgent extends BaseSensorAgent {

    // * * * C O N S T A N T S * * * //
    private final Context mContextF;
    private final ConnectivityManager mConnectivityManagerF;
    private final WifiManager mWifiManagerF;


    // * * * F I E L D S * * * //
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mLatestMeasuredTimestamp = System.currentTimeMillis();

        }
    };


    // * * * C O N S T R U C T O R S * * * //
    public NetworkSensorAgent(Context context) {
        mContextF = context;
        mConnectivityManagerF = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManagerF = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }


    // * * * I N H E R I T S * * * //
    @Override
    public void startListening() {
        super.startListening();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        mContextF.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void stopListening() {
        super.stopListening();

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

}
