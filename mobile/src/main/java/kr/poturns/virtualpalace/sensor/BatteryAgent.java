package kr.poturns.virtualpalace.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public class BatteryAgent extends BaseAgent {

    private final Context mContextF;
    private final BatteryManager mBatteryManagerF;

    public BatteryAgent(Context context) {
        mContextF = context;

        mBatteryManagerF = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
    }

    @Override
    public void startListening() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);

        mContextF.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void stopListening() {
        mContextF.unregisterReceiver(mReceiver);
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.BATTERY;
    }

    @Override
    protected void handleForCollectingChannels(AgentType type, float[] changed) {

    }

    @Override
    protected float[] updateForListeningChannels() {
        return new float[]{

        };
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            int plugType = intent.getIntExtra("plugged", 0);
            int level = intent.getIntExtra("level", 0);
            int scale = intent.getIntExtra("scale", 100);
            int voltage = intent.getIntExtra("voltage", 0);
            int temperature = intent.getIntExtra("temperature", 0);
            String tech = intent.getStringExtra("technology");
            int health = intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
        }
    };
}
