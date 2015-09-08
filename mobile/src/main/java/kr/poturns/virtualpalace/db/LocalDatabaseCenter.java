package kr.poturns.virtualpalace.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * <b> 로컬 저장소의 DATABASE 를 관리한다. </b>
 * <p>
 * 각 모드별 데이터를 테이블로 나누어 관리한다.
 * 1) Resource
 * 2) VR
 * 3) AR
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
    /**
     * RESOURCE TABLE 필드 상수
     */
    public enum RESOURCE_FIELD implements IField {
        _ID,
        NAME,
        TYPE,
        CATEGORY,
        ARCHIVE_PATH,
        ARCHIVE_KEY,
        DRIVE_PATH,
        DRIVE_KEY,
        THUMBNAIL_PATH,
        DESCRIPTION,
        CTIME,
        MTIME;

        @Override
        public String getTableName() {
            return TABLE_RESOURCE;
        }
    }

    /**
     * VIRTUAL TABLE 필드 상수
     */
    public enum VIRTUAL_FIELD implements IField {
        _ID,
        RESID,
        TYPE,
        POS_X,
        POS_Y,
        POS_Z,
        ROTATE_X,
        ROTATE_Y,
        ROTATE_Z,
        CONTAINER,
        CONT_ORDER,
        STYLE;

        @Override
        public String getTableName() {
            return TABLE_VIRTUAL;
        }
    }

    /**
     * AUGMENTED TABLE 필드 상수
     */
    public enum AUGMENTED_FIELD implements IField {
        _ID,
        RESID,
        ALTITUDE,
        LATITUDE,
        LONGITUDE;

        @Override
        public String getTableName() {
            return TABLE_AUGMENTED;
        }
    }

    /**
     * RESOURE TABLE 명
     */
    static final String TABLE_RESOURCE = "resource";
    /**
     * VIRTUAL TABLE 명
     */
    static final String TABLE_VIRTUAL = "virtual";
    /**
     * AUGMENTED TABLE 명
     */
    static final String TABLE_AUGMENTED = "augmented";

    /**
     * DB 저장 위치 : Local App 디렉토리에 위치하여, App 삭제시 DB도 자동으로 삭제될 수 있도록 한다.
     *      /sdcard/data/data/kr.poturns.virtualpalace/databases/LocalDB
     */
    private static final String NAME = "LocalDB";

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
                // Resource Table
                db.execSQL(
                        "CREATE TABLE resource (" +
                                "_id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "name           TEXT NOT NULL," +
                                "type           TEXT NOT NULL," +
                                "category       TEXT," +
                                "archive_path   TEXT," +
                                "archive_key    TEXT," +
                                "drive_path     TEXT," +
                                "drive_key      TEXT," +
                                "thumbnail_path TEXT," +
                                "description    TEXT," +
                                "ctime          INTEGER NOT NULL," +
                                "mtime          INTEGER" +
                                ");"
                );

                // VR Table
                db.execSQL(
                        "CREATE TABLE virtual (" +
                                "_id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                                // Resource Table 의 _id 값.
                                // 하나의 Resource 가 다양한 위치에 지정될 수 있음을 고려.
                                "resid          INTEGER NOT NULL," +
                                "type           TEXT NOT NULL," +
                                "pos_x          REAL NOT NULL," +
                                "pos_y          REAL NOT NULL," +
                                "pos_z          REAL NOT NULL," +
                                "rotate_x       REAL," +
                                "rotate_y       REAL," +
                                "rotate_z       REAL," +
                                "container      INTEGER," +
                                "cont_order     INTEGER," +
                                "style          TEXT" +
                                ");"
                );

                // AR Table
                db.execSQL(
                        "CREATE TABLE augmented (" +
                                "_id          INTEGER PRIMARY KEY AUTOINCREMENT,"  +
                                // Resource Table 의 _id 값.
                                // 하나의 Resource 가 다양한 위치에 지정될 수 있음을 고려.
                                "resid        INTEGER NOT NULL," +
                                "altitude     REAL NOT NULL," +
                                "latitude     REAL NOT NULL," +
                                "longitude    REAL NOT NULL)"
                );
            }


            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // TODO :
                // DATA 옮기는 과정 필요.
                // or
                // Google Drive 에 백업한 데이터를 새 Local DB에 Insert 하기.
            }
        };
    }



    // * * * M E T H O D S * * * //
    /**
     * 해당 id 값을 가진 Resource 데이터를 반환한다.
     *
     * @param id
     * @return JSON ARRAY
     */
    public synchronized JSONArray queryObjectDetails(int id) {
        JSONArray array = new JSONArray();

        Cursor cursor = mOpenHelperF.getReadableDatabase().rawQuery(
                "SELECT * FROM resource WHERE _id = ?",
                new String[]{ String.valueOf(id) });

        while(cursor.moveToNext()) {
            try {
                JSONObject row = new JSONObject();
                row.put(RESOURCE_FIELD._ID.toString(), cursor.getInt(RESOURCE_FIELD._ID.ordinal()));
                row.put(RESOURCE_FIELD.NAME.toString(), cursor.getString(RESOURCE_FIELD.NAME.ordinal()));
                row.put(RESOURCE_FIELD.TYPE.toString(), cursor.getString(RESOURCE_FIELD.TYPE.ordinal()));
                row.put(RESOURCE_FIELD.CATEGORY.toString(), cursor.getString(RESOURCE_FIELD.CATEGORY.ordinal()));
                row.put(RESOURCE_FIELD.ARCHIVE_PATH.toString(), cursor.getString(RESOURCE_FIELD.ARCHIVE_PATH.ordinal()));
                row.put(RESOURCE_FIELD.ARCHIVE_KEY.toString(), cursor.getString(RESOURCE_FIELD.ARCHIVE_KEY.ordinal()));
                row.put(RESOURCE_FIELD.DRIVE_PATH.toString(), cursor.getString(RESOURCE_FIELD.DRIVE_PATH.ordinal()));
                row.put(RESOURCE_FIELD.DRIVE_KEY.toString(), cursor.getString(RESOURCE_FIELD.DRIVE_KEY.ordinal()));
                row.put(RESOURCE_FIELD.THUMBNAIL_PATH.toString(), cursor.getString(RESOURCE_FIELD.THUMBNAIL_PATH.ordinal()));
                row.put(RESOURCE_FIELD.DESCRIPTION.toString(), cursor.getString(RESOURCE_FIELD.DESCRIPTION.ordinal()));
                row.put(RESOURCE_FIELD.CTIME.toString(), cursor.getLong(RESOURCE_FIELD.CTIME.ordinal()));
                row.put(RESOURCE_FIELD.MTIME.toString(), cursor.getLong(RESOURCE_FIELD.MTIME.ordinal()));

                array.put(row);

            } catch (JSONException e) {
                Log.e("LocalDB_Field_Exception", e.getMessage());
            }
        }

        cursor.close();
        return array;
    }

    /**
     * 주어진 현실 위치 좌표로 부터 일정 반경범위 내에 위치한 Object 들을 찾는다.
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param altitude 고도
     * @param radius 반경
     * @return JSON ARRAY
     */
    public synchronized JSONArray queryNearObjectsByRealLocation(double latitude, double longitude, double altitude, double radius) {
        JSONArray array = new JSONArray();

        Cursor cursor = mOpenHelperF.getReadableDatabase().rawQuery(
                "SELECT r.name, r.description, r.thumbnail_path, a.resid, a.latitude, a.longitude, a.altitude  FROM resource r, augmented a " +
                        "WHERE (a.latitude BETWEEN ? AND ?) AND (a.longitude BETWEEN ? AND ?) AND (a.altitude BETWEEN ? AND ?) AND r._id = a.resid;",
        new String[]{
                String.valueOf(latitude - radius), String.valueOf(latitude + radius),
                String.valueOf(longitude - radius), String.valueOf(longitude + radius),
                String.valueOf(altitude - radius), String.valueOf(altitude + radius)
        });

        while(cursor.moveToNext()) {
            try {
                JSONObject row = new JSONObject();
                row.put(RESOURCE_FIELD.NAME.toString(), cursor.getString(0));
                row.put(RESOURCE_FIELD.DESCRIPTION.toString(), cursor.getString(1));
                row.put(RESOURCE_FIELD.THUMBNAIL_PATH.toString(), cursor.getString(2));
                row.put(AUGMENTED_FIELD.RESID.toString(), cursor.getInt(3));
                row.put(AUGMENTED_FIELD.LATITUDE.toString(), cursor.getDouble(4));
                row.put(AUGMENTED_FIELD.LONGITUDE.toString(), cursor.getDouble(4));
                row.put(AUGMENTED_FIELD.ALTITUDE.toString(), cursor.getDouble(4));

                array.put(row);

            } catch (JSONException e) {
                Log.e("LDB_Field_Exception", e.getMessage());
            }
        }

        cursor.close();
        return array;
    }

    /**
     * 가상공간에서 렌더링에 필요한 데이터를 반환한다.
     * // TODO : 렌더링에 필요한 값을 구지 DB에 가지고 있을 필요가 있는가?
     * // TODO : Unity에서 렌더링에 필요한 데이터는 로컬 파일로 가지고 있고, 오브젝트 ID와 매칭되는 리소스만 반환하도록 하면 될 듯.
     *
     * @return JSON ARRAY
     */
    public synchronized JSONArray queryAllVirtualRenderings() {
        JSONArray array = new JSONArray();

        Cursor cursor = mOpenHelperF.getReadableDatabase().rawQuery(
                "SELECT v.*, r.name, r.description, r.thumbnail_path FROM virtual v, resource r WHERE v.resid = r._id;",
                null);

        int length = VIRTUAL_FIELD.values().length;
        while(cursor.moveToNext()) {
            try {
                JSONObject row = new JSONObject();
                row.put(VIRTUAL_FIELD._ID.toString(), VIRTUAL_FIELD._ID.ordinal());
                row.put(VIRTUAL_FIELD.RESID.toString(), VIRTUAL_FIELD.RESID.ordinal());
                row.put(VIRTUAL_FIELD.TYPE.toString(), VIRTUAL_FIELD.TYPE.ordinal());
                row.put(VIRTUAL_FIELD.POS_X.toString(), VIRTUAL_FIELD.POS_X.ordinal());
                row.put(VIRTUAL_FIELD.POS_Y.toString(), VIRTUAL_FIELD.POS_Y.ordinal());
                row.put(VIRTUAL_FIELD.POS_Z.toString(), VIRTUAL_FIELD.POS_Z.ordinal());
                row.put(VIRTUAL_FIELD.ROTATE_X.toString(), VIRTUAL_FIELD.ROTATE_X.ordinal());
                row.put(VIRTUAL_FIELD.ROTATE_Y.toString(), VIRTUAL_FIELD.ROTATE_Y.ordinal());
                row.put(VIRTUAL_FIELD.ROTATE_Z.toString(), VIRTUAL_FIELD.ROTATE_Z.ordinal());
                row.put(VIRTUAL_FIELD.CONTAINER.toString(), VIRTUAL_FIELD.CONTAINER.ordinal());
                row.put(VIRTUAL_FIELD.CONT_ORDER.toString(), VIRTUAL_FIELD.CONT_ORDER.ordinal());
                row.put(VIRTUAL_FIELD.STYLE.toString(), VIRTUAL_FIELD.STYLE.ordinal());
                row.put(RESOURCE_FIELD.NAME.toString(), length + 0);
                row.put(RESOURCE_FIELD.DESCRIPTION.toString(), length + 1);
                row.put(RESOURCE_FIELD.THUMBNAIL_PATH.toString(), length + 2);

                array.put(row);

            } catch (JSONException e) {
                Log.e("LocalDB_Field_Exception", e.getMessage());
            }
        }

        cursor.close();
        return array;
    }



    // * * * I N N E R  C L A S S E S * * * //
    /**
     * 테이블 필드 상수를 묶기 위한 인터페이스.
     */
    protected interface IField {
        /**
         * 해당 필드가 소속되어 있는 테이블 명을 반환한다.
         * @return
         */
        String getTableName();

        /**
         * 해당 필드의 DB 내 순서 값을 반환한다.
         * @return
         */
        // enum 키워드 내에 이미 구현되어 있으나, 인터페이스로 호출하기 위해 선언.
        int ordinal();
    }

    /**
     * DB에 데이터를 삽입/수정/삭제하는 작업을 수행할 시,
     * 상황에 맞게 SQL 구문을 생성하고, 호출할 수 있도록 하는 BUILDER 패턴 클래스.
     *
     * 테이블 필드 상수 (
     * {@link kr.poturns.virtualpalace.db.LocalDatabaseCenter.RESOURCE_FIELD},
     * {@link kr.poturns.virtualpalace.db.LocalDatabaseCenter.VIRTUAL_FIELD},
     * {@link kr.poturns.virtualpalace.db.LocalDatabaseCenter.AUGMENTED_FIELD})를
     * 제네릭 변수로 입력받아, SQL 구문을 작성한다.
     */
    public final class WriteBuilder<E extends IField> {
        // NOTICE >  DB 내 필드 개수가 많아질 경우, 크기를 늘릴 것.
        private final int MAX = 16;

        private final ArrayList<Pair<String, String>> mSetClauseList = new ArrayList<Pair<String, String>>(MAX);
        private final ArrayList<String> mWhereClauseList = new ArrayList<String>(MAX);
        private String mTableName = null;

        /**
         * 테이블 내 유효한 필드가 맞는지 확인한다.
         *
         * @param field
         * @throws {@link InvalidParameterException}
         * @return
         */
        private WriteBuilder check(E field) {
            if (mTableName == null)
                mTableName = field.getTableName();

            else if (mTableName != field.getTableName())
                throw new InvalidParameterException();

            return this;
        }

        /**
         * 다룰 SET 데이터를 추가한다.
         *
         * @param field
         * @param value
         * @return
         */
        public WriteBuilder set(E field, String value) {
            mSetClauseList.set(field.ordinal(), new Pair<String, String>(field.toString(), value));
            return check(field);
        }

        /**
         * 추가된 SET 데이터를 초기화한다.
         *
         * @return
         */
        public WriteBuilder setClear() {
            mSetClauseList.clear();

            if (mWhereClauseList.isEmpty())
                mTableName = null;
            return this;
        }

        /**
         * WHERE 구문에 NOT EQUAL 조건을 추가한다.
         *
         * @param field
         * @param value
         * @throws {@link InvalidParameterException}
         * @return
         */
        public WriteBuilder whereNotEqual(E field, String value) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(" != ")
                            .append(value)
                            .toString()
            );
            return check(field);
        }

        /**
         * WHERE 구문에 EQUAL 조건을 추가한다.
         *
         * @param field
         * @param value
         * @throws {@link InvalidParameterException}
         * @return
         */
        public WriteBuilder whereEqual(E field, String value) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(" = ")
                            .append(value)
                            .toString()
            );
            return check(field);
        }

        /**
         * WHERE 구문에 BETWEEN 조건을 추가한다.
         *
         * @param field
         * @param min
         * @param max
         * @throws {@link InvalidParameterException}
         * @return
         */
        public WriteBuilder whereBetween(E field, String min, String max) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(" BETWEEN ")
                            .append(min)
                            .append(" AND ")
                            .append(max)
                            .toString()
            );
            return check(field);
        }

        /**
         * WHERE 구문에 초과/이상 조건을 추가한다.
         *
         * @param field
         * @param value
         * @param allowEqual true = 이상, false = 초과
         * @throws {@link InvalidParameterException}
         * @return
         */
        public WriteBuilder whereGreaterThan(E field, String value, boolean allowEqual) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(allowEqual ? " >= " : " > ")
                            .append(value)
                            .toString()
            );
            return check(field);
        }

        /**
         * WHERE 구문에 미만/이하 조건을 추가한다.
         *
         * @param field
         * @param value
         * @param allowEqual true = 이하, false = 미만
         * @throws {@link InvalidParameterException}
         * @return
         */
        public WriteBuilder whereLessThan(E field, String value, boolean allowEqual) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(allowEqual ? " <= " : " < ")
                            .append(value)
                            .toString()
            );
            return check(field);
        }

        /**
         * 추가된 조건들을 초기화한다.
         *
         * @return
         */
        public WriteBuilder whereClear() {
            mWhereClauseList.clear();

            if (mSetClauseList.isEmpty())
                mTableName = null;

            return this;
        }

        /**
         * 추가된 SET 데이터를 Local DB에 삽입한다.
         * ( WHERE 구문은 사용하지 않는다. )
         *
         * @return true = 성공, false = 실패
         */
        public boolean insert() {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO ")
                    .append(mTableName)
                    .append(" VALUES( ");

            int length = (TABLE_RESOURCE.equals(mTableName))? RESOURCE_FIELD.values().length :
                    (TABLE_VIRTUAL.equals(mTableName))? VIRTUAL_FIELD.values().length :
                    (TABLE_AUGMENTED.equals(mTableName))? AUGMENTED_FIELD.values().length : 0;

            for (int i=0; i<length; i++) {
                Pair<String, String> element = mSetClauseList.get(i);
                builder.append((element == null)? null : element.second).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기
            builder.append(" )");

            try {
                setClear().whereClear();
                mOpenHelperF.getWritableDatabase().execSQL(builder.toString());
                return true;

            } catch (SQLException e) {
                Log.e("LDB_Insert_Exception", e.getMessage());
                return false;
            }
        }

        /**
         * Local DB 에서 설정된 WHERE 조건에 맞는 데이터를 SET 데이터로 수정한다.
         *
         * @return true = 성공, false = 실패
         */
        public boolean modify() {
            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE ")
                    .append(mTableName)
                    .append(" SET ");

            for (Pair<String, String> element : mSetClauseList) {
                if (element == null)
                    continue;

                builder.append(element.first)
                        .append("=")
                        .append(element.second)
                        .append(",");
            }
            builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기

            builder.append(" WHERE ");
            for (String clause : mWhereClauseList) {
                builder.append("(").append(clause).append("),");
            }
            builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기

            try {
                setClear().whereClear();
                mOpenHelperF.getWritableDatabase().execSQL(builder.toString());
                return true;

            } catch (SQLException e) {
                Log.e("LDB_Modify_Exception", e.getMessage());
                return false;
            }
        }

        /**
         * 추가된 WHERE 조건에 해당하는 데이터를 Local DB 에서 삭제한다.
         * ( SET 구문은 사용하지 않는다. )
         *
         * @return true = 성공, false = 실패
         */
        public boolean delete() {
            StringBuilder builder = new StringBuilder();
            builder.append("DELETE FROM ")
                    .append(mTableName)
                    .append(" WHERE ");

            for (String clause : mWhereClauseList) {
                builder.append("(").append(clause).append("),");
            }
            builder.deleteCharAt(builder.length()-1);   // 마지막 ' , ' 지우기

            try {
                setClear().whereClear();
                mOpenHelperF.getWritableDatabase().execSQL(builder.toString());
                return true;

            } catch (SQLException e) {
                Log.e("LDB_Delete_Exception", e.getMessage());
                return false;
            }
        }

    }
}
