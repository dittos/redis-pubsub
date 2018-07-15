package org.sapzil.redis.pubsub.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.reactivex.Completable;
import io.reactivex.Single;

public class ChannelFutures {
    public static Single<Channel> toChannelSingle(ChannelFuture future) {
        return Single.create(emitter -> {
            future.addListener(f -> {
                if (f.isSuccess()) {
                    emitter.onSuccess(future.channel());
                } else {
                    emitter.onError(f.cause());
                }
            });
        });
    }

    public static Completable toCompletable(ChannelFuture future) {
        return Completable.create(emitter -> {
            future.addListener(f -> {
                if (f.isSuccess()) {
                    emitter.onComplete();
                } else {
                    emitter.onError(f.cause());
                }
            });
        });
    }
}
