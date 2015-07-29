package kr.poturns.virtualpalace.controller;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import kr.poturns.virtualpalace.InfraDataService;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public class PalaceApplication extends Application {

    InfraDataService mInfraService;

    @Override
    public void onCreate() {
        super.onCreate();

        bindService(
                new Intent(this, InfraDataService.class),
                mConnection,
                Context.BIND_AUTO_CREATE    // TODO : Bind 옵션 살펴보기
        );
    }

    @Override
    public void onTerminate() {

        unbindService(mConnection);

        super.onTerminate();
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service == null) {
                mInfraService = ((InfraDataService.LocalBinder) service).getService();
            }
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            mInfraService = null;
        }
    };
}
