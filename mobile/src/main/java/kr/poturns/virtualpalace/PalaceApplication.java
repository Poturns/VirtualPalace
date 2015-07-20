package kr.poturns.virtualpalace;

import android.app.Application;

import kr.poturns.virtualpalace.sensor.AcceleroAgent;
import kr.poturns.virtualpalace.sensor.BatteryAgent;
import kr.poturns.virtualpalace.sensor.GyroAgent;
import kr.poturns.virtualpalace.sensor.LocationAgent;
import kr.poturns.virtualpalace.sensor.NetworkAgent;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public class PalaceApplication extends Application {

    AcceleroAgent mAcceleroAgent;
    BatteryAgent mBatteryAgent;
    GyroAgent mGyroAgent;
    LocationAgent mLocationAgent;
    NetworkAgent mNetworkAgent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAcceleroAgent = new AcceleroAgent(this);
        mBatteryAgent = new BatteryAgent(this);
        mGyroAgent = new GyroAgent(this);
        mLocationAgent = new LocationAgent(this);
        mNetworkAgent = new NetworkAgent(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
