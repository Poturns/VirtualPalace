package kr.poturns.virtualpalace.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public class LocalArchive  {

    private static LocalArchive sInstance;

    public static final LocalArchive getInstance(Context context) {
        if (sInstance == null)
            sInstance = new LocalArchive(context);

        return sInstance;
    }

    private final Context mContextF;

    private final SharedPreferences mPrefF;

    private LocalArchive(Context context) {
        mContextF = context;
        mPrefF = context.getSharedPreferences("local_archive", Context.MODE_PRIVATE);

    }

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
