package kr.poturns.virtualpalace.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

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
    private final ServiceConnection mConnectionF = new ServiceConnection() {

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

        mInfraServiceIntent = new Intent(this, InfraDataService.class);
        bindService(mInfraServiceIntent, mConnectionF, Context.BIND_AUTO_CREATE );
    }

    @Override
    public void onTerminate() {

        unbindService(mConnectionF);

        super.onTerminate();
    }


    // * * * I N H E R I T S * * * //
    @Override
    public Handler getControlHandler() {
        //FIXME infinite loop
        PalaceMaster master = PalaceMaster.getInstance(this);
        if (master != null)
            return master.getInputHandler();

        return null;
    }

    @Override
    public void setInputConnector(OperationInputConnector connector, String name) {
        if (name == null)
            return;

        //FIXME infinite loop
        PalaceMaster master = PalaceMaster.getInstance(this);
        if (master != null)
            master.addInputConnector(connector, name);
    }


    // * * * G E T T E R S & S E T T E R S * * * //
    public InfraDataService getInfraDataService() {
        return mInfraService;
    }
}
