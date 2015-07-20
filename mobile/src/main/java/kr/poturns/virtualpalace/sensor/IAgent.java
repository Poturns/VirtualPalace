package kr.poturns.virtualpalace.sensor;

/**
 * Created by YeonhoKim on 2015-07-20.
 */
public interface IAgent {
    enum AgentType {
        ACCELEROMETER,
        BATTERY,
        GYROSCOPE,
        LOCATION,
        NETWORK
    }

    // http://developer.android.com/guide/topics/sensors/sensors_motion.html 참조할 것.

    void startListening();

    void stopListening();

    AgentType getAgentType();
}
