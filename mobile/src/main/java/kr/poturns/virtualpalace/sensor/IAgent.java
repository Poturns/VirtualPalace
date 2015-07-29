package kr.poturns.virtualpalace.sensor;

/**
 * Sensing Data를 얻는 Agent 기능 정의를 위한 Interface.
 *
 * Created by YeonhoKim on 2015-07-20.
 */
public interface IAgent {
    /**
     * Agent Type : 가속도
     */
    public static final int TYPE_AGENT_ACCELEROMETER = 1;
    /**
     * Agent Type : 배터리
     */
    public static final int TYPE_AGENT_BATTERY = 2;
    /**
     * Agent Type : 자이로
     */
    public static final int TYPE_AGENT_GYROSCOPE = 3;
    /**
     * Agent Type : 위치
     */
    public static final int TYPE_AGENT_LOCATION = 4;
    /**
     * Agent Type : 네트워크
     */
    public static final int TYPE_AGENT_NETWORK = 5;


    // TODO:
    // http://developer.android.com/guide/topics/sensors/sensors_motion.html 참조할 것.

    /**
     * Agent Listening 시작
     */
    public void startListening();

    /**
     * Agent Listening 중지
     */
    public void stopListening();

    /**
     * Agent Type 반환
     * @return
     */
    public int getAgentType();
}
