package kr.poturns.virtualpalace.sensor;

import android.util.Pair;

/**
 * <b> 백그라운드에서 연산에 필요한 기반 센서 데이터를 수집하기 위한 모듈을 구현한다. </b>
 *
 * @author YeonhoKim
 */
public abstract class BaseSensorAgent implements ISensorAgent {

    // * * * C O N S T A N T S * * * //
    public static final int DATA_INDEX_TIMESTAMP = 0;

    public interface OnDataCollaborationListener {
        /**
         *
         * @param thisType
         * @param targetType
         * @param thisData
         * @param targetData
         */
        void onCollaboration(int thisType, int targetType, double[] thisData, double[] targetData);
    }

    // * * * F I E L D S * * * //
    protected Pair<BaseSensorAgent, OnDataCollaborationListener>[] mCollaborationArray;

    protected long mLatestMeasuredTimestamp;

    protected boolean isListening;


    // * * * C O N S T R U C T O R S * * * //
    public BaseSensorAgent() {
        // AgentType 값이 1부터 시작함.
        mCollaborationArray = new Pair[TYPE_TOTAL_COUNT + 1];
    }


    // * * * I N H E R I T S * * * //
    @Override
    public void startListening() {
        isListening = true;
    }

    @Override
    public void stopListening() {
        isListening = false;
    }


    // * * * M E T H O D S * * * //
    public synchronized final void start() {
        if (!isListening)
            startListening();
    }

    public synchronized final void stop() {
        if (isListening)
            stopListening();
    }

    public final boolean isListening() {
        return isListening;
    }

    /**
     *
     * @param agent
     * @param listener
     */
    public void setCollaborationWith(BaseSensorAgent agent, OnDataCollaborationListener listener) {
        // 동일한 Agent Type 의 값은 처리하지 않는다.
        if (agent == null || agent.getAgentType() == getAgentType())
            return;

        int agentType = agent.getAgentType();
        mCollaborationArray[agentType] = new Pair<BaseSensorAgent, OnDataCollaborationListener>(agent, listener);
    }

    /**
     *
     */
    protected void onDataMeasured() {
        for (Pair<BaseSensorAgent, OnDataCollaborationListener> pair : mCollaborationArray) {
            if (pair == null || pair.second == null)
                continue;

            BaseSensorAgent agent = pair.first;
            OnDataCollaborationListener listener = pair.second;

            listener.onCollaboration(getAgentType(), agent.getAgentType(), getLatestData(), agent.getLatestData());
        }

        onCollaborationDone();
    }

    /**
     *
     */
    protected void onCollaborationDone() {  }

    /**
     *
     * @return
     */
    public abstract double[] getLatestData();

}
