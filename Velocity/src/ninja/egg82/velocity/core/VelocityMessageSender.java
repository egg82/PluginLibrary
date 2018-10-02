package ninja.egg82.velocity.core;

import java.io.Closeable;

import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import ninja.egg82.concurrent.FixedConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;

public class VelocityMessageSender implements Closeable {
    // vars

    // Server info/connection
    private RegisteredServer server = null;

    // Message backlog/queue - for storing messages in case of disconnect. We put
    // this here instead of in the wrappers so one server doesn't screw over another
    // if it has no players
    private IConcurrentDeque<VelocityMessageQueueData> backlog = new FixedConcurrentDeque<VelocityMessageQueueData>(150);

    // constructor
    public VelocityMessageSender(RegisteredServer server) {
        this.server = server;
    }

    // public
    public RegisteredServer getServer() {
        return server;
    }

    public void submit(ChannelIdentifier channel, byte[] message) {
        // Grab a new data object
        VelocityMessageQueueData messageData = new VelocityMessageQueueData(channel, message);
        // Add the new data to the send queue, tossing the oldest messages if needed
        while (!backlog.offerLast(messageData) && !backlog.isEmpty()) {
            backlog.pollFirst();
        }
    }

    public void sendAll() {
        while (!backlog.isEmpty()) {
            if (server.getPlayersConnected().isEmpty()) {
                // No players on the server to send messages with. Break out of the loop
                break;
            }

            // Grab the oldest data first
            VelocityMessageQueueData first = backlog.pollFirst();
            if (first == null) {
                // Data is null, which means we prematurely reached the end of the queue
                break;
            }

            // Try to push the data to the Bungee queue
            try {
                server.sendPluginMessage(first.getChannel(), first.getData());
            } catch (Exception ex) {
                // Message send failed. Re-add it to the front of the backlog
                backlog.addFirst(first);
            }
        }
    }

    public void close() {
        backlog.clear();
    }

    // private

}
