package kr.poturns.virtualpalace.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import kr.poturns.virtualpalace.augmented.AugmentedItem;
import kr.poturns.virtualpalace.controller.data.AugmentedTable;
import kr.poturns.virtualpalace.controller.data.ITable;
import kr.poturns.virtualpalace.controller.data.ResourceTable;
import kr.poturns.virtualpalace.controller.data.VRContainerTable;
import kr.poturns.virtualpalace.controller.data.VirtualTable;

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
     *      /data/data/kr.poturns.virtualpalace/databases/LocalDB
     */
    private static final String NAME = "LocalDB";
    /**
     * 로컬 DB .Version
     */
    private static final int VERSION = 8;

    private final Context InContext;

    private final SQLiteOpenHelper OpenHelper;


    // * * * F I E L D S * * * //



    // * * * C O N S T R U C T O R S * * * //
    private LocalDatabaseCenter(Context context) {
        InContext = context;
        OpenHelper = new SQLiteOpenHelper(context, NAME, null, VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                StringBuilder resBuilder = new StringBuilder("CREATE TABLE " + ITable.TABLE_RESOURCE + " (");
                for (ResourceTable field : ResourceTable.values())
                    resBuilder.append(field.name()).append(" ").append(field.attributes).append(",");
                resBuilder.deleteCharAt(resBuilder.length() - 1).append(");");

                StringBuilder arBuilder = new StringBuilder("CREATE TABLE " + ITable.TABLE_AUGMENTED + " (");
                for (AugmentedTable field : AugmentedTable.values())
                    arBuilder.append(field.name()).append(" ").append(field.attributes).append(",");
                arBuilder.deleteCharAt(arBuilder.length()-1).append(");");

                StringBuilder vrBuilder = new StringBuilder("CREATE TABLE " + ITable.TABLE_VIRTUAL + " (");
                for (VirtualTable field : VirtualTable.values())
                    vrBuilder.append(field.name()).append(" ").append(field.attributes).append(",");
                vrBuilder.deleteCharAt(vrBuilder.length()-1).append(");");

                StringBuilder vrContainerBuilder = new StringBuilder("CREATE TABLE " + ITable.TABLE_VR_CONTAINER + " (");
                for (VRContainerTable field : VRContainerTable.values())
                    vrContainerBuilder.append(field.name()).append(" ").append(field.attributes).append(",");
                vrContainerBuilder.deleteCharAt(vrContainerBuilder.length()-1).append(");");


                db.execSQL(resBuilder.toString());
                db.execSQL(arBuilder.toString());
                db.execSQL(vrBuilder.toString());
                db.execSQL(vrContainerBuilder.toString());

                initVRContainer(db);
            }

            private void initVRContainer(SQLiteDatabase db) {
                for (int i=0; i<18; i++) {
                    ContentValues values = new ContentValues();
                    values.put(VRContainerTable.NAME.toString(), ("BookCaseTrigger" + i));

                    db.insert(ITable.TABLE_VR_CONTAINER, null, values);
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE " + ITable.TABLE_RESOURCE);
                db.execSQL("DROP TABLE " + ITable.TABLE_AUGMENTED);
                db.execSQL("DROP TABLE " + ITable.TABLE_VIRTUAL);
                db.execSQL("DROP TABLE " + ITable.TABLE_VR_CONTAINER);

                onCreate(db);
                // TODO : DATA 옮기는 과정 필요 or Google Drive 에 백업한 데이터를 새 Local DB에 Insert 하기.
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

        Cursor cursor = OpenHelper.getReadableDatabase().rawQuery(
                "SELECT a.*, r.title, r.contents, r.res_type FROM augmented a, resource r" +
                        "WHERE (a.latitude BETWEEN ? AND ?) AND (a.longitude BETWEEN ? AND ?) AND (a.altitude BETWEEN ? AND ?)" +
                        " AND (a.res_id = r._id);",
                new String[]{
                        String.valueOf(latitude - radius), String.valueOf(latitude + radius),
                        String.valueOf(longitude - radius), String.valueOf(longitude + radius),
                        String.valueOf(altitude - radius), String.valueOf(altitude + radius)
        });

        while(cursor.moveToNext()) {
            AugmentedItem item = new AugmentedItem();
            int length = AugmentedTable.values().length;

            item.augmentedID = cursor.getInt(cursor.getColumnIndex(AugmentedTable._ID.name()));
            item.resID = cursor.getInt(cursor.getColumnIndex(AugmentedTable.RES_ID.name()));
            item.altitude = cursor.getDouble(cursor.getColumnIndex(AugmentedTable.ALTITUDE.name()));
            item.latitude = cursor.getDouble(cursor.getColumnIndex(AugmentedTable.LATITUDE.name()));
            item.longitude = cursor.getDouble(cursor.getColumnIndex(AugmentedTable.LONGITUDE.name()));
            item.supportX = cursor.getDouble(cursor.getColumnIndex(AugmentedTable.SUPPORT_X.name()));
            item.supportY = cursor.getDouble(cursor.getColumnIndex(AugmentedTable.SUPPORT_Y.name()));
            item.supportZ = cursor.getDouble(cursor.getColumnIndex(AugmentedTable.SUPPORT_Z.name()));

            item.title = cursor.getString(cursor.getColumnIndex(ResourceTable.TITLE.name()));
            item.contents = cursor.getString(cursor.getColumnIndex(ResourceTable.CONTENTS.name()));
            item.res_type = cursor.getInt(cursor.getColumnIndex(ResourceTable.RES_TYPE.name()));

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

        Cursor cursor = OpenHelper.getReadableDatabase().rawQuery(
                "SELECT v.*, r.title, r.contents, r.res_type " +
                        "FROM virtual v, resource r WHERE v.res_id = r._id;", null);

        int length = VirtualTable.values().length;
        while(cursor.moveToNext()) {
            try {
                JSONObject row = new JSONObject();

                row.put(VirtualTable._ID.toString(), cursor.getInt(VirtualTable._ID.ordinal()));
                row.put(VirtualTable.RES_ID.toString(), cursor.getInt(VirtualTable.RES_ID.ordinal()));
                row.put(VirtualTable.NAME.toString(), cursor.getString(VirtualTable.NAME.ordinal()));
                row.put(VirtualTable.MODEL_TYPE.toString(), cursor.getInt(VirtualTable.MODEL_TYPE.ordinal()));
                row.put(VirtualTable.POS_X.toString(), cursor.getDouble(VirtualTable.POS_X.ordinal()));
                row.put(VirtualTable.POS_Y.toString(), cursor.getDouble(VirtualTable.POS_Y.ordinal()));
                row.put(VirtualTable.POS_Z.toString(), cursor.getDouble(VirtualTable.POS_Z.ordinal()));
                row.put(VirtualTable.ROTATE_X.toString(), cursor.getDouble(VirtualTable.ROTATE_X.ordinal()));
                row.put(VirtualTable.ROTATE_Y.toString(), cursor.getDouble(VirtualTable.ROTATE_Y.ordinal()));
                row.put(VirtualTable.ROTATE_Z.toString(), cursor.getDouble(VirtualTable.ROTATE_Z.ordinal()));
                row.put(VirtualTable.ROTATE_W.toString(), cursor.getDouble(VirtualTable.ROTATE_W.ordinal()));
                row.put(VirtualTable.SIZE_X.toString(), cursor.getDouble(VirtualTable.SIZE_X.ordinal()));
                row.put(VirtualTable.SIZE_Y.toString(), cursor.getDouble(VirtualTable.SIZE_Y.ordinal()));
                row.put(VirtualTable.SIZE_Z.toString(), cursor.getDouble(VirtualTable.SIZE_Z.ordinal()));
                row.put(VirtualTable.PARENT_NAME.toString(), cursor.getString(VirtualTable.PARENT_NAME.ordinal()));

                row.put(ResourceTable.TITLE.toString(), cursor.getString(length+0));
                row.put(ResourceTable.CONTENTS.toString(), cursor.getString(length+1));
                row.put(ResourceTable.RES_TYPE.toString(), cursor.getString(length+2));

                array.put(row);

            } catch (JSONException e) {
                Log.e("LocalDB_Field_Exception", "queryAllVirtualRenderings() :: " + e.getMessage());
            }
        }

        cursor.close();
        return array;
    }

    final File getDatabaseFile() {
        File appDirectory = new File(Environment.getDataDirectory(), InContext.getPackageName());
        return new File(appDirectory, ("databases/" + NAME));
    }



    // * * * I N N E R  C L A S S E S * * * //

    /**
     * DB에 데이터를 조회하는 작업을 수행할 시,
     * 상황에 맞게 SQL 구문을 생성하고, 호출할 수 있도록 하는 BUILDER 패턴 클래스.
     *
     * @param <E>
     */
    public static class ReadBuilder<E extends ITable> extends CrudBuilder<E> {

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
            SQLiteDatabase db = mCenterF.OpenHelper.getReadableDatabase();


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
    public static class WriteBuilder<E extends ITable> extends CrudBuilder<E> {

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

            Log.d("LDB_Insert", "LDB Insert : " + builder.toString());
            SQLiteDatabase db = mCenterF.OpenHelper.getWritableDatabase();
            try {
                if (ITable.TABLE_RESOURCE.equalsIgnoreCase(mTableName))
                    mSetClauseValues.put("CTIME", System.currentTimeMillis());
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

            for (String clause : mWhereClauseList) {
                builder.append("(").append(clause).append("),");
            }
            builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기

            boolean result = false;
            Log.d("LDB_Update", "LDB Update : " + builder.toString());
            SQLiteDatabase db = mCenterF.OpenHelper.getWritableDatabase();
            try {
                if (ITable.TABLE_RESOURCE.equalsIgnoreCase(mTableName))
                    mSetClauseValues.put("MTIME", System.currentTimeMillis());
                int affectedRows = db.update(mTableName, mSetClauseValues, builder.toString(), null);
                result = (affectedRows > 0);
                return result;

            } catch (SQLException e) {
                Log.e("LDB_Modify_Exception", e.getMessage());
                return false;

            } finally {
                if(result)
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

            for (String clause : mWhereClauseList) {
                builder.append("(").append(clause).append("),");
            }
            builder.deleteCharAt(builder.length() - 1);   // 마지막 ' , ' 지우기

            Log.d("LDB_Delete", "LDB Delete : " + builder.toString());
            SQLiteDatabase db = mCenterF.OpenHelper.getWritableDatabase();
            try {
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
     * {@link ResourceTable},
     * {@link VirtualTable},
     * {@link AugmentedTable})를
     * 제네릭 변수로 입력받아, SQL 구문을 작성한다.
     *
     * @param <E>
     */
    protected static abstract class CrudBuilder<E extends ITable> {
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
        private CrudBuilder<E> check(E field) {
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

        //TODO TEXT 데이터에 따옴표를 붙여줌, where 절에서만 사용할 것, 메소드 이름은 리팩토링 권장
        private String getValue(E field, String value){
            return field.isTextField() ? ("'" + value + "'") : value;
        }

        /**
         * 다룰 SET 데이터를 추가한다.
         *
         * @param field
         * @param value
         * @return
         */
        public CrudBuilder<E> set(E field, String value) {
            if (field == null)
                return this;

            mSetClauseValues.put(field.toString(),value);
            //mSetClauseMap.put(field.ordinal(), new Pair<String, String>(field.toString(), value));

            return check(field);
        }

        public CrudBuilder<E> set(E field) {
            return set(field, null);
        }

        /**
         * 추가된 SET 데이터를 초기화한다.
         *
         * @return
         */
        public CrudBuilder<E> setClear() {
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
        public CrudBuilder<E> whereNotEqual(E field, String value) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(" != ")
                            .append(getValue(field,value))
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
        public CrudBuilder<E> whereEqual(E field, String value) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(" = ")
                            .append(getValue(field,value))
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
        public CrudBuilder<E> whereBetween(E field, String min, String max) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(" BETWEEN ")
                            .append(getValue(field,min))
                            .append(" AND ")
                            .append(getValue(field,max))
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
        public CrudBuilder<E> whereGreaterThan(E field, String value, boolean allowEqual) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(allowEqual ? " >= " : " > ")
                            .append(getValue(field,value))
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
        public CrudBuilder<E> whereSmallerThan(E field, String value, boolean allowEqual) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(allowEqual ? " <= " : " < ")
                            .append(getValue(field,value))
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
        public CrudBuilder<E> whereLike(E field, String value) {
            mWhereClauseList.add(
                    new StringBuilder()
                            .append(field.toString())
                            .append(" LIKE %")
                            .append(getValue(field,value))
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
        public CrudBuilder<E> whereClear() {
            mWhereClauseList.clear();

            // Set 구문이 아직 등록되지 않았을 경우, 완전 초기화와 같음.
            if (mSetClauseValues.size() > 0)
            //if (mSetClauseMap.isEmpty())
                mTableName = null;

            return this;
        }
    }
}
