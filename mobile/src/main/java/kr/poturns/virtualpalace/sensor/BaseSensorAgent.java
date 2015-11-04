package kr.poturns.virtualpalace.sensor;

import android.util.Pair;

import java.util.HashMap;

/**
 * <b> 백그라운드에서 연산에 필요한 기반 센서 데이터를 수집하기 위한 모듈을 구현한다. </b>
 *
 * @author YeonhoKim
 */
public abstract class BaseSensorAgent implements ISensorAgent {

    // * * * C O N S T A N T S * * * //
    public static final int DATA_INDEX_TIMESTAMP = 0;

    /**
     * Sensor Data Collaboration Listener
     */
    public interface OnDataCollaborationListener {
        /**
         * @param thisType 현 Agent Type
         * @param targetType Collaboration 작업을 등록한 Agent Type
         * @param thisData 현 Agent 데이터
         * @param targetData Collaboration 작업을 등록한 Agent 데이터
         */
        void onCollaboration(int thisType, int targetType, double[] thisData, double[] targetData);
    }

    // * * * F I E L D S * * * //
    protected HashMap<Integer, Pair<BaseSensorAgent, OnDataCollaborationListener>> mCollaborationMap;

    protected long mLatestMeasuredTimestamp;

    protected boolean isListening;

    protected boolean isActivated = false;


    // * * * C O N S T R U C T O R S * * * //
    public BaseSensorAgent() {
        mCollaborationMap = new HashMap<Integer, Pair<BaseSensorAgent, OnDataCollaborationListener>>(ISensorAgent.TYPE_TOTAL_COUNT);
    }


    // * * * I N H E R I T S * * * //
    @Override
    public synchronized void startListening() {
        // flag 전환이 기대한 대로 동작하기 위해서 synchronized 사용.
        isListening = true;
    }

    @Override
    public synchronized void stopListening() {
        // flag 전환이 기대한 대로 동작하기 위해서 synchronized 사용.
        isListening = false;
    }


    // * * * M E T H O D S * * * //
    public synchronized final void start() {
        if (!isListening && isActivated)
            startListening();
    }

    public synchronized final void stop() {
        if (isListening)
            stopListening();
    }

    public final boolean isListening() {
        return isListening;
    }

    public void setActivation(boolean activation) {
        this.isActivated = activation;
        if (! activation)
            stop();
    }

    /**
     * 해당 Sensor Agent 와의 Collaboration 작업을 등록한다.
     *
     * @param agent
     * @param listener
     */
    public void setCollaborationWith(BaseSensorAgent agent, OnDataCollaborationListener listener) {
        // 동일한 Agent Type 의 값은 처리하지 않는다.
        if (agent == null || agent.getAgentType() == getAgentType())
            return;

        mCollaborationMap.put(
                agent.getAgentType(),
                new Pair<BaseSensorAgent, OnDataCollaborationListener>(agent, listener)
        );
    }

    /**
     * 본 Sensor Agent 에서 데이터가 측정되었을 때 호출된다.
     * Collaboration 등록된 Sensor Agent 에게 측정된 데이터를 전달한다.
     */
    protected void onDataMeasured() {
        for (Pair<BaseSensorAgent, OnDataCollaborationListener> pair : mCollaborationMap.values()) {
            // 등록된 Collaboration Sensor Agent 순회
            if (pair == null || pair.second == null)
                continue;

            BaseSensorAgent agent = pair.first;
            OnDataCollaborationListener listener = pair.second;

            // Collaboration 전달.
            listener.onCollaboration(
                    getAgentType(),
                    agent.getAgentType(),
                    getLatestData(),
                    agent.getLatestData()
            );
        }

        onCollaborationDone();
    }

    /**
     * Collaboration 작업이 끝났을 때 호출된다.
     */
    protected void onCollaborationDone() {
        // OVERRIDE ME
    }

    /**
     * 측정된 최신 데이터를 반환한다.
     *
     * @return
     */
    public abstract double[] getLatestData();

}
