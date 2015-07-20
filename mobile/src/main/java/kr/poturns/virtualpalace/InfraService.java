package kr.poturns.virtualpalace;

import android.app.Service;
import android.content.Intent;
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
public class InfraService extends Service {

    private static final Object INSTANCE_LOCK = new Object();

    private static InfraService sInstance;

    public static final InfraService getInstance() {
        synchronized (INSTANCE_LOCK) {
            return sInstance;
        }
    }

    private final AcceleroAgent mAcceleroAgentF;
    private final BatteryAgent mBatteryAgentF;
    private final GyroAgent mGyroAgentF;
    private final LocationAgent mLocationAgentF;
    private final  NetworkAgent mNetworkAgentF;

    public InfraService() {
        synchronized (INSTANCE_LOCK) {
            sInstance = this;

            mAcceleroAgentF = new AcceleroAgent(this);
            mBatteryAgentF = new BatteryAgent(this);
            mGyroAgentF = new GyroAgent(this);
            mLocationAgentF = new LocationAgent(this);
            mNetworkAgentF = new NetworkAgent(this);
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (INSTANCE_LOCK) {


        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
}
