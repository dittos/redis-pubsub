package org.sapzil.redis.pubsub;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;
import org.sapzil.redis.pubsub.message.RedisPubSubServerMessage;
import org.sapzil.redis.pubsub.message.SubscribeMessage;

import java.util.Arrays;

import static org.sapzil.redis.pubsub.util.ChannelFutures.toCompletable;

public class RedisPubSubConnection {
    private final Channel channel;
    private final PublishSubject<RedisPubSubServerMessage> serverMessages = PublishSubject.create();

    public RedisPubSubConnection(Channel channel) {
        this.channel = channel;
        serverMessages.subscribe();
        channel.pipeline().addLast(new SimpleChannelInboundHandler<RedisPubSubServerMessage>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, RedisPubSubServerMessage msg) {
                serverMessages.onNext(msg);
            }
        });
        channel.closeFuture().addListener(f -> serverMessages.onComplete());
    }

    public Completable subscribe(String topic) {
        return Completable.defer(() -> toCompletable(channel.writeAndFlush(new SubscribeMessage(Arrays.asList(topic)))));
    }
}
