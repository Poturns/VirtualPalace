package kr.poturns.virtualpalace.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                Log.e("LocalDB_Field_Exception", e.getMessage());
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

    public void insertNewResource() {

    }

    public void insertRealLocationPoint() {

    }

    public void insertVirtualObjectMapping() {

    }

    public void modifyResource() {

    }

    public void modifyRealLocationPoint() {

    }

    public void modifyVirtualObjectMapping() {

    }

    public void deleteNewResource() {

    }

    public void deleteRealLocationPoint() {

    }

    public void deleteVirtualObjectMapping() {

    }


    // * * * I N N E R  C L A S S E S * * * //
    /**
     *
     */
    public enum RESOURCE_FIELD {
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
    }

    /**
     *
     */
    public enum VIRTUAL_FIELD {
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
    }

    /**
     *
     */
    public enum AUGMENTED_FIELD {
        _ID,
        RESID,
        ALTITUDE,
        LATITUDE,
        LONGITUDE;
    }
}
