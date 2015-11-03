package kr.poturns.virtualpalace.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.security.InvalidParameterException;

import kr.poturns.virtualpalace.InfraDataService;
import kr.poturns.virtualpalace.input.GlobalApplication;
import kr.poturns.virtualpalace.input.OperationInputConnector;

/**
 * <b> "Virtual Palace" Application 전역객체 </b>
 * <p>
 *
 * </p>
 *
 * @author Yeonho.Kim
 */
public class PalaceApplication extends GlobalApplication {

    // * * * C O N S T A N T S * * * //
    private final ServiceConnection Connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service != null) {
                mInfraService = ((InfraDataService.LocalBinder) service).getService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mInfraService = null;
        }
    };


    // * * * F I E L D S * * * //
    private InfraDataService mInfraService;

    private Intent mInfraServiceIntent;


    // * * * L I F E C Y C L E * * * //
    @Override
    public void onCreate() {
        super.onCreate();

        // Controller 생성.
        PalaceMaster.getInstance(this);

        mInfraServiceIntent = new Intent(this, InfraDataService.class);
        bindService(mInfraServiceIntent, Connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("PalaceApplication", "onTerminate");
        destroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("PalaceApplication", "onLowMemory");
        destroy();
    }


    @Override
    public void onTrimMemory(int level) {
        switch (level) {
            case TRIM_MEMORY_RUNNING_CRITICAL:
                break;
        }

        super.onTrimMemory(level);

    }

    void destroy() {
        try {
            unbindService(Connection);
            stopService(mInfraServiceIntent);

            PalaceMaster master = PalaceMaster.getInstance(this);
            if (master != null)
                master.destroy();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // * * * I N H E R I T S * * * //
    @Override
    public Handler getInputHandler(int supportType) {
        PalaceMaster master = PalaceMaster.getInstance(this);
        return (master != null)? master.getInputHandler(supportType) : null;
    }

    @Override
    public Handler setInputConnector(int supportType, OperationInputConnector connector) {
        PalaceMaster master = PalaceMaster.getInstance(this);

        if (supportType < 0)
            throw new InvalidParameterException("SupportType is not valid.");

        if ( master != null) {
            if (connector != null)
                master.attachInputConnector(supportType, connector);
            else
                master.detachInputConnector(supportType);

            return master.getInputHandler(supportType);
        }
        return null;
    }


    // * * * G E T T E R S & S E T T E R S * * * //
    public InfraDataService getInfraDataService() {
        return mInfraService;
    }
}
