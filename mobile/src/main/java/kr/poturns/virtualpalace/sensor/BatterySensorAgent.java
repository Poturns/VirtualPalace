package kr.poturns.virtualpalace.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public class BatterySensorAgent extends BaseSensorAgent implements BaseSensorAgent.OnDataCollaborationListener{

    public static final int DATA_INDEX_PLUGGED = 1;
    public static final int DATA_INDEX_LEVEL = 2;
    public static final int DATA_INDEX_PERCENTAGE = 3;

    private final Context mContextF;
    private final BatteryManager mBatteryManagerF;

    private int mPlugType;
    private int mBatteryLevel;
    private int mBatteryPercent;

    public BatterySensorAgent(Context context) {
        mContextF = context;
        mBatteryManagerF = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
    }

    @Override
    public void startListening() {
        super.startListening();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);

        mContextF.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void stopListening() {
        super.stopListening();

        mContextF.unregisterReceiver(mReceiver);
    }

    @Override
    public int getAgentType() {
        return TYPE_AGENT_BATTERY;
    }

    /**
     * @return
     */
    @Override
    public double[] getLatestData() {
        return new double[]{
                mLatestMeasuredTimestamp,
                mPlugType,
                mBatteryLevel,
                mBatteryPercent
        };
    }

    /**
     * @param thisType
     * @param targetType
     * @param thisData
     * @param targetData
     */
    @Override
    public void onCollaboration(int thisType, int targetType, double[] thisData, double[] targetData) {
        switch (targetType) {

        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            mLatestMeasuredTimestamp = System.currentTimeMillis();
            mPlugType = intent.getIntExtra("plugged", 0);
            int level = intent.getIntExtra("level", 0);
            int scale = intent.getIntExtra("scale", 100);
            int voltage = intent.getIntExtra("voltage", 0);
            int temperature = intent.getIntExtra("temperature", 0);
            int health = intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
            String tech = intent.getStringExtra("technology");

            Log.d("BatterySensorAgent", intent.getExtras().toString());

            onDataMeasured();
        }
    };
}
