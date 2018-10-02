package ninja.egg82.velocity.core;

import com.velocitypowered.api.proxy.messages.ChannelIdentifier;

public class VelocityMessageQueueData {
    // vars
    private ChannelIdentifier channel = null;
    private byte[] data = null;

    // constructor
    public VelocityMessageQueueData(ChannelIdentifier channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    // public
    public ChannelIdentifier getChannel() {
        return channel;
    }
    public byte[] getData() {
        return data;
    }

    // private

}
