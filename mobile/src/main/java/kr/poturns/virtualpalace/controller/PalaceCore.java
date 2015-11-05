package kr.poturns.virtualpalace.controller;

import android.database.Cursor;
import android.text.format.DateFormat;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import kr.poturns.virtualpalace.InfraDataService;
import kr.poturns.virtualpalace.augmented.AugmentedInput;
import kr.poturns.virtualpalace.controller.data.AugmentedTable;
import kr.poturns.virtualpalace.controller.data.IProtocolKeywords;
import kr.poturns.virtualpalace.controller.data.ITable;
import kr.poturns.virtualpalace.controller.data.ResourceItem;
import kr.poturns.virtualpalace.controller.data.ResourceTable;
import kr.poturns.virtualpalace.controller.data.VRContainerTable;
import kr.poturns.virtualpalace.controller.data.VirtualTable;
import kr.poturns.virtualpalace.input.IProcessorCommands;
import kr.poturns.virtualpalace.input.IProcessorCommands.JsonKey;
import kr.poturns.virtualpalace.input.OperationInputConnector;
import kr.poturns.virtualpalace.inputmodule.speech.SpeechController;
import kr.poturns.virtualpalace.inputmodule.speech.SpeechInputConnector;
import kr.poturns.virtualpalace.sensor.BaseSensorAgent;
import kr.poturns.virtualpalace.sensor.ISensorAgent;
import kr.poturns.virtualpalace.sensor.LocationSensorAgent;
import kr.poturns.virtualpalace.util.DriveAssistant;
import kr.poturns.virtualpalace.util.DriveRestAssistant;


/**
 * <b> INTERNAL CONTROLLER : 컨트롤러의 관리 기능을 다룬다 </b>
 * <p>데이터 처리 (DB + Archive + Drive)
 * </p>
 *
 * <p>Input Connector 관리
 * <ol>
 *  <li>각 Input Connector에서 GlobalApplication을 통해 Controller에 Connector 등록 요청을 한다. {@link #attachInputConnector(int, OperationInputConnector)}</li>
 *  <li>Controller에서 해당 Input Connector를 활성화하여 Input 전달을 처리한다. {@link #activateInputConnector(int)}</li>
 *  <li>연결되었으나 활성화되지 않았을 경우, 해당 Input Connector로부터 발생하는 Input은 처리되지 않는다.</li>
 *  <li>{@link #deactivateInputConnector(int)}를 통해 해당 Input Connector는 비활성화 될 수 있다.</li>
 *  <li>{@link #detachInputConnector(int)}를 통해 해당 Input Connector는 등록해제 될 수 있다.</li>
 * </ol>
 * </p>
 *
 *
 * @author Yeonho.Kim
 */
abstract class PalaceCore {

    // * * * C O N S T A N T S * * * //
    protected final PalaceApplication App;
    protected final TreeMap<Integer, OperationInputConnector> AttachedInputConnectorMap;

    private final LocalArchive Archive;
    protected final LocalDatabaseCenter DBCenter;
    protected final DriveAssistant AppDriveAssistant;
    protected final DriveRestAssistant GlobalDriveAssistant;
    private final TreeMap<Long, OnPlayModeListener> PlayModeListeners;


    // * * * F I E L D S * * * //
    protected OnPlayModeListener.PlayMode mCurrentMode;
    protected boolean isOnCardboard;
    protected int mActivatedConnectorSupportFlag;


