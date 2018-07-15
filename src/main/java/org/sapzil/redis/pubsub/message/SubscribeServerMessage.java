package org.sapzil.redis.pubsub.message;

public class SubscribeServerMessage implements RedisPubSubServerMessage {
    private final String channel;
    private final long channelCount;

    public SubscribeServerMessage(String channel, long channelCount) {
        this.channel = channel;
        this.channelCount = channelCount;
    }

    public String getChannel() {
        return channel;
    }

    public long getChannelCount() {
        return channelCount;
    }
}
