package org.sapzil.redis.pubsub.message;

public class MessageServerMessage implements RedisPubSubServerMessage {
    private final String channel;
    private final byte[] message;

    public MessageServerMessage(String channel, byte[] message) {
        this.channel = channel;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getMessage() {
        return message;
    }
}