    // * * * C O N S T R U C T O R S * * * //
    protected PalaceCore(PalaceApplication application) {
        App = application;

        // DATA Part.
        Archive = LocalArchive.getInstance(application);
        DBCenter = LocalDatabaseCenter.getInstance(application);
        AppDriveAssistant = new DriveAssistant(application);
        GlobalDriveAssistant = new DriveRestAssistant(application);

        // INPUT Part.
        AttachedInputConnectorMap = new TreeMap<Integer, OperationInputConnector>();
        mActivatedConnectorSupportFlag = IProcessorCommands.TYPE_INPUT_SUPPORT_SCREENTOUCH;

        PlayModeListeners = new TreeMap<Long, OnPlayModeListener>();
        PlayModeListeners.put(0L, new OnPlayModeListener() {
            @Override
            public void onAttached(Long attachedKey) {
                ;
            }

            @Override
            public void onDetached() {
                ;
            }

            @Override
            public void onPlayModeChanged(PlayMode mode, boolean onCardboard) {
                mCurrentMode = mode;
                isOnCardboard = onCardboard;
            }
        });

        mCurrentMode = OnPlayModeListener.PlayMode.STANDARD;
        isOnCardboard = false;
    }

    protected void destroy() {

    }


    // * * * A B S T R A C T * * * //
    /**
     * Unity 에 발생한 이벤트를 전달한다.
     *
     * @param event 발생시킬 이벤트 명
     * @param contents 이벤트 세부 내용
     */
    protected abstract void dispatchEvent(String event, JSONObject contents);


    // * * * M E T H O D S * * * //
    /**
     *
     * @param listener
     */
    @Deprecated
    void attachOnPlayModeChangedListener(OnPlayModeListener listener) {
        if (listener == null)
            return ;

        long key = System.currentTimeMillis();
        PlayModeListeners.put(key, listener);
        listener.onAttached(key);
    }

    /**
     *
     * @param key
     */
    @Deprecated
    void detachOnPlayModeChangedListener(long key) {
        if (key == 0)
            return ;

        PlayModeListeners.remove(key).onDetached();
    }

    /**
     *
     * @param mode
     */
    @Deprecated
    boolean switchMode(OnPlayModeListener.PlayMode mode, boolean onCardboard) {
        if (mCurrentMode == mode && isOnCardboard == onCardboard)
            return false;

        for(OnPlayModeListener listener : PlayModeListeners.values()) {
            listener.onPlayModeChanged(mode, onCardboard);
        }
        return true;
    }



