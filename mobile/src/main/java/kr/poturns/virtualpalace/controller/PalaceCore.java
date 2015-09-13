package kr.poturns.virtualpalace.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.TreeMap;

import kr.poturns.virtualpalace.InfraDataService;
import kr.poturns.virtualpalace.input.IControllerCommands;
import kr.poturns.virtualpalace.input.OperationInputConnector;
import kr.poturns.virtualpalace.util.DriveAssistant;

/**
 * <b> INTERNAL CONTROLLER : 컨트롤러의 관리 기능을 다룬다 </b>
 * <p>
 *
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
    void attachOnPlayModeChangedListener(OnPlayModeListener listener) {
        if (listener == null)
            return ;

        long key = System.currentTimeMillis();
        mPlayModeListenersF.put(key, listener);
        listener.onAttached(key);
    }

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

        // 동일 SupportType 은 새로운 Connector 로 연결된다.
        if (connector != null) {
            mSupportsFlag |= supportType;
            mInputConnectorMapF.put(supportType, connector);
            connector.configureFromController(mAppF, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_TRUE);
        }
    }

    /**
     * {@link OperationInputConnector}를 Controller 에서 해제한다.
     *
     * @param supportType Connector Support Type
     */
    void detachInputConnector(int supportType) {
        OperationInputConnector connector = mInputConnectorMapF.remove(supportType);

        if (connector != null) {
            mSupportsFlag ^= supportType;
            connector.configureFromController(mAppF, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_FALSE);
        }

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
     *
     * @param key 속성
     * @param value 속성 값
     * @return
     */
    JSONArray searchMetadata(String key, String value) {
        if ("id".equalsIgnoreCase(key)) {
            return mDBCenterF.queryObjectDetailsById(Integer.parseInt(value));

        } else if ("name".equalsIgnoreCase(key)) {
            return mDBCenterF.queryObjectDetailsByName(value);

        } else if ("type".equalsIgnoreCase(key)) {
            return mDBCenterF.queryObjectDetailsByType(value);
        }

        return null;
    }

    /**
     *
     * @return
     */
    boolean insertNewMetadata(JSONObject insert, String table) {
        LocalDatabaseCenter.WriteBuilder builder = getBuilderWithParsing(table, insert);
        return (builder == null)? false : builder.insert();
    }

    /**
     *
     * @return
     */
    boolean updateMetadata(JSONObject update, String table) {
        LocalDatabaseCenter.WriteBuilder builder = getBuilderWithParsing(table, update);
        return (builder == null)? false : builder.modify();
    }

    /**
     *
     * @return
     */
    boolean deleteMetadata(JSONObject delete, String table) {
        LocalDatabaseCenter.WriteBuilder builder = getBuilderWithParsing(table, delete);
        return (builder == null)? false : builder.delete();
    }

    /**
     *
     * @param table
     * @param command
     * @return
     */
    private LocalDatabaseCenter.WriteBuilder getBuilderWithParsing(String table, JSONObject command) {
        LocalDatabaseCenter.WriteBuilder builder;
        switch (table) {
            case LocalDatabaseCenter.TABLE_VIRTUAL:
                builder = new LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.VIRTUAL_FIELD>(mDBCenterF);
                break;

            case LocalDatabaseCenter.TABLE_AUGMENTED:
                builder =  new LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.AUGMENTED_FIELD>(mDBCenterF);
                break;

            case LocalDatabaseCenter.TABLE_RESOURCE:
                builder =  new LocalDatabaseCenter.WriteBuilder<LocalDatabaseCenter.RESOURCE_FIELD>(mDBCenterF);
                break;

            default:
                return null;
        }

        Iterator<String> conditions = command.keys();
        while (conditions.hasNext()) {
            String condition = conditions.next();

            try {
                JSONObject content = command.getJSONObject(condition);
                Iterator<String> iter = content.keys();

                // WHERE BETWEEN
                if (IControllerCommands.JsonKey.WHERE_FROM.equalsIgnoreCase(condition)) {
                    JSONObject dest = command.getJSONObject(IControllerCommands.JsonKey.WHERE_TO);

                    while (iter.hasNext()) {
                        String key = iter.next();
                        builder.whereBetween(getField(table, key), content.getString(key), dest.getString(key));
                    }

                } else if (IControllerCommands.JsonKey.WHERE_TO.equalsIgnoreCase(condition)){
                    // IControllerCommands.JsonKey.WHERE_FROM 에서 한번에 처리.
                    continue;

                    // ELSE
                } else {
                    while (iter.hasNext()) {
                        String key = iter.next();
                        LocalDatabaseCenter.IField field = getField(table, key);
                        String value = content.getString(key);

                        switch (condition) {
                            case IControllerCommands.JsonKey.SET:
                                builder.set(field, value);
                                break;

                            case IControllerCommands.JsonKey.WHERE:
                                builder.whereEqual(field, value);
                                break;

                            case IControllerCommands.JsonKey.WHERE_NOT:
                                builder.whereNotEqual(field, value);
                                break;

                            case IControllerCommands.JsonKey.WHERE_GREATER:
                                builder.whereGreaterThan(field, value, (content.has(IControllerCommands.JsonKey.ALLOW_EQUAL))?
                                        content.getBoolean(IControllerCommands.JsonKey.ALLOW_EQUAL) : false);
                                break;

                            case IControllerCommands.JsonKey.WHERE_SMALLER:
                                builder.whereSmallerThan(field, value, (content.has(IControllerCommands.JsonKey.ALLOW_EQUAL))?
                                        content.getBoolean(IControllerCommands.JsonKey.ALLOW_EQUAL) : false);
                                break;

                            case IControllerCommands.JsonKey.WHERE_LIKE:
                                builder.whereLike(field, value);
                                break;
                        }
                    }
                }
            } catch (JSONException e) { }
        }

        return builder;
    }

    private LocalDatabaseCenter.IField getField(String table, String name) {
        switch (table) {
            case LocalDatabaseCenter.TABLE_VIRTUAL:
                return LocalDatabaseCenter.VIRTUAL_FIELD.valueOf(name);

            case LocalDatabaseCenter.TABLE_AUGMENTED:
                return LocalDatabaseCenter.AUGMENTED_FIELD.valueOf(name);

            case LocalDatabaseCenter.TABLE_RESOURCE:
                return LocalDatabaseCenter.RESOURCE_FIELD.valueOf(name);

            default:
                return null;
        }
    }

    /**
     *
     * @return
     */
    boolean executeBackup() {

        mDriveAssistantF.getAppFolder();
        return false;
    }




    // * * * G E T T E R S & S E T T E R S * * * //
    protected double[] getSensorData(int sensorType) {
        InfraDataService service = mAppF.getInfraDataService();
        if (service != null)
            return service.getSensorAgent(sensorType).getLatestData();

        return null;
    }

}
