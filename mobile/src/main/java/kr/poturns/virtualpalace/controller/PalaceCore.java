package kr.poturns.virtualpalace.controller;

import java.util.HashMap;

import kr.poturns.virtualpalace.InfraDataService;
import kr.poturns.virtualpalace.db.LocalArchive;
import kr.poturns.virtualpalace.input.OperationInputConnector;

/**
 * <b> INTERNAL CONTROLLER : 컨트롤러의 관리 기능을 다룬다 </b>
 * <p>
 *
 * </p>
 *
 * @author Yeonho.Kim
 */
class PalaceCore {

    // * * * C O N S T A N T S * * * //
    protected final PalaceApplication mAppF;
    protected  final LocalArchive mLocalArchiveF;

    protected final HashMap<String, OperationInputConnector> mInputConnectorMap;



    // * * * F I E L D S * * * //


    // * * * C O N S T R U C T O R S * * * //
    protected PalaceCore(PalaceApplication application) {
        mAppF = application;
        mLocalArchiveF = LocalArchive.getInstance(application);

        mInputConnectorMap = new HashMap<String, OperationInputConnector>();

    }


    // * * * M E T H O D S * * * //
    /**
     *
     * @param connector
     * @param name
     */
    void addInputConnector(OperationInputConnector connector, String name) {
        OperationInputConnector old = mInputConnectorMap.get(name);
        if (old != null)
            old.configureFromController(mAppF, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_FALSE);

        if (connector != null) {
            mInputConnectorMap.put(name, connector);
            connector.configureFromController(mAppF, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_TRUE);
        }
    }



    // * * * G E T T E R S & S E T T E R S * * * //
    protected double[] getSensorData(int sensorType) {
        InfraDataService service = mAppF.getInfraDataService();
        if (service != null)
            return service.getSensorAgent(sensorType).getLatestData();

        return null;
    }
}
