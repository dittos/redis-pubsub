package org.sapzil.redis.pubsub.message;

import java.util.List;

public class SubscribeMessage implements RedisPubSubMessage {
    private final List<String> channels;

    public SubscribeMessage(List<String> channels) {
        this.channels = channels;
    }

    public List<String> getChannels() {
        return channels;
    }
}
