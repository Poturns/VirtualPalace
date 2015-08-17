package kr.poturns.virtualpalace.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <b> </b>
 *
 * @author Yeonho.Kim
 */
public class LocalArchive  {

    // * * * S I N G L E T O N * * * //
    private static LocalArchive sInstance;

    public static final LocalArchive getInstance(Context context) {
        if (sInstance == null)
            sInstance = new LocalArchive(context);
        return sInstance;
    }



    // * * * C O N S T A N T S * * * //
    private final Context mContextF;

    private final SharedPreferences mPrefF;



    // * * * C O N S T R U C T O R S * * * //
    private LocalArchive(Context context) {
        mContextF = context;
        mPrefF = context.getSharedPreferences("local_archive", Context.MODE_PRIVATE);

    }



    // * * * G E T T E R S & S E T T E R S * * * //
    public String getSystemStringValue(String key) {
        return mPrefF.getString(key, null);
    }

    public boolean putSystemStringValue(String key, String value) {
        return mPrefF.edit()
                    .putString(key, value)
                    .commit();
    }



    public interface System {
        public static final String ACCOUNT = "local_archive_system_account";
    }



}
