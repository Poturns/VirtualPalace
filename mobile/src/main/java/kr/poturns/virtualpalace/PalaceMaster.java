package kr.poturns.virtualpalace;

import android.content.Context;

import kr.poturns.virtualpalace.db.LocalArchive;
import kr.poturns.virtualpalace.util.GoogleServiceAssistant;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public class PalaceMaster {

    private static PalaceMaster sInstance;

    public static final PalaceMaster getInstance(Context context) {
        if (sInstance == null)
            sInstance = new PalaceMaster(context);

        return sInstance;
    }

    private final Context mContextF;

    private final LocalArchive mLocalArchiveF;
    private final GoogleServiceAssistant mGoogleServiceAssistantF;

    private PalaceMaster(Context context) {
        mContextF = context;
        mLocalArchiveF = LocalArchive.getInstance(context);

        mGoogleServiceAssistantF = new GoogleServiceAssistant(context, mLocalArchiveF.getSystemStringValue(LocalArchive.System.ACCOUNT));
    }
}
