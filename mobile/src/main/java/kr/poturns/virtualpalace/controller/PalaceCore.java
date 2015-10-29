package kr.poturns.virtualpalace.controller;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import kr.poturns.virtualpalace.InfraDataService;
import kr.poturns.virtualpalace.augmented.AugmentedItem;
import kr.poturns.virtualpalace.input.IControllerCommands;
import kr.poturns.virtualpalace.input.IControllerCommands.JsonKey;
import kr.poturns.virtualpalace.input.OperationInputConnector;
import kr.poturns.virtualpalace.inputmodule.speech.SpeechController;
import kr.poturns.virtualpalace.inputmodule.speech.SpeechInputConnector;
import kr.poturns.virtualpalace.sensor.BaseSensorAgent;
import kr.poturns.virtualpalace.sensor.ISensorAgent;
import kr.poturns.virtualpalace.sensor.LocationSensorAgent;
import kr.poturns.virtualpalace.util.DriveAssistant;


/**
 * <b> INTERNAL CONTROLLER : 컨트롤러의 관리 기능을 다룬다 </b>
 * <p>
 *     - 데이터 처리 (DB + Archive + Drive)
 *     -
 * <p/>
 * </p>
 *
 * @author Yeonho.Kim
 */
class PalaceCore {

    // * * * C O N S T A N T S * * * //
    protected final PalaceApplication mAppF;
    protected final TreeMap<Integer, OperationInputConnector> mInputConnectorMapF;

    private final LocalArchive mLocalArchiveF;
    private final LocalDatabaseCenter mDBCenterF;
    private final DriveAssistant mDriveAssistantF;
    private final TreeMap<Long, OnPlayModeListener> mPlayModeListenersF;


    // * * * F I E L D S * * * //
    protected OnPlayModeListener.PlayMode mCurrentMode;
    protected boolean isOnCardboard;
    protected int mSupportsFlag;


    // * * * C O N S T R U C T O R S * * * //
    protected PalaceCore(PalaceApplication application) {
        mAppF = application;
        mLocalArchiveF = LocalArchive.getInstance(application);
        mDBCenterF = LocalDatabaseCenter.getInstance(application);
        mDriveAssistantF = new DriveAssistant(application);

        mInputConnectorMapF = new TreeMap<Integer, OperationInputConnector>();
        mPlayModeListenersF = new TreeMap<Long, OnPlayModeListener>();
        mPlayModeListenersF.put(0L, new OnPlayModeListener() {
            @Override
            public void onAttached(Long attachedKey) {;}

            @Override
            public void onDetached() {;}

            @Override
            public void onPlayModeChanged(PlayMode mode, boolean onCardboard) {
                mCurrentMode = mode;
                isOnCardboard = onCardboard;
            }
        });

        mCurrentMode = OnPlayModeListener.PlayMode.STANDARD;
        isOnCardboard = false;
        mSupportsFlag = IControllerCommands.TYPE_INPUT_SUPPORT_SCREENTOUCH
                        | IControllerCommands.TYPE_INPUT_SUPPORT_VOICE ;
    }


    // * * * M E T H O D S * * * //
    /**
     *
     * @param listener
     */
    void attachOnPlayModeChangedListener(OnPlayModeListener listener) {
        if (listener == null)
            return ;

        long key = System.currentTimeMillis();
        mPlayModeListenersF.put(key, listener);
        listener.onAttached(key);
    }

    /**
     *
     * @param key
     */
    void detachOnPlayModeChangedListener(long key) {
        if (key == 0)
            return ;

        mPlayModeListenersF.remove(key).onDetached();
    }

    /**
     *
     * @param mode
     */
    boolean switchMode(OnPlayModeListener.PlayMode mode, boolean onCardboard) {
        if (mCurrentMode == mode && isOnCardboard == onCardboard)
            return false;

        for(OnPlayModeListener listener : mPlayModeListenersF.values()) {
            listener.onPlayModeChanged(mode, onCardboard);
        }
        return true;
    }