    // * * * I N P U T _ P A R T . * * * //
    /**
     * {@link OperationInputConnector}를 Controller 에 등록한다.
     * 연결할 경우, 자동으로 활성화 하지 않는다.
     *
     * @param connector Connector Support Type
     * @param supportType Connector Instance
     */
    void attachInputConnector(int supportType, OperationInputConnector connector) {
        // 동일 SupportType 에서 이미 연결되어 있는 경우, 해제한다.
        OperationInputConnector attached = AttachedInputConnectorMap.get(supportType);
        if (attached != null) {
            attached.configureFromController(App, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_FALSE);
        }

        // 동일 SupportType 은 새로운 Connector 로 연결된다.
        if (connector != null) {
            AttachedInputConnectorMap.put(supportType, connector);
            connector.configureFromController(App, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_TRUE);

            JSONObject content = new JSONObject();
            try {
                content.put(IProtocolKeywords.Event.KEY_TOAST_MESSAGE_TYPE, "success");
                content.put(IProtocolKeywords.Event.KEY_TOAST_MESSAGE_MSG, "입력[" + supportType + "]가 준비되었습니다.");

                dispatchEvent(IProtocolKeywords.Event.EVENT_TOAST_MESSAGE, content);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@link OperationInputConnector}를 Controller 에서 등록해제한다.
     * 해제할 경우, 자동으로 비활성화 한다.
     *
     * @param supportType Connector Support Type
     */
    void detachInputConnector(int supportType) {
        if (deactivateInputConnector(supportType)) {
            // 연결해제 작업을 수행하도록 한다.
            OperationInputConnector connector = AttachedInputConnectorMap.remove(supportType);
            connector.configureFromController(App, OperationInputConnector.KEY_ENABLE, OperationInputConnector.VALUE_FALSE);

            JSONObject content = new JSONObject();
            try {
                content.put(IProtocolKeywords.Event.KEY_TOAST_MESSAGE_TYPE, "fail");
                content.put(IProtocolKeywords.Event.KEY_TOAST_MESSAGE_MSG, "입력[" + supportType + "]가 연결 해제되었습니다.");

                dispatchEvent(IProtocolKeywords.Event.EVENT_TOAST_MESSAGE, content);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
    }

    /**
     * 연결되어 있는 supportType {@link OperationInputConnector}를 활성화한다.
     * 활성화된 Input Connector 에서 전달된 Input 데이터만 처리된다.
     *
     * Major Input 타입에서는 동시에 하나의 Support Type 만 활성화된다.
     * 따라서 기존에 활성화되어 있던 SupportType 은 비활성되고, 새로운 SupportType 이 활성화된다.
     *
     * @param supportType
     */
    boolean activateInputConnector(int supportType) {
        OperationInputConnector connector = AttachedInputConnectorMap.get(supportType);

        boolean result = (connector != null);
        if (result) {
            if (supportType < IProcessorCommands.TYPE_INPUT_SUPPORT_MAJOR_LIMIT) {
                int activatedType = mActivatedConnectorSupportFlag % IProcessorCommands.TYPE_INPUT_SUPPORT_MAJOR_LIMIT;
                // 기존 활성화되어 있던 Major Support Type 비활성화.
                deactivateInputConnector(activatedType);
            }

            mActivatedConnectorSupportFlag |= supportType;
            connector.configureFromController(App, OperationInputConnector.KEY_ACTIVATE, OperationInputConnector.VALUE_TRUE);

            JSONObject content = new JSONObject();
            try {
                content.put(String.valueOf(supportType), true);
                dispatchEvent(IProtocolKeywords.Event.EVENT_INPUTMODE_CHANGED, content);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 연결되어 있는 supportType {@link OperationInputConnector}를 비활성화한다.
     * 비활성화된 Input Connector 에서 전달된 Input 데이터는 처리되지 않는다.
     *
     * @param supportType
     */
    boolean deactivateInputConnector(int supportType) {
        OperationInputConnector connector = AttachedInputConnectorMap.get(supportType);

        boolean result = (connector != null);
        if (result) {
            mActivatedConnectorSupportFlag ^= supportType;
            connector.configureFromController(App, OperationInputConnector.KEY_ACTIVATE, OperationInputConnector.VALUE_FALSE);

            JSONObject content = new JSONObject();
            try {
                content.put(String.valueOf(supportType), false);
                dispatchEvent(IProtocolKeywords.Event.EVENT_INPUTMODE_CHANGED, content);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    boolean requestSpeechDetection(String mode, String action) {
        if (isActivatedInputType(IProcessorCommands.TYPE_INPUT_SUPPORT_VOICE)) {
            OperationInputConnector connector = AttachedInputConnectorMap.get(IProcessorCommands.TYPE_INPUT_SUPPORT_VOICE);

            JSONObject returnObject = new JSONObject();
            try {
                returnObject.put(IProtocolKeywords.Request.KEY_USE_SPEECH_MODE, mode);
            } catch (JSONException e) { ; }

            if (IProtocolKeywords.Request.KEY_USE_SPEECH_ACTION_START.equalsIgnoreCase(action)) {
                connector.configureFromController(App, SpeechInputConnector.KEY_SWITCH_MODE,
                        IProtocolKeywords.Request.KEY_USE_SPEECH_MODE_COMMAND.equalsIgnoreCase(mode) ?
                                SpeechController.MODE_COMMAND : SpeechController.MODE_TEXT);

                connector.configureFromController(App, SpeechInputConnector.KEY_ACTIVE_RECOGNIZE, SpeechInputConnector.VALUE_TRUE);
                dispatchEvent(IProtocolKeywords.Event.EVENT_SPEECH_STARTED, returnObject);
                return true;

            } else if (IProtocolKeywords.Request.KEY_USE_SPEECH_ACTION_STOP.equalsIgnoreCase(action)) {
                connector.configureFromController(App, SpeechInputConnector.KEY_ACTIVE_RECOGNIZE, SpeechInputConnector.VALUE_FALSE);
                dispatchEvent(IProtocolKeywords.Event.EVENT_SPEECH_ENDED, returnObject);
                return true;
            }
        }

        return false;
    }


    // * * * D A T A _ P A R T . * * * //
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
     * 현 위치 근처에 등록되어 있는 AugmentedInput 목록을 조회한다.
     *
     * @return
     */
    public ArrayList<AugmentedInput> queryNearAugmentedItems() {
        double[] latestData = getSensorData(ISensorAgent.TYPE_AGENT_LOCATION);
        double radius = 0.0000005;

        return DBCenter.queryNearObjectsOnRealLocation(
                latestData[LocationSensorAgent.DATA_INDEX_LATITUDE],
                latestData[LocationSensorAgent.DATA_INDEX_LONGITUDE],
                latestData[LocationSensorAgent.DATA_INDEX_ALTITUDE],
                radius
        );
    }

    /**
     *
     * @param returnObject
     * @return
     */
    protected boolean queryNearAugmentedItems(JSONObject returnObject) {
        JSONArray returnArray = new JSONArray();
        try {
            for (AugmentedInput item : queryNearAugmentedItems()) {
                JSONObject each = new JSONObject();

                // TODO :

                returnArray.put(each);
            }
            returnObject.put(IProtocolKeywords.Request.KEY_CALLBACK_RETURN, returnArray);

        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    /**
     * VR Scene에서 렌더링해야할 모든 Item 목록을 반환한다.
     *
     * @param returnObject
     * @return
     */
    protected boolean queryVirtualRenderingItems(JSONObject returnObject) {
        try {
            returnObject.put(IProtocolKeywords.Request.KEY_CALLBACK_RETURN, DBCenter.queryAllVirtualRenderings());
            return true;

        } catch (Exception e) { ; }
        return false;
    }

    /**
     *
     * @param returnObject
     * @return
     */
    protected boolean queryVRContainerItems(JSONObject returnObject) {
       try {
           // SELECT ALL
           LocalDatabaseCenter.ReadBuilder builder = makeReadBuilder(ITable.TABLE_VR_CONTAINER, new JSONObject() );

           JSONArray array = new JSONArray();
           Cursor cursor = builder.select();

           while (cursor.moveToNext()) {
               JSONObject bookcase = new JSONObject();
               bookcase.put(VRContainerTable.NAME.toString(), cursor.getString(VRContainerTable.NAME.ordinal()));
               bookcase.put(VRContainerTable.Z_OFFSET.toString(), cursor.getString(VRContainerTable.Z_OFFSET.ordinal()));
               bookcase.put(VRContainerTable.COUNT.toString(), cursor.getString(VRContainerTable.COUNT.ordinal()));
               array.put(bookcase);
           }
           cursor.close();

           returnObject.put(IProtocolKeywords.Request.KEY_CALLBACK_RETURN, array);
           return true;

       } catch (Exception e) { ;}

        return false;
    }

    /**
     * 새로운 Augmented Item을 추가한다.
     * 동시에 간략한 Resource Item을 생성한다.
     *
     * @param arItem
     * @param resItem
     * @return
     */
    public long insertNewAugmentedItem(AugmentedInput arItem, ResourceItem resItem) {
        long resID = insertSimpleResourceItem(resItem);
        if (resID <= 0)
            return -1;

        LocalDatabaseCenter.WriteBuilder<AugmentedTable> builder =
                new LocalDatabaseCenter.WriteBuilder<AugmentedTable>(DBCenter);

        builder.set(AugmentedTable.RES_ID, String.valueOf(resID))
                .set(AugmentedTable.LATITUDE, String.valueOf(arItem.latitude))
                .set(AugmentedTable.LONGITUDE, String.valueOf(arItem.longitude))
                .set(AugmentedTable.ALTITUDE, String.valueOf(arItem.altitude))
                .set(AugmentedTable.SUPPORT_X, String.valueOf(arItem.supportX))
                .set(AugmentedTable.SUPPORT_Y, String.valueOf(arItem.supportY))
                .set(AugmentedTable.SUPPORT_Z, String.valueOf(arItem.supportZ));

        return builder.insert();
    }

    /**
     * 간략한 정보만 담은 Resource Item을 추가한다.
     *
     * @param item
     * @return
     */
    long insertSimpleResourceItem(ResourceItem item) {
        LocalDatabaseCenter.WriteBuilder<ResourceTable> builder =
                new LocalDatabaseCenter.WriteBuilder<ResourceTable>(DBCenter);

        builder.set(ResourceTable.TITLE, item.name)
                .set(ResourceTable.RES_TYPE, "0")
                .set(ResourceTable.CTIME, String.valueOf(System.currentTimeMillis()));

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
        LocalDatabaseCenter.WriteBuilder<VirtualTable> builder =
                new LocalDatabaseCenter.WriteBuilder<VirtualTable>(DBCenter);

        builder.set(VirtualTable.RES_ID, String.valueOf(resID))
                .set(VirtualTable.MODEL_TYPE, "0");

        return builder.insert();
    }


    /**
     * 세부 사항이 담긴 JSON 객체를 읽어 INSERT 작업을 수행한다.
     *
     * @param insert
     * @param table
     * @return
     */
    boolean insertNewLocalData(JSONObject insert, String table) {
        LocalDatabaseCenter.WriteBuilder builder = makeWriteBuilder(table, insert);
        return (builder == null)? false : (builder.insert() > 0);
    }

    /**
     *
     * @param select
     * @param table
     * @return
     */
    boolean selectLocalData(JSONObject select, String table, JSONObject result) {
        LocalDatabaseCenter.ReadBuilder builder = makeReadBuilder(table, select);

        try {
            JSONArray array = new JSONArray();

            Cursor cursor = builder.select();
            while (cursor.moveToNext()) {
                JSONObject row = new JSONObject();

                if (builder.mSetClauseValues.size() > 0) {
                    for (String key : builder.mSetClauseValues.keySet())
                        row.put(key, cursor.getString(cursor.getColumnIndex(key)));

                } else {
                    if (ITable.TABLE_RESOURCE.equalsIgnoreCase(table)) {
                        ResourceTable[] fields = ResourceTable.values();
                        for (int i=0; i<fields.length; i++)
                            row.put(fields[i].name(), cursor.getString(i));

                    } else if (ITable.TABLE_AUGMENTED.equalsIgnoreCase(table)) {
                        AugmentedTable[] fields = AugmentedTable.values();
                        for (int i=0; i<fields.length; i++)
                            row.put(fields[i].name(), cursor.getString(i));

                    } else if (ITable.TABLE_VIRTUAL.equalsIgnoreCase(table)) {
                        VirtualTable[] fields = VirtualTable.values();
                        for (int i=0; i<fields.length; i++)
                            row.put(fields[i].name(), cursor.getString(i));

                    } else if (ITable.TABLE_VR_CONTAINER.equalsIgnoreCase(table)) {
                        VRContainerTable[] fields = VRContainerTable.values();
                        for (int i=0; i<fields.length; i++)
                            row.put(fields[i].name(), cursor.getString(i));
                    }
                }

                array.put(row);
            }
            cursor.close();

            result.put(IProcessorCommands.JsonKey.QUERY_RESULT, array);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
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
    boolean updateLocalData(JSONObject update, String table) {
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
    boolean deleteLocalData(JSONObject delete, String table) {
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
        if (ITable.TABLE_VIRTUAL.equals(table))
            builder = new LocalDatabaseCenter.ReadBuilder<VirtualTable>(DBCenter);

        else if (ITable.TABLE_AUGMENTED.equals(table))
            builder = new LocalDatabaseCenter.ReadBuilder<AugmentedTable>(DBCenter);

        else if (ITable.TABLE_RESOURCE.equals(table))
            builder = new LocalDatabaseCenter.ReadBuilder<ResourceTable>(DBCenter);

        else if (ITable.TABLE_VR_CONTAINER.equals(table))
            builder = new LocalDatabaseCenter.ReadBuilder<VRContainerTable>(DBCenter);

        if (builder == null || elements == null)
            return null;

        builder.setTable(table);

        Iterator<String> elementIterator = elements.keys();
        while (elementIterator.hasNext()) {
            String element = elementIterator.next();

            try {
                if (JsonKey.SET.equalsIgnoreCase(element)) {
                    JSONArray array = elements.getJSONArray(element);
                    for (int i=0; i<array.length(); i++) {
                        ITable field = getField(table, array.getString(i));
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
                    // IProcessorCommands.JsonKey.WHERE_FROM 에서 한번에 처리.
                    continue;
                }
                // OTHER CLAUSES
                else {
                    // 조건 내 (필드 : 값) Pair 단위 처리.
                    while (itemIterator.hasNext()) {
                        String item = itemIterator.next();

                        ITable field = getField(table, item);
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
        if (ITable.TABLE_VIRTUAL.equals(table))
            builder = new LocalDatabaseCenter.WriteBuilder<VirtualTable>(DBCenter);

        else if (ITable.TABLE_AUGMENTED.equals(table))
            builder = new LocalDatabaseCenter.WriteBuilder<AugmentedTable>(DBCenter);

        else if (ITable.TABLE_RESOURCE.equals(table))
            builder = new LocalDatabaseCenter.WriteBuilder<ResourceTable>(DBCenter);

        else if (ITable.TABLE_VR_CONTAINER.equals(table))
            builder = new LocalDatabaseCenter.WriteBuilder<VRContainerTable>(DBCenter);

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
                    // IProcessorCommands.JsonKey.WHERE_FROM 에서 한번에 처리.
                    continue;
                }

                // OTHER CLAUSES
                else {
                    // 조건 내 (필드 : 값) Pair 단위 처리.
                    while (itemIterator.hasNext()) {
                        String item = itemIterator.next();

                        ITable field = getField(table, item);
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
    private ITable getField(String table, String name) {
        final String NAME = name.toUpperCase();
        try {
            if (ITable.TABLE_VIRTUAL.equals(table)) {
                return VirtualTable.valueOf(NAME);

            } else if (ITable.TABLE_AUGMENTED.equals(table)) {
                return AugmentedTable.valueOf(NAME);

            } else if (ITable.TABLE_RESOURCE.equals(table)) {
                return ResourceTable.valueOf(NAME);
            }

        } catch (IllegalArgumentException e) { ; }
        return null;
    }

    /**
     * Google Drive App Folder에 DB 파일을 백업한다.
     *
     * @return
     */
    boolean executeBackUp() {
        File dbFile = DBCenter.getDatabaseFile();
        DriveFolder folder = AppDriveAssistant.getAppFolder();

        // Drive Contents 생성
        DriveContents contents = AppDriveAssistant.newDriveContents();
        DriveAssistant.IDriveContentsApi.writeFileContents(contents, dbFile.getAbsolutePath());

        String fileName = "VirtualPalace-" + DateFormat.format("yyMMddhhmmss", System.currentTimeMillis()) + ".dbk";
        DriveFile file = AppDriveAssistant.DriveFolderApi.createFile(folder, contents, fileName, "db");
        return true;
    }



    // * * * G E T T E R S & S E T T E R S * * * //
    /**
     *
     * @param sensorType
     * @return
     */
    protected double[] getSensorData(int sensorType) {
        InfraDataService service = App.getInfraDataService();
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
    public Integer[] getAttachedInputTypeArray() {
        Integer[] list = new Integer[AttachedInputConnectorMap.size()];
        AttachedInputConnectorMap.keySet().toArray(list);

        return list;
    }

    /**
     *
     * @param inputType
     * @return
     */
    protected boolean isActivatedInputType(int inputType) {
        return (mActivatedConnectorSupportFlag & inputType) > 0;
    }

}
