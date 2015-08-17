package kr.poturns.virtualpalace;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import kr.poturns.virtualpalace.sensor.AcceleroSensorAgent;
import kr.poturns.virtualpalace.sensor.BaseSensorAgent;
import kr.poturns.virtualpalace.sensor.BatterySensorAgent;
import kr.poturns.virtualpalace.sensor.GyroSensorAgent;
import kr.poturns.virtualpalace.sensor.ISensorAgent;
import kr.poturns.virtualpalace.sensor.LocationSensorAgent;
import kr.poturns.virtualpalace.sensor.NetworkSensorAgent;

/**
 *
 * @author YeonhoKim
 */
public class InfraDataService extends Service {

    private final AcceleroSensorAgent mAcceleroAgentF;
    private final BatterySensorAgent mBatteryAgentF;
    private final GyroSensorAgent mGyroAgentF;
    private final LocationSensorAgent mLocationAgentF;
    private final NetworkSensorAgent mNetworkAgentF;

    public InfraDataService() {
        mAcceleroAgentF = new AcceleroSensorAgent(this);
        mBatteryAgentF = new BatterySensorAgent(this);
        mGyroAgentF = new GyroSensorAgent(this);
        mLocationAgentF = new LocationSensorAgent(this);
        mNetworkAgentF = new NetworkSensorAgent(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mLocationAgentF.setCollaborationWith(mBatteryAgentF);
        mLocationAgentF.setCollaborationWith(mNetworkAgentF);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAcceleroAgentF.start();
        mBatteryAgentF.start();
        mGyroAgentF.start();
        mLocationAgentF.start();
        mNetworkAgentF.start();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        mAcceleroAgentF.stop();
        mBatteryAgentF.stop();
        mGyroAgentF.stop();
        mLocationAgentF.stop();
        mNetworkAgentF.stop();

        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinderF;
    }

    private final LocalBinder mBinderF  = new LocalBinder();

    public class LocalBinder extends Binder {
        public InfraDataService getService() {
            return InfraDataService.this;
        }
    }

    /**
     *
     * @param {@link ISensorAgent} agentType
     * @return
     */
    public BaseSensorAgent getSensorAgent(int agentType) {
        switch (agentType) {
            case ISensorAgent.TYPE_AGENT_ACCELEROMETER:
                return mAcceleroAgentF;

            case ISensorAgent.TYPE_AGENT_BATTERY:
                return mBatteryAgentF;

            case ISensorAgent.TYPE_AGENT_GYROSCOPE:
                return mGyroAgentF;

            case ISensorAgent.TYPE_AGENT_LOCATION:
                return mLocationAgentF;

            case ISensorAgent.TYPE_AGENT_NETWORK:
                return mNetworkAgentF;

            default:
                return null;
        }
    }
}
