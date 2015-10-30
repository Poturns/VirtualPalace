package kr.poturns.virtualpalace;

import kr.poturns.virtualpalace.sensor.AcceleroSensorAgent;
import kr.poturns.virtualpalace.sensor.BaseSensorAgent;
import kr.poturns.virtualpalace.sensor.BatterySensorAgent;
import kr.poturns.virtualpalace.sensor.GyroSensorAgent;
import kr.poturns.virtualpalace.sensor.ISensorAgent;
import kr.poturns.virtualpalace.sensor.LocationSensorAgent;
import kr.poturns.virtualpalace.sensor.MagneticSensorAgent;
import kr.poturns.virtualpalace.sensor.NetworkSensorAgent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.HashMap;

/**
 * Virtual Palace 서비스를 위해 백그라운드에서 데이터를 수집하는 Service.
 *
 * @author YeonhoKim
 */
public class InfraDataService extends Service {

    private HashMap<Integer, BaseSensorAgent> mSensorAgentMap;

    private int agentActivation = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorAgentMap = new HashMap<Integer, BaseSensorAgent>(ISensorAgent.TYPE_TOTAL_COUNT);

        // Default Agent Activation
        agentActivation =
                ISensorAgent.TYPE_AGENT_BATTERY +
                        ISensorAgent.TYPE_AGENT_NETWORK;

        initSensorAgents();
    }

    private void initSensorAgents() {
        int agentType;

        AcceleroSensorAgent acceleroAgent = new AcceleroSensorAgent(this);
        agentType = acceleroAgent.getAgentType();
        acceleroAgent.setActivation(isActivatedAgent(agentType));
        mSensorAgentMap.put(agentType, acceleroAgent);

        BatterySensorAgent batteryAgent = new BatterySensorAgent(this);
        agentType = batteryAgent.getAgentType();
        batteryAgent.setActivation(isActivatedAgent(agentType));
        mSensorAgentMap.put(agentType, batteryAgent);

        GyroSensorAgent gyroAgent = new GyroSensorAgent(this);
        agentType = gyroAgent.getAgentType();
        gyroAgent.setActivation(isActivatedAgent(agentType));
        mSensorAgentMap.put(agentType, gyroAgent);

        LocationSensorAgent locationAgent = new LocationSensorAgent(this);
        agentType = locationAgent.getAgentType();
        locationAgent.setActivation(isActivatedAgent(agentType));
        mSensorAgentMap.put(agentType, locationAgent);

        NetworkSensorAgent networkAgent = new NetworkSensorAgent(this);
        agentType = networkAgent.getAgentType();
        networkAgent.setActivation(isActivatedAgent(agentType));
        mSensorAgentMap.put(agentType, networkAgent);

        MagneticSensorAgent magneticAgent = new MagneticSensorAgent(this);
        agentType = magneticAgent.getAgentType();
        magneticAgent.setActivation(isActivatedAgent(agentType));
        mSensorAgentMap.put(agentType, magneticAgent);

        // Collaboration 등록
        acceleroAgent.setCollaborationWith(magneticAgent);
        locationAgent.setCollaborationWith(batteryAgent);
        locationAgent.setCollaborationWith(networkAgent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startListeningActivated();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        stopListeningActivated();
        super.onDestroy();
    }

    public void startListeningActivated() {
        for (BaseSensorAgent agent : mSensorAgentMap.values())
            agent.start();
    }

    public void stopListeningActivated() {
        for (BaseSensorAgent agent : mSensorAgentMap.values())
            agent.stop();
    }

    /**
     * AR Mode에서 사용하는 Sensor 데이터를 수집하기 위한 Agent 를 구동한다.
     */
    public void startListeningForAR() {
        int[] arSensorArray = {
                ISensorAgent.TYPE_AGENT_ACCELEROMETER,
                ISensorAgent.TYPE_AGENT_LOCATION,
                ISensorAgent.TYPE_AGENT_MAGNETIC
        };

        for (int agentType : arSensorArray) {
            activateSensorAgent(agentType);
            getSensorAgent(agentType).start();
        }
    }

    /**
     * AR Mode에서 사용하는 Sensor 데이터를 수집하기 위한 Agent 를 정지한다.
     */
    public void stopListeningFromAR() {
        int[] arSensorArray = {
                ISensorAgent.TYPE_AGENT_ACCELEROMETER,
                ISensorAgent.TYPE_AGENT_LOCATION,
                ISensorAgent.TYPE_AGENT_MAGNETIC
        };

        for (int agentType : arSensorArray) {
            activateSensorAgent(agentType);
            getSensorAgent(agentType).stop();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinderF;
    }

    private final LocalBinder mBinderF  = new LocalBinder();

    public class LocalBinder extends Binder {
        public InfraDataService getService() {
            return InfraDataService.this;
        }
    }

    /**
     * 해당 AgentType에 해당하는 SensorAgent 를 활성화한다.
     *
     * @param agentType
     */
    public void activateSensorAgent(int agentType) {
        switch (agentType) {
            case ISensorAgent.TYPE_AGENT_ACCELEROMETER:
            case ISensorAgent.TYPE_AGENT_BATTERY:
            case ISensorAgent.TYPE_AGENT_GYROSCOPE:
            case ISensorAgent.TYPE_AGENT_LOCATION:
            case ISensorAgent.TYPE_AGENT_NETWORK:
            case ISensorAgent.TYPE_AGENT_MAGNETIC:
                getSensorAgent(agentType).setActivation(true);
                agentActivation |= agentType;
                break;
        }
    }

    /**
     * 해당 AgentType 에 해당하는 SensorAgent 를 비활성화 및 정지한다.
     *
     * @param agentType
     */
    public void deactivateSensorAgent(int agentType) {
        switch (agentType) {
            case ISensorAgent.TYPE_AGENT_ACCELEROMETER:
            case ISensorAgent.TYPE_AGENT_BATTERY:
            case ISensorAgent.TYPE_AGENT_GYROSCOPE:
            case ISensorAgent.TYPE_AGENT_LOCATION:
            case ISensorAgent.TYPE_AGENT_NETWORK:
            case ISensorAgent.TYPE_AGENT_MAGNETIC:
                getSensorAgent(agentType).setActivation(false);
                agentActivation ^= agentType;
                break;
        }
    }

    /**
     * 해당 AgentType에 해당하는 SensorAgent 가 활성화되어있는지 확인한다.
     *
     * @param agentType
     * @return true/false
     */
    public boolean isActivatedAgent(int agentType) {
        return (agentActivation & agentType) == agentType;
    }

    /**
     * 해당 AgentType에 해당하는 SensorAgent를 반환한다.
     *
     * @param {@link ISensorAgent} agentType
     * @return {@link BaseSensorAgent}; agentType이 일치하지 않을 경우, NULL
     */
    public BaseSensorAgent getSensorAgent(int agentType) {
        return mSensorAgentMap.get(agentType);
    }
}
