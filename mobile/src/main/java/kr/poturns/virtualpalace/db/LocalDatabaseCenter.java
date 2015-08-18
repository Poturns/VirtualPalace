package kr.poturns.virtualpalace.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <b> 로컬 저장소의 DATABASE 를 관리한다. </b>
 * <p>
 *
 * </p>
 *
 * @author Yeonho.Kim
 */
public class LocalDatabaseCenter {

    // * * * S I N G L E T O N * * * //
    private static LocalDatabaseCenter sInstance;

    public static final LocalDatabaseCenter getInstance(Context context) {
        if (sInstance == null)
            sInstance = new LocalDatabaseCenter(context);
        return sInstance;
    }



    // * * * C O N S T A N T S * * * //
    public static final String NAME = "LocalDB";

    private static final int VERSION = 0;

    private final Context mContextF;

    private final SQLiteOpenHelper mOpenHelperF;


    // * * * F I E L D S * * * //



    // * * * C O N S T R U C T O R S * * * //
    private LocalDatabaseCenter(Context context) {
        mContextF = context;

        mOpenHelperF = new SQLiteOpenHelper(context, NAME, null, VERSION) {

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE ...");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
    }



    // * * * M E T H O D S * * * //




    // * * * I N N E R  C L A S S E S * * * //

}
