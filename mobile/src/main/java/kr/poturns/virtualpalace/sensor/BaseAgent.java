package kr.poturns.virtualpalace.sensor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * @author YeonhoKim
 */
abstract class BaseAgent implements IAgent {

    protected final Channel mChannelF = new Channel();

    protected final ConcurrentHashMap<AgentType, Channel> mListeningChannels = new ConcurrentHashMap<AgentType, Channel>();

    public Channel getChannel() {
        return mChannelF;
    }

    public synchronized void addListeningChannel(AgentType type, Channel channel) {
        mListeningChannels.put(type, channel);
    }

    public synchronized Map<AgentType, Channel> getAllListeningChannel() {
        return mListeningChannels;
    }

    public synchronized void removeListeningChannel(AgentType type) {
        mListeningChannels.remove(type);
    }

    protected abstract void handleForCollectingChannels(AgentType type, float[] changed);

    protected final void handle() {
        for(AgentType type : mChannelF.mChannelValues.keySet())
            handleForCollectingChannels(type, mChannelF.getValues(type));
    }

    protected abstract float[] updateForListeningChannels();

    protected final void update() {
        for(Channel channel : mListeningChannels.values())
            channel.update(getAgentType(), updateForListeningChannels());
    }


    /**
     *
     * @author YeonhoKim
     */
    public class Channel {
        private ConcurrentHashMap<AgentType, float[]> mChannelValues = new ConcurrentHashMap<>(5);

        public void update(AgentType type, float[] changed) {
            mChannelValues.put(type, changed);
        }

        public float[] getValues(IAgent.AgentType type) {
            return mChannelValues.get(type);
        }
    }

}
