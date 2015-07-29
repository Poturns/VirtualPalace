package kr.poturns.virtualpalace.controller;

import kr.poturns.virtualpalace.db.LocalArchive;
import kr.poturns.virtualpalace.util.GoogleServiceAssistant;

/**
 * 컨트롤러의 외부적 기능을 담당한다.
 *
 * Created by YeonhoKim on 2015-07-20.
 */
public class PalaceMaster extends PalaceCore {

    private static PalaceMaster sInstance;

    public static final PalaceMaster getInstance(PalaceApplication app) {
        if (sInstance == null)
            sInstance = new PalaceMaster(app);

        return sInstance;
    }

    private final PalaceApplication mAppF;
    private final GoogleServiceAssistant mGoogleServiceAssistantF;

    private PalaceMaster(PalaceApplication app) {
        super(app);

        mAppF = app;
        mGoogleServiceAssistantF = new GoogleServiceAssistant(app, mLocalArchiveF.getSystemStringValue(LocalArchive.System.ACCOUNT));

    }


}