    /**
     * {@link OperationInputConnector}를 Controller 에 연결한다.
     * 연결할 경우, 자동으로 활성화된다.
     *
     * @param connector Connector Support Type
     * @param supportType Connector Instance
     */
    void attachInputConnector(int supportType, OperationInputConnector connector) {
        // 동일 SupportType 에서 이미 연결되어 있는 경우, 해제한다.
        OperationInputConnector attached = mInputConnectorMapF.get(supportType);
        if (attached != null) {
            attached.configureFromController(mAppF, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_FALSE);
        }

        // 동일 SupportType 은 새로운 Connector 로 연결되고, 활성화 된다.
        if (connector != null) {
            mSupportsFlag |= supportType;
            mInputConnectorMapF.put(supportType, connector);
            connector.configureFromController(mAppF, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_TRUE);
            connector.configureFromController(mAppF, OperationInputConnector.KEY_ACTIVATE, OperationInputConnector.VALUE_TRUE);
        }
    }

    /**
     * {@link OperationInputConnector}를 Controller 에서 해제한다.
     * 해제할 경우, 자동으로 비활성화된다.
     *
     * @param supportType Connector Support Type
     */
    void detachInputConnector(int supportType) {
        OperationInputConnector connector = mInputConnectorMapF.remove(supportType);

        if (connector != null) {
            mSupportsFlag = 0;
            for(int type : mInputConnectorMapF.keySet())
                mSupportsFlag |= type;

            connector.configureFromController(mAppF, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_FALSE);
            connector.configureFromController(mAppF, OperationInputConnector.KEY_ACTIVATE, OperationInputConnector.VALUE_FALSE);
        }
    }

    /**
     * 연결되어 있는 supportType {@link OperationInputConnector}를 활성화한다.
     * 활성화된 Input Connector 에서 전달된 Input 데이터만 처리된다.
     *
     * @param supportType
     */
    boolean activateInputConector(int supportType) {
        OperationInputConnector connector = mInputConnectorMapF.get(supportType);

        if (connector != null) {
            mSupportsFlag |= supportType;
            connector.configureFromController(mAppF, OperationInputConnector.KEY_ACTIVATE, OperationInputConnector.VALUE_TRUE);
            return true;
        }

        return false;
    }

    /**
     * 연결되어 있는 supportType {@link OperationInputConnector}를 비활성화한다.
     * 비활성화된 Input Connector 에서 전달된 Input 데이터는 처리되지 않는다.
     *
     * @param supportType
     */
    boolean deactivateInputConnector(int supportType) {
        OperationInputConnector connector = mInputConnectorMapF.get(supportType);

        if (connector != null) {
            mSupportsFlag ^= supportType;
            for(int type : mInputConnectorMapF.keySet())
                if (type != supportType)
                    mSupportsFlag |= type;

            connector.configureFromController(mAppF, OperationInputConnector.KEY_ACTIVATE, OperationInputConnector.VALUE_FALSE);
            return true;
        }

        return false;
    }

