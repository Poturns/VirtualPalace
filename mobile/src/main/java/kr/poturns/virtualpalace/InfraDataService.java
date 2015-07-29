package kr.poturns.virtualpalace;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import kr.poturns.virtualpalace.sensor.AcceleroAgent;
import kr.poturns.virtualpalace.sensor.BatteryAgent;
import kr.poturns.virtualpalace.sensor.GyroAgent;
import kr.poturns.virtualpalace.sensor.LocationAgent;
import kr.poturns.virtualpalace.sensor.NetworkAgent;

/**
 *
 * @author YeonhoKim
 */
public class InfraDataService extends Service {

    private final AcceleroAgent mAcceleroAgentF;
    private final BatteryAgent mBatteryAgentF;
    private final GyroAgent mGyroAgentF;
    private final LocationAgent mLocationAgentF;
    private final NetworkAgent mNetworkAgentF;

    public InfraDataService() {
        mAcceleroAgentF = new AcceleroAgent(this);
        mBatteryAgentF = new BatteryAgent(this);
        mGyroAgentF = new GyroAgent(this);
        mLocationAgentF = new LocationAgent(this);
        mNetworkAgentF = new NetworkAgent(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mLocationAgentF.setCollaborationWith(mBatteryAgentF);
        mLocationAgentF.setCollaborationWith(mNetworkAgentF);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAcceleroAgentF.startListening();
        mBatteryAgentF.startListening();
        mGyroAgentF.startListening();
        mLocationAgentF.startListening();
        mNetworkAgentF.startListening();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        mAcceleroAgentF.stopListening();
        mBatteryAgentF.stopListening();
        mGyroAgentF.stopListening();
        mLocationAgentF.stopListening();
        mNetworkAgentF.stopListening();

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
}
