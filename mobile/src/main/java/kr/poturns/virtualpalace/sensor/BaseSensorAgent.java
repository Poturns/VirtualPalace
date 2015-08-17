package kr.poturns.virtualpalace.sensor;

import android.util.Pair;

/**
 * 백그라운드에서 연산에 필요한 기반 센서 데이터를 수집하기 위한 모듈을 구현한다.
 *
 * @author YeonhoKim
 */
public abstract class BaseSensorAgent implements ISensorAgent {

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

    protected Pair<BaseSensorAgent, OnDataCollaborationListener>[] mCollaborationArray;

    protected long mLatestMeasuredTimestamp;

    protected boolean isListening;

    public BaseSensorAgent() {
        final int countAgent = 5;

        // AgentType 값이 1부터 시작함.
        mCollaborationArray = new Pair[countAgent + 1];
    }

    public synchronized final void start() {
        if (!isListening)
            startListening();
    }

    public synchronized final void stop() {
        if (isListening)
            stopListening();
    }

    @Override
    public void startListening() {
        isListening = true;
    }

    @Override
    public void stopListening() {
        isListening = false;
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
        int agentType = agent.getAgentType();

        // 동일한 Type의 값은 처리하지 않는다.
        if (getAgentType() == agentType)
            return;

        mCollaborationArray[agentType] = new Pair<BaseSensorAgent, OnDataCollaborationListener>(agent, listener);
    }

    /**
     *
     */
    protected void onDataMeasured() {
        for (Pair<BaseSensorAgent, OnDataCollaborationListener> pair : mCollaborationArray) {
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