    /**
     *
     * @param supportType
     * @return
     */
    boolean requestTextResultByVoiceRecognition(int supportType) {
        if ((mSupportsFlag & supportType) == supportType) {

            for (int support : mInputConnectorMapF.keySet()) {
                if ((support & supportType) == supportType) {
                    OperationInputConnector connector = mInputConnectorMapF.get(support);
                    connector.configureFromController(mAppF, SpeechInputConnector.KEY_SWITCH_MODE, SpeechController.MODE_TEXT);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 로컬저장소 & 로컬 DB & 구글 드라이브 간의 데이터 동기화 상태를 체크한다.
     * (동기적 비동기화)
     *
     * @return 작업이 수행될 경우 True, 수행되지 않을 경우 False.
     */
    boolean verifyLocalDataSync() {
        // TODO : 기본전제조건 1 = 우선순위 : Memory > Cloud App Folder <-> Local Database >= Cloud Common Folder <-> Local Archive
        // TODO : 기본전제조건 2 = 최근에 발생한 데이터 수정 내역이 우선순위가 더 높다.

        // 1. 메모리 파악.
        // 2. Google Drive 로부터 리스트 받기.
        // 3. Local Archive 로부터 데이터 리스트 받아오기.
        // 4. Local DB에 저장된 내용과 (1), (2)의 내용 비교하기.
        // 5.

        return false;
    }

    /**
     * 현 위치 근처에 등록되어 있는 AugmentedItem 목록을 조회한다.
     *
     * @return
     */
    public ArrayList<AugmentedItem> queryNearAugmentedItems() {
        double[] latestData = getSensorData(ISensorAgent.TYPE_AGENT_LOCATION);
        double radius = 0.0000005;

        return mDBCenterF.queryNearObjectsOnRealLocation(
                latestData[LocationSensorAgent.DATA_INDEX_LATITUDE],
                latestData[LocationSensorAgent.DATA_INDEX_LONGITUDE],
                latestData[LocationSensorAgent.DATA_INDEX_ALTITUDE],
                radius
        );
    }

    /**
     * 새로운 Augmented Item을 추가한다.
     * 동시에 간략한 Resource Item을 생성한다.
     *
     * @param arItem
     * @param resItem
     * @return
     */
    public long insertNewAugmentedItem(AugmentedItem arItem, ResourceItem resItem) {
        long resID = insertSimpleResourceItem(resItem);
        if (resID <= 0)
            return -1;

        LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.AUGMENTED_FIELD> builder =
                new LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.AUGMENTED_FIELD>(mDBCenterF);

        builder.set(LocalDatabaseCenter.AUGMENTED_FIELD.RES_ID, String.valueOf(resID))
                .set(LocalDatabaseCenter.AUGMENTED_FIELD.LATITUDE, String.valueOf(arItem.latitude))
                .set(LocalDatabaseCenter.AUGMENTED_FIELD.LONGITUDE, String.valueOf(arItem.longitude))
                .set(LocalDatabaseCenter.AUGMENTED_FIELD.ALTITUDE, String.valueOf(arItem.altitude))
                .set(LocalDatabaseCenter.AUGMENTED_FIELD.SUPPORT_X, String.valueOf(arItem.supportX))
                .set(LocalDatabaseCenter.AUGMENTED_FIELD.SUPPORT_Y, String.valueOf(arItem.supportY))
                .set(LocalDatabaseCenter.AUGMENTED_FIELD.SUPPORT_Z, String.valueOf(arItem.supportZ));

        return builder.insert();
    }

    /**
     * 간략한 정보만 담은 Resource Item을 추가한다.
     *
     * @param item
     * @return
     */
    long insertSimpleResourceItem(ResourceItem item) {
        LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.RESOURCE_FIELD> builder =
                new LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.RESOURCE_FIELD>(mDBCenterF);

        builder.set(LocalDatabaseCenter.RESOURCE_FIELD.NAME, item.name)
                .set(LocalDatabaseCenter.RESOURCE_FIELD.DESCRIPTION, item.description);

        long resID = builder.insert();
        if (resID > 0)
            insertTemporaryVirtualItem(resID);

        return resID;
    }

    /**
     * 처리되지 않은 임시 VirtualItem을 생성한다.
     *
     * @param resID
     * @return
     */
    long insertTemporaryVirtualItem(long resID) {
        LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.VIRTUAL_FIELD> builder =
                new LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.VIRTUAL_FIELD>(mDBCenterF);

        builder.set(LocalDatabaseCenter.VIRTUAL_FIELD.RES_ID, String.valueOf(resID))
                .set(LocalDatabaseCenter.VIRTUAL_FIELD.TYPE, "0");

        return builder.insert();
    }

    /**
     *
     * @param command
     * @param paramObj
     * @param returnObj
     * @return
     */
    boolean executeConstructedQuery(String command, JSONObject paramObj, JSONObject returnObj) {
        try {
            if (JsonKey.QUERY_ALL_VR_ITEMS.equalsIgnoreCase(command)) {
                returnObj.put(JsonKey.QUERY_RESULT, mDBCenterF.queryAllVirtualRenderings());
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 세부 사항이 담긴 JSON 객체를 읽어 INSERT 작업을 수행한다.
     *
     * @param insert
     * @param table
     * @return
     */
    boolean insertNewMetadata(JSONObject insert, String table) {
        LocalDatabaseCenter.WriteBuilder builder = makeWriteBuilder(table, insert);
        return (builder == null)? false : (builder.insert() > 0);
    }

    /**
     *
     * @param select
     * @param table
     * @return
     */
    boolean selectMetadata(JSONObject select, String table, JSONObject result) {
        LocalDatabaseCenter.ReadBuilder builder = makeReadBuilder(table, select);

        try {
            JSONArray array = new JSONArray();

            Cursor cursor = builder.select();
            while (cursor.moveToNext()) {
                JSONObject row = new JSONObject();

                if (LocalDatabaseCenter.TABLE_RESOURCE.equalsIgnoreCase(table)) {
                    LocalDatabaseCenter.RESOURCE_FIELD[] fields = LocalDatabaseCenter.RESOURCE_FIELD.values();
                    for (int i=0; i<fields.length; i++)
                        row.put(fields[i].name(), cursor.getString(i));

                } else if (LocalDatabaseCenter.TABLE_AUGMENTED.equalsIgnoreCase(table)) {
                    LocalDatabaseCenter.AUGMENTED_FIELD[] fields = LocalDatabaseCenter.AUGMENTED_FIELD.values();
                    for (int i=0; i<fields.length; i++)
                        row.put(fields[i].name(), cursor.getString(i));

                } else if (LocalDatabaseCenter.TABLE_VIRTUAL.equalsIgnoreCase(table)) {
                    LocalDatabaseCenter.VIRTUAL_FIELD[] fields = LocalDatabaseCenter.VIRTUAL_FIELD.values();
                    for (int i=0; i<fields.length; i++)
                        row.put(fields[i].name(), cursor.getString(i));
                }

                array.put(row);
            }
            cursor.close();

            result.put(IControllerCommands.JsonKey.QUERY_RESULT, array);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 세부 사항이 담긴 JSON 객체를 읽어 UPDATE 작업을 수행한다.
     *
     * @param update
     * @param table
     * @return
     */
    boolean updateMetadata(JSONObject update, String table) {
        LocalDatabaseCenter.WriteBuilder builder = makeWriteBuilder(table, update);
        return (builder == null)? false : builder.modify();
    }

    /**
     * 세부 사항이 담긴 JSON 객체를 읽어 DELETE 작업을 수행한다.
     *
     * @param delete
     * @param table
     * @return
     */
    boolean deleteMetadata(JSONObject delete, String table) {
        LocalDatabaseCenter.WriteBuilder builder = makeWriteBuilder(table, delete);
        return (builder == null)? false : builder.delete();
    }

    /**
     * JSON 객체를 파싱하여
     * {@link kr.poturns.virtualpalace.controller.LocalDatabaseCenter.ReadBuilder}로 변환한다.
     *
     * @param table 처리할 Table 명.
     * @param elements 명령에 대한 세부내용이 담긴 JSON 객체. (null일 경우, return null)
     * @return
     */
    private LocalDatabaseCenter.ReadBuilder makeReadBuilder(String table, JSONObject elements) {
        LocalDatabaseCenter.ReadBuilder builder = null;
        if (LocalDatabaseCenter.TABLE_VIRTUAL.equals(table))
            builder = new LocalDatabaseCenter.ReadBuilder<LocalDatabaseCenter.VIRTUAL_FIELD>(mDBCenterF);

        else if (LocalDatabaseCenter.TABLE_AUGMENTED.equals(table))
            builder = new LocalDatabaseCenter.ReadBuilder<LocalDatabaseCenter.AUGMENTED_FIELD>(mDBCenterF);

        else if (LocalDatabaseCenter.TABLE_RESOURCE.equals(table))
            builder = new LocalDatabaseCenter.ReadBuilder<LocalDatabaseCenter.RESOURCE_FIELD>(mDBCenterF);

        if (builder == null || elements == null)
            return null;


        Iterator<String> elementIterator = elements.keys();
        while (elementIterator.hasNext()) {
            String element = elementIterator.next();

            try {
                if (JsonKey.SET.equalsIgnoreCase(element)) {
                    JSONArray array = elements.getJSONArray(element);
                    for (int i=0; i<array.length(); i++) {
                        LocalDatabaseCenter.IField field = getField(table, array.getString(i));
                        builder.set(field, null);
                    }
                    continue;
                }

                JSONObject items = elements.getJSONObject(element);
                Iterator<String> itemIterator = items.keys();

                // WHERE BETWEEN
                if (JsonKey.WHERE_FROM.equalsIgnoreCase(element)) {
                    JSONObject dest = elements.getJSONObject(JsonKey.WHERE_TO);

                    // 조건 내 (필드 : 값) Pair 단위 처리.
                    while (itemIterator.hasNext()) {
                        String item = itemIterator.next();
                        builder.whereBetween(getField(table, item), items.getString(item), dest.getString(item));
                    }
                } else if (JsonKey.WHERE_TO.equalsIgnoreCase(element)) {
                    // IControllerCommands.JsonKey.WHERE_FROM 에서 한번에 처리.
                    continue;
                }
                // OTHER CLAUSES
                else {
                    // 조건 내 (필드 : 값) Pair 단위 처리.
                    while (itemIterator.hasNext()) {
                        String item = itemIterator.next();

                        LocalDatabaseCenter.IField field = getField(table, item);
                        if (field == null)
                            // 찾을 수 없는 필드명은 제외.
                            continue;

                        String value = items.getString(item);
                        if (JsonKey.WHERE.equalsIgnoreCase(element)) {
                            builder.whereEqual(field, value);

                        } else if (JsonKey.WHERE_NOT.equalsIgnoreCase(element)) {
                            builder.whereNotEqual(field, value);

                        } else if (JsonKey.WHERE_GREATER.equalsIgnoreCase(element)) {
                            builder.whereGreaterThan(field, value, items.optBoolean(JsonKey.ALLOW_EQUAL, false));

                        } else if (JsonKey.WHERE_SMALLER.equalsIgnoreCase(element)) {
                            builder.whereSmallerThan(field, value, items.optBoolean(JsonKey.ALLOW_EQUAL, false));

                        } else if (JsonKey.WHERE_LIKE.equalsIgnoreCase(element)) {
                            builder.whereLike(field, value);
                        }
                    }
                }

            } catch (JSONException e) {
                // JSONException 예외가 발생하는 조건은 처리하지 않는다.
            }
        }
        return builder;
    }

    /**
     * JSON 객체를 파싱하여
     * {@link kr.poturns.virtualpalace.controller.LocalDatabaseCenter.WriteBuilder}로 변환한다.
     *
     * @param table 처리할 Table 명.
     * @param elements 명령에 대한 세부내용이 담긴 JSON 객체. (null일 경우, return null)
     * @return
     */
    private LocalDatabaseCenter.WriteBuilder makeWriteBuilder(String table, JSONObject elements) {
        LocalDatabaseCenter.WriteBuilder builder = null;
        if (LocalDatabaseCenter.TABLE_VIRTUAL.equals(table))
            builder = new LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.VIRTUAL_FIELD>(mDBCenterF);

        else if (LocalDatabaseCenter.TABLE_AUGMENTED.equals(table))
            builder = new LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.AUGMENTED_FIELD>(mDBCenterF);

        else if (LocalDatabaseCenter.TABLE_RESOURCE.equals(table))
            builder = new LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.RESOURCE_FIELD>(mDBCenterF);

        if (builder == null || elements == null)
            return null;


        Iterator<String> elementIterator = elements.keys();
        while (elementIterator.hasNext()) {
            String element = elementIterator.next();

            try {
                JSONObject items = elements.getJSONObject(element);
                Iterator<String> itemIterator = items.keys();

                // WHERE BETWEEN
                if (JsonKey.WHERE_FROM.equalsIgnoreCase(element)) {
                    JSONObject dest = elements.getJSONObject(JsonKey.WHERE_TO);

                    // 조건 내 (필드 : 값) Pair 단위 처리.
                    while (itemIterator.hasNext()) {
                        String item = itemIterator.next();
                        builder.whereBetween(getField(table, item), items.getString(item), dest.getString(item));
                    }
                } else if (JsonKey.WHERE_TO.equalsIgnoreCase(element)) {
                    // IControllerCommands.JsonKey.WHERE_FROM 에서 한번에 처리.
                    continue;
                }

                // OTHER CLAUSES
                else {
                    // 조건 내 (필드 : 값) Pair 단위 처리.
                    while (itemIterator.hasNext()) {
                        String item = itemIterator.next();

                        LocalDatabaseCenter.IField field = getField(table, item);
                        if (field == null)
                            // 찾을 수 없는 필드명은 제외.
                            continue;

                        String value = items.getString(item);
                        if (JsonKey.SET.equalsIgnoreCase(element)) {
                            builder.set(field, value);

                        } else if (JsonKey.WHERE.equalsIgnoreCase(element)) {
                            builder.whereEqual(field, value);

                        } else if (JsonKey.WHERE_NOT.equalsIgnoreCase(element)) {
                            builder.whereNotEqual(field, value);

                        } else if (JsonKey.WHERE_GREATER.equalsIgnoreCase(element)) {
                            builder.whereGreaterThan(field, value, items.optBoolean(JsonKey.ALLOW_EQUAL, false));

                        } else if (JsonKey.WHERE_SMALLER.equalsIgnoreCase(element)) {
                            builder.whereSmallerThan(field, value, items.optBoolean(JsonKey.ALLOW_EQUAL, false));

                        } else if (JsonKey.WHERE_LIKE.equalsIgnoreCase(element)) {
                            builder.whereLike(field, value);
                        }
                    }
                }

            } catch (JSONException e) {
                // JSONException 예외가 발생하는 조건은 처리하지 않는다.
            }
        }

        return builder;
    }

    /**
     * 특정 Table 에서 name 값의 이름을 갖는 필드를 반환한다.
     *
     * @param table
     * @param name
     * @return
     */
    private LocalDatabaseCenter.IField getField(String table, String name) {
        final String NAME = name.toUpperCase();
        try {
            if (LocalDatabaseCenter.TABLE_VIRTUAL.equals(table)) {
                return LocalDatabaseCenter.VIRTUAL_FIELD.valueOf(NAME);

            } else if (LocalDatabaseCenter.TABLE_AUGMENTED.equals(table)) {
                return LocalDatabaseCenter.AUGMENTED_FIELD.valueOf(NAME);

            } else if (LocalDatabaseCenter.TABLE_RESOURCE.equals(table)) {
                return LocalDatabaseCenter.RESOURCE_FIELD.valueOf(NAME);
            }

        } catch (IllegalArgumentException e) { ; }
        return null;
    }

    /**
     * @return
     */
    boolean executeBackUp() {
        mDBCenterF.backUp(mDriveAssistantF);
        return false;
    }




    // * * * G E T T E R S & S E T T E R S * * * //

    /**
     *
     * @param sensorType
     * @return
     */
    protected double[] getSensorData(int sensorType) {
        InfraDataService service = mAppF.getInfraDataService();

        if (service != null) {
            BaseSensorAgent agent = service.getSensorAgent(sensorType);

            if (agent != null)
                return agent.getLatestData();
        }

        return null;
    }

    /**
     *
     * @return
     */
    protected Integer[] listEnabledInputType() {
        Integer[] list = new Integer[mInputConnectorMapF.size()];
        mInputConnectorMapF.keySet().toArray(list);
        return list;
    }

    /**
     *
     * @param inputType
     * @return
     */
    protected boolean isActivatedInputType(int inputType) {
        return (mSupportsFlag & inputType) == inputType;
    }

}
