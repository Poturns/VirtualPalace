package kr.poturns.virtualpalace;

import kr.poturns.virtualpalace.sensor.AcceleroSensorAgent;
import kr.poturns.virtualpalace.sensor.BaseSensorAgent;
import kr.poturns.virtualpalace.sensor.BatterySensorAgent;
import kr.poturns.virtualpalace.sensor.GyroSensorAgent;
import kr.poturns.virtualpalace.sensor.ISensorAgent;
import kr.poturns.virtualpalace.sensor.LocationSensorAgent;
import kr.poturns.virtualpalace.sensor.MagneticSensorAgent;
import kr.poturns.virtualpalace.sensor.NetworkSensorAgent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 *
 * @author YeonhoKim
 */
public class InfraDataService extends Service {

    private AcceleroSensorAgent mAcceleroAgent;
    private BatterySensorAgent mBatteryAgent;
    private GyroSensorAgent mGyroAgent;
    private LocationSensorAgent mLocationAgent;
    private NetworkSensorAgent mNetworkAgent;
    private MagneticSensorAgent mMagneticAgent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAcceleroAgent = new AcceleroSensorAgent(this);
        mBatteryAgent = new BatterySensorAgent(this);
        mGyroAgent = new GyroSensorAgent(this);
        mLocationAgent = new LocationSensorAgent(this);
        mNetworkAgent = new NetworkSensorAgent(this);
        mMagneticAgent = new MagneticSensorAgent(this);

        mLocationAgent.setCollaborationWith(mBatteryAgent);
        mLocationAgent.setCollaborationWith(mNetworkAgent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startListening();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        stopListening();

        super.onDestroy();
    }

    public void startListening() {
        mAcceleroAgent.start();
        mBatteryAgent.start();
        mGyroAgent.start();
        mLocationAgent.start();
        mNetworkAgent.start();
        mMagneticAgent.start();
    }

    public void stopListening() {
        mAcceleroAgent.stop();
        mBatteryAgent.stop();
        mGyroAgent.stop();
        mLocationAgent.stop();
        mNetworkAgent.stop();
        mMagneticAgent.stop();
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
                return mAcceleroAgent;

            case ISensorAgent.TYPE_AGENT_BATTERY:
                return mBatteryAgent;

            case ISensorAgent.TYPE_AGENT_GYROSCOPE:
                return mGyroAgent;

            case ISensorAgent.TYPE_AGENT_LOCATION:
                return mLocationAgent;

            case ISensorAgent.TYPE_AGENT_NETWORK:
                return mNetworkAgent;
                
            case ISensorAgent.TYPE_AGENT_MAGNETIC:
            	return mMagneticAgent;

            default:
                return null;
        }
    }
}
