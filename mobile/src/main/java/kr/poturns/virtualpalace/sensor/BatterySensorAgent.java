package kr.poturns.virtualpalace.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * <b> 배터리 센서 AGENT </b>
 *
 * @author Yeonho.Kim
 */
public class BatterySensorAgent extends BaseSensorAgent implements BaseSensorAgent.OnDataCollaborationListener{

    // * * * C O N S T A N T S * * * //
    public static final int DATA_INDEX_PLUGGED = 1;
    public static final int DATA_INDEX_LEVEL = 2;
    public static final int DATA_INDEX_TEMPERATURE = 3;

    private final Context mContextF;
    private final BatteryManager mBatteryManagerF;
    /**
     *
     */
    private final BroadcastReceiver mReceiverF = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                mLatestMeasuredTimestamp = System.currentTimeMillis();
                mPlugType = intent.getIntExtra("plugged", 0);
                mBatteryLevel = intent.getIntExtra("level", 0);
                mTemperature = intent.getIntExtra("temperature", 0) / 10.0;

            } else if(Intent.ACTION_BATTERY_LOW.equals(action)) {

            }

            onDataMeasured();
        }
    };


    // * * * F I E L D S * * * //
    private int mPlugType;
    private int mBatteryLevel;
    private double mTemperature;


    // * * * C O N S T R U C T O R S * * * //
    public BatterySensorAgent(Context context) {
        mContextF = context;
        mBatteryManagerF = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
    }


    // * * * I N H E R I T S * * * //
    @Override
    public void startListening() {
        super.startListening();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);

        mContextF.registerReceiver(mReceiverF, intentFilter);
    }

    @Override
    public void stopListening() {
        super.stopListening();
        mContextF.unregisterReceiver(mReceiverF);
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
                mTemperature
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
        // Battery Agent 는 다른 Sensor Agent로 부터 데이터를 전달받아 Collaboration 을 수행하지 않는다.
    }

    @Override
    @Deprecated
    public void setCollaborationWith(BaseSensorAgent agent, OnDataCollaborationListener listener) {
        // Battery Agent 는 다른 Sensor Agent 로부터 데이터를 전달받아 Collaboration 을 수행하지 않는다.
    }
}
