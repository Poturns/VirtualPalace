package kr.poturns.virtualpalace.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import kr.poturns.virtualpalace.augmented.AugmentedItem;
import kr.poturns.virtualpalace.util.DriveAssistant;

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

        @Override
        public boolean equalString(String str) {
            return this.name().equalsIgnoreCase(str);
        }
    }

    /**
     * VIRTUAL TABLE 필드 상수
     */
    public enum VIRTUAL_FIELD implements IField {
        // Virtual Object ID (DB Index)
        _ID,
        // Resource ID
        RES_ID,
        // Object Name (Unity Name)
        NAME,
        // 오브젝트 타입
        TYPE,
        // 위치 좌표 값
        POS_X,
        POS_Y,
        POS_Z,
        // 회전 값
        ROTATE_X,
        ROTATE_Y,
        ROTATE_Z,
        // 크기 값
        SIZE_X,
        SIZE_Y,
        SIZE_Z,
        // 본 오브젝트를 포함하는 오브젝트 ID
        CONTAINER,
        // Conatainer에 포함된 순서
        CONT_ORDER,
        // Customized Style
        STYLE;

        @Override
        public String getTableName() {
            return TABLE_VIRTUAL;
        }

        @Override
        public boolean equalString(String str) {
            return this.name().equalsIgnoreCase(str);
        }
    }

    /**
     * AUGMENTED TABLE 필드 상수
     */
    public enum AUGMENTED_FIELD implements IField {
        // Augmented Reality ID
        _ID,
        // Resource ID
        RES_ID,
        // 고도
        ALTITUDE,
        // 위도
        LATITUDE,
        // 경도
        LONGITUDE,
        // 입체 보완 좌표 X
        SUPPORT_X,
        // 입체 보완 좌표 Y
        SUPPORT_Y,
        // 입체 보완 좌표 Z
        SUPPORT_Z;

        @Override
        public String getTableName() {
            return TABLE_AUGMENTED;
        }

        @Override
        public boolean equalString(String str) {
            return this.name().equalsIgnoreCase(str);
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
     *      /data/data/kr.poturns.virtualpalace/databases/LocalDB
     */
    private static final String NAME = "LocalDB";
    /**
     * 로컬 DB .Version
     */
    private static final int VERSION = 4;

    private final Context mContextF;

    private final SQLiteOpenHelper mOpenHelperF;



    // * * * F I E L D S * * * //



    // * * * C O N S T R U C T O R S * * * //
    private LocalDatabaseCenter(Context context) {
        mContextF = context;
        mOpenHelperF = new SQLiteOpenHelper(context, NAME, null, VERSION) {

            private final String RESOURCE =
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
                            "mtime          INTEGER";

            private final String VIRTUAL =
                    "_id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "res_id         INTEGER NOT NULL," +
                            "name           TEXT," +
                            "type           INTEGER NOT NULL," +
                            "pos_x          REAL NOT NULL," +
                            "pos_y          REAL NOT NULL," +
                            "pos_z          REAL NOT NULL," +
                            "rotate_x       REAL," +
                            "rotate_y       REAL," +
                            "rotate_z       REAL," +
                            "size_x         REAL," +
                            "size_y         REAL," +
                            "size_z         REAL," +
                            "container      TEXT," +
                            "cont_order     INTEGER," +
                            "style          TEXT";

            private final String AUGMENTED =
                    "_id            INTEGER PRIMARY KEY AUTOINCREMENT,"  +
                            "res_id         INTEGER NOT NULL," +
                            "altitude       REAL," +
                            "latitude       REAL," +
                            "longitude      REAL," +
                            "support_x      REAL," +
                            "support_y      REAL," +
                            "support_z      REAL";

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE resource (" + RESOURCE  + ");");
                db.execSQL("CREATE TABLE virtual (" + VIRTUAL + ");");
                db.execSQL("CREATE TABLE augmented (" + AUGMENTED + ");");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // TODO : DATA 옮기는 과정 필요 or Google Drive 에 백업한 데이터를 새 Local DB에 Insert 하기.
                db.execSQL("DROP TABLE resource");
                db.execSQL("DROP TABLE virtual");
                db.execSQL("DROP TABLE augmented");

                onCreate(db);

                /*db.execSQL("ALTER TABLE resource (" + RESOURCE + ");");
                db.execSQL("ALTER TABLE virtual (" + VIRTUAL + ");");
                db.execSQL("ALTER TABLE augmented (" + AUGMENTED + ");");*/
            }
        };
    }



    // * * * M E T H O D S * * * //
    /**
     * 주어진 현실 위치 좌표로 부터 일정 반경범위 내에 위치한 오브젝트를 찾는다.
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param altitude 고도
     * @param radius 반경
     * @return {@link AugmentedItem}
     */
    public ArrayList<AugmentedItem> queryNearObjectsOnRealLocation(double latitude, double longitude, double altitude, double radius) {
        ArrayList<AugmentedItem> mNearItemList = new ArrayList<AugmentedItem>();

        Cursor cursor = mOpenHelperF.getReadableDatabase().rawQuery(
                "SELECT * FROM augmented " +
                        "WHERE (latitude BETWEEN ? AND ?) AND (longitude BETWEEN ? AND ?) AND (altitude BETWEEN ? AND ?);",
                new String[]{
                        String.valueOf(latitude - radius), String.valueOf(latitude + radius),
                        String.valueOf(longitude - radius), String.valueOf(longitude + radius),
                        String.valueOf(altitude - radius), String.valueOf(altitude + radius)
        });

        while(cursor.moveToNext()) {
            AugmentedItem item = new AugmentedItem();

            item.augmentedID = cursor.getInt(cursor.getColumnIndex(AUGMENTED_FIELD._ID.name()));
            item.resID = cursor.getInt(cursor.getColumnIndex(AUGMENTED_FIELD.RES_ID.name()));
            item.altitude = cursor.getDouble(cursor.getColumnIndex(AUGMENTED_FIELD.ALTITUDE.name()));
            item.latitude = cursor.getDouble(cursor.getColumnIndex(AUGMENTED_FIELD.LATITUDE.name()));
            item.longitude = cursor.getDouble(cursor.getColumnIndex(AUGMENTED_FIELD.LONGITUDE.name()));
            item.supportX = cursor.getDouble(cursor.getColumnIndex(AUGMENTED_FIELD.SUPPORT_X.name()));
            item.supportY = cursor.getDouble(cursor.getColumnIndex(AUGMENTED_FIELD.SUPPORT_Y.name()));
            item.supportZ = cursor.getDouble(cursor.getColumnIndex(AUGMENTED_FIELD.SUPPORT_Z.name()));

            mNearItemList.add(item);
        }

        cursor.close();
        return mNearItemList;
    }

    /**
     * 가상공간 렌더링에 필요한 오브젝트 데이터를 반환한다.
     *
     * @return JSON ARRAY
     */
    public synchronized JSONArray queryAllVirtualRenderings() {
        JSONArray array = new JSONArray();

        Cursor cursor = mOpenHelperF.getReadableDatabase().rawQuery(
                "SELECT v.*, r.name, r.description, r.thumbnail_path " +
                        "FROM virtual v, resource r WHERE v.res_id = r._id;", null);

        int length = VIRTUAL_FIELD.values().length;
        while(cursor.moveToNext()) {
            try {
                JSONObject row = new JSONObject();

                row.put(VIRTUAL_FIELD._ID.toString(), cursor.getInt(VIRTUAL_FIELD._ID.ordinal()));
                row.put(VIRTUAL_FIELD.RES_ID.toString(), cursor.getInt(VIRTUAL_FIELD.RES_ID.ordinal()));
                row.put(VIRTUAL_FIELD.NAME.toString(), cursor.getString(VIRTUAL_FIELD.NAME.ordinal()));
                row.put(VIRTUAL_FIELD.TYPE.toString(), cursor.getInt(VIRTUAL_FIELD.TYPE.ordinal()));
                row.put(VIRTUAL_FIELD.POS_X.toString(), cursor.getDouble(VIRTUAL_FIELD.POS_X.ordinal()));
                row.put(VIRTUAL_FIELD.POS_Y.toString(), cursor.getDouble(VIRTUAL_FIELD.POS_Y.ordinal()));
                row.put(VIRTUAL_FIELD.POS_Z.toString(), cursor.getDouble(VIRTUAL_FIELD.POS_Z.ordinal()));
                row.put(VIRTUAL_FIELD.ROTATE_X.toString(), cursor.getDouble(VIRTUAL_FIELD.ROTATE_X.ordinal()));
                row.put(VIRTUAL_FIELD.ROTATE_Y.toString(), cursor.getDouble(VIRTUAL_FIELD.ROTATE_Y.ordinal()));
                row.put(VIRTUAL_FIELD.ROTATE_Z.toString(), cursor.getDouble(VIRTUAL_FIELD.ROTATE_Z.ordinal()));
                row.put(VIRTUAL_FIELD.SIZE_X.toString(), cursor.getDouble(VIRTUAL_FIELD.SIZE_X.ordinal()));
                row.put(VIRTUAL_FIELD.SIZE_Y.toString(), cursor.getDouble(VIRTUAL_FIELD.SIZE_Y.ordinal()));
                row.put(VIRTUAL_FIELD.SIZE_Z.toString(), cursor.getDouble(VIRTUAL_FIELD.SIZE_Z.ordinal()));
                row.put(VIRTUAL_FIELD.CONTAINER.toString(), cursor.getString(VIRTUAL_FIELD.CONTAINER.ordinal()));
                row.put(VIRTUAL_FIELD.CONT_ORDER.toString(), cursor.getInt(VIRTUAL_FIELD.CONT_ORDER.ordinal()));
                row.put(VIRTUAL_FIELD.STYLE.toString(), cursor.getString(VIRTUAL_FIELD.STYLE.ordinal()));

                row.put(RESOURCE_FIELD.NAME.toString(), cursor.getString(length+0));
                row.put(RESOURCE_FIELD.DESCRIPTION.toString(), cursor.getString(length+1));
                row.put(RESOURCE_FIELD.THUMBNAIL_PATH.toString(), cursor.getString(length+2));

                array.put(row);

            } catch (JSONException e) {
                Log.e("LocalDB_Field_Exception", "queryAllVirtualRenderings() :: " + e.getMessage());
            }
        }

        cursor.close();
        return array;
    }

    public void backUp(DriveAssistant assistant) {
        File dbFile = new File(Environment.getDataDirectory(), "kr.poturns.virtualpalace/databases/LocalDB");
        DriveFolder folder = assistant.getAppFolder();

        // Drive Contents 생성
        DriveContents contents = assistant.newDriveContents();
        DriveAssistant.IDriveContentsApi.writeFileContents(contents, dbFile.getAbsolutePath());

        String fileName = NAME + "-" + DateFormat.format("yyMMddhhmmss", System.currentTimeMillis()) + ".dbk";
        DriveFile file = assistant.DriveFolderApi.createFile(folder, contents, fileName, "db");
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
         * toString() + equalsIgnoreCase()
         * @param str
         * @return
         */
        boolean equalString(String str);

        /**
         * 해당 필드의 DB 내 순서 값을 반환한다.
         * @return
         */
        // enum 키워드 내에 이미 구현되어 있으나, 인터페이스로 호출하기 위해 선언.
        int ordinal();
    }


    /**
     * DB에 데이터를 조회하는 작업을 수행할 시,
     * 상황에 맞게 SQL 구문을 생성하고, 호출할 수 있도록 하는 BUILDER 패턴 클래스.
     *
     * @param <E>
     */
    public static class ReadBuilder<E extends IField> extends CrudBuilder<E> {

        public ReadBuilder(LocalDatabaseCenter center) {
            super(center);
        }

        public Cursor select() {
            StringBuilder builder = new StringBuilder("SELECT ");

            if (mSetClauseValues.size() == 0)
//            if (mSetClauseMap.isEmpty())
                builder.append("*");

            else {
//                for (Pair<String, String> pair : mSetClauseMap.values())
//                    builder.append(pair.first).append(",");
                for (String key : mSetClauseValues.keySet())
                    builder.append(key).append(",");
                builder.deleteCharAt(builder.length()-1);   // 마지막 ' , ' 지우기
            }
            builder.append(" FROM ").append(mTableName);

            if (! mWhereClauseList.isEmpty()) {
                builder.append(" WHERE ");
                for (String clause : mWhereClauseList) {
                    builder.append("(").append(clause).append("),");
                }
                builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기
            }

            Log.d("LDB_Select", "LDB Select : " + builder.toString());
            SQLiteDatabase db = mCenterF.mOpenHelperF.getReadableDatabase();


            try {
                return db.rawQuery(builder.toString(), null);

            } catch (SQLException e) {
                Log.e("LDB_Insert_Exception", e.getMessage());
                return null;

            } finally {
                setClear().whereClear();
                // 데이터 반환을 위해 close()는 지연시킨다.
                //db.close();
            }
        }
    }

    /**
     * DB에 데이터를 삽입/수정/삭제하는 작업을 수행할 시,
     * 상황에 맞게 SQL 구문을 생성하고, 호출할 수 있도록 하는 BUILDER 패턴 클래스.
     *
     * @param <E>
     */
    public static class WriteBuilder<E extends IField> extends CrudBuilder<E> {

        public WriteBuilder(LocalDatabaseCenter center) {
            super(center);
        }

        /**
         * 추가된 SET 데이터를 Local DB에 삽입한다.
         * ( WHERE 구문은 사용하지 않는다. )
         *
         * @return 성공시 삽입된 ROW_ID, 실패시 -1
         */
        public long insert() {
            StringBuilder builder = new StringBuilder();
//            builder.append("INSERT INTO ")
//                    .append(mTableName)
//                    .append(" VALUES( ");

//            int length = (TABLE_RESOURCE.equals(mTableName))? RESOURCE_FIELD.values().length :
//                    (TABLE_VIRTUAL.equals(mTableName))? VIRTUAL_FIELD.values().length :
//                    (TABLE_AUGMENTED.equals(mTableName))? AUGMENTED_FIELD.values().length : 0;


//            for (int i=0; i<length; i++) {
//                Pair<String, String> element = mSetClauseMap.get(i);
//                builder.append((element == null)? null : element.second).append(",");
//            }
//            builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기
//            builder.append(" )");

            Log.d("LDB_Insert", "LDB Insert : " + builder.toString());
            SQLiteDatabase db = mCenterF.mOpenHelperF.getWritableDatabase();
            try {
//                mCenterF.mOpenHelperF.getWritableDatabase().execSQL(builder.toString());
                return db.insert(mTableName, null, mSetClauseValues);

            } catch (SQLException e) {
                Log.e("LDB_Insert_Exception", e.getMessage());
                return 0;

            } finally {
                setClear().whereClear();
                db.close();
            }
        }

        /**
         * Local DB 에서 설정된 WHERE 조건에 맞는 데이터를 SET 데이터로 수정한다.
         *
         * @return true = 성공, false = 실패
         */
        public boolean modify() {
            StringBuilder builder = new StringBuilder();

//            builder.append("UPDATE ")
//                    .append(mTableName)
//                    .append(" SET ");

            // SET
//            for (Map.Entry<String, Object> element : mSetClauseValues.valueSet()) {
//                builder.append(element.getKey())
//                        .append("=")
//                        .append(element.getValue())
//                        .append(",");
//            }
//            builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기

            // WHERE
//            builder.append(" WHERE ");
            for (String clause : mWhereClauseList) {
                builder.append("(").append(clause).append("),");
            }
            builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기


            Log.d("LDB_Update", "LDB Update : " + builder.toString());
            SQLiteDatabase db = mCenterF.mOpenHelperF.getWritableDatabase();
            try {
//                mCenterF.mOpenHelperF.getWritableDatabase().execSQL(builder.toString());
                int affectedRows = db.update(mTableName, mSetClauseValues, builder.toString(), null);
                return (affectedRows > 0);

            } catch (SQLException e) {
                Log.e("LDB_Modify_Exception", e.getMessage());
                return false;

            } finally {
                setClear().whereClear();
                db.close();
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
//            builder.append("DELETE FROM ")
//                    .append(mTableName)
//                    .append(" WHERE ");

            for (String clause : mWhereClauseList) {
                builder.append("(").append(clause).append("),");
            }
            builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기

            Log.d("LDB_Delete", "LDB Delete : " + builder.toString());
            SQLiteDatabase db = mCenterF.mOpenHelperF.getWritableDatabase();
            try {
//                mCenterF.mOpenHelperF.getWritableDatabase().execSQL(builder.toString());
                int affectedRows = db.delete(mTableName, builder.toString(), null);
                return (affectedRows > 0);

            } catch (SQLException e) {
                Log.e("LDB_Delete_Exception", e.getMessage());
                return false;

            } finally {
                setClear().whereClear();
                db.close();
            }
        }
    }

    /**
     * 테이블 필드 상수 (
     * {@link LocalDatabaseCenter.RESOURCE_FIELD},
     * {@link LocalDatabaseCenter.VIRTUAL_FIELD},
     * {@link LocalDatabaseCenter.AUGMENTED_FIELD})를
     * 제네릭 변수로 입력받아, SQL 구문을 작성한다.
     *
     * @param <E>
     */
    protected static abstract class CrudBuilder<E extends IField> {
        // NOTICE >  DB 내 필드 개수가 많아질 경우, 크기를 늘릴 것.
        protected final int MAX = 16;

        protected final LocalDatabaseCenter mCenterF;
        protected final ArrayList<String> mWhereClauseList;
        protected final ContentValues mSetClauseValues;
        //protected final TreeMap<Integer, Pair<String, String>> mSetClauseMap;
        protected String mTableName = null;

        protected CrudBuilder(LocalDatabaseCenter center) {
            mCenterF = center;
            //mSetClauseMap = new TreeMap<Integer, Pair<String, String>>();
            mSetClauseValues = new ContentValues(MAX);
            mWhereClauseList = new ArrayList<String>(MAX);
        }

        /**
         * 테이블 내 유효한 필드가 맞는지 확인한다.
         *
         * @param field
         * @throws {@link InvalidParameterException}
         * @return
         */
        private CrudBuilder check(E field) {
            if (field == null)
                return this;

            if (mTableName == null)
                mTableName = field.getTableName();

            else if (!mTableName.equalsIgnoreCase(field.getTableName()))
                throw new InvalidParameterException();

            return this;
        }

        /**
         * TableName을 직접 할당한다.
         *
         * @param tableName
         */
        public void setTable(String tableName) {
            mTableName = tableName;
        }

        /**
         * 다룰 SET 데이터를 추가한다.
         *
         * @param field
         * @param value
         * @return
         */
        public CrudBuilder set(E field, String value) {
            if (field == null)
                return this;

            mSetClauseValues.put(field.toString(), value);
            //mSetClauseMap.put(field.ordinal(), new Pair<String, String>(field.toString(), value));

            return check(field);
        }

        public CrudBuilder set(E field) {
            return set(field, null);
        }

        /**
         * 추가된 SET 데이터를 초기화한다.
         *
         * @return
         */
        public CrudBuilder setClear() {
            mSetClauseValues.clear();
            //mSetClauseMap.clear();

            // Where 구문이 아직 등록되지 않았을 경우, 완전 초기화와 같음.
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
        public CrudBuilder whereNotEqual(E field, String value) {
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
        public CrudBuilder whereEqual(E field, String value) {
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
        public CrudBuilder whereBetween(E field, String min, String max) {
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
        public CrudBuilder whereGreaterThan(E field, String value, boolean allowEqual) {
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
        public CrudBuilder whereSmallerThan(E field, String value, boolean allowEqual) {
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
         * WHERE 구문에 유사 조건을 추가한다.
         *
         * @param field
         * @param value
         * @return
         */
        public CrudBuilder whereLike(E field, String value) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(" LIKE %")
                            .append(value)
                            .append("% ")
                            .toString()
            );
            return check(field);
        }

        /**
         * 추가된 조건들을 초기화한다.
         *
         * @return
         */
        public CrudBuilder whereClear() {
            mWhereClauseList.clear();

            // Set 구문이 아직 등록되지 않았을 경우, 완전 초기화와 같음.
            if (mSetClauseValues.size() > 0)
            //if (mSetClauseMap.isEmpty())
                mTableName = null;

            return this;
        }
    }
}
