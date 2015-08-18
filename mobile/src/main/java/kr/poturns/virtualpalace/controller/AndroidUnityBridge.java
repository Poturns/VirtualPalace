package kr.poturns.virtualpalace.controller;

import android.os.Handler;

/**
 *  <b> ANDROID - UNITY 간 통신 클래스 </b>
 *
 *  @author Yeonho.Kim
 *  @author Myungjin.Kim
 */
class AndroidUnityBridge {

    // * * * S I N G L E T O N * * * //
    private static AndroidUnityBridge sInstance;

    public static AndroidUnityBridge getInstance(PalaceApplication app) {
        if (sInstance == null)
            sInstance = new AndroidUnityBridge(app);
        return sInstance;
    }


    // * * * C O N S T A N T S * * * //
    private final PalaceMaster mMasterF;
    private final Handler mRequestHandlerF;


    // * * * C O N S T R U C T O R S * * * //
    private AndroidUnityBridge(PalaceApplication app) {
        mMasterF = PalaceMaster.getInstance(app);
        mRequestHandlerF = mMasterF.getRequestHandler();
    }

}
