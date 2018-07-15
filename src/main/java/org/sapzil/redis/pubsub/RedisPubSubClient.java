package org.sapzil.redis.pubsub;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.*;
import io.reactivex.Single;
import org.sapzil.redis.pubsub.codec.RedisPubSubMessageDecoder;
import org.sapzil.redis.pubsub.codec.RedisPubSubMessageEncoder;
import org.sapzil.redis.pubsub.util.ChannelFutures;

public class RedisPubSubClient {
    private final Bootstrap bootstrap;

    public RedisPubSubClient() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new RedisEncoder(),
                        new RedisPubSubMessageEncoder(),
                        new RedisDecoder(),
                        new RedisBulkStringAggregator(),
                        new RedisArrayAggregator(),
                        new RedisPubSubMessageDecoder()
                );
            }
        });
        this.bootstrap = b;
    }

    public Single<RedisPubSubConnection> connect(String host, int port) {
        return Single.defer(() -> ChannelFutures.toChannelSingle(bootstrap.connect(host, port)))
                .map(RedisPubSubConnection::new);
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 6379;
        RedisPubSubClient client = new RedisPubSubClient();
        RedisPubSubConnection conn = client.connect(host, port).blockingGet();
        conn.subscribe("topic1").blockingAwait();
    }
}
