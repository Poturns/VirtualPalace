package kr.poturns.virtualpalace.sensor;

import android.util.Pair;

/**
 * 백그라운드에서 연산에 필요한 기반 센서 데이터를 수집하기 위한 모듈을 구현한다.
 *
 * @author YeonhoKim
 */
abstract class BaseAgent implements IAgent {

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

    protected Pair<BaseAgent, OnDataCollaborationListener>[] mCollaborationArray;

    protected long mLatestMeasuredTimestamp;

    public BaseAgent() {
        final int countAgent = 5;

        // AgentType 값이 1부터 시작함.
        mCollaborationArray = new Pair[countAgent + 1];
    }

    /**
     *
     * @param agent
     * @param listener
     */
    public void setCollaborationWith(BaseAgent agent, OnDataCollaborationListener listener) {
        int agentType = agent.getAgentType();

        // 동일한 Type의 값은 처리하지 않는다.
        if (getAgentType() == agentType)
            return;

        mCollaborationArray[agentType] = new Pair<>(agent, listener);
    }

    /**
     *
     */
    protected void onDataMeasured() {
        for (Pair<BaseAgent, OnDataCollaborationListener> pair : mCollaborationArray) {
            BaseAgent agent = pair.first;
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
