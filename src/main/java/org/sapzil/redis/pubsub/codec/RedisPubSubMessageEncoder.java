package org.sapzil.redis.pubsub.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.redis.ArrayHeaderRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import org.sapzil.redis.pubsub.message.RedisPubSubMessage;
import org.sapzil.redis.pubsub.message.SubscribeMessage;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RedisPubSubMessageEncoder extends MessageToMessageEncoder<RedisPubSubMessage> {
    private static final ByteBuf COMMAND_SUBSCRIBE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("subscribe", StandardCharsets.UTF_8));

    @Override
    protected void encode(ChannelHandlerContext ctx, RedisPubSubMessage msg, List<Object> out) {
        if (msg instanceof SubscribeMessage) {
            SubscribeMessage message = (SubscribeMessage) msg;
            out.add(new ArrayHeaderRedisMessage(message.getChannels().size() + 1));
            out.add(new FullBulkStringRedisMessage(COMMAND_SUBSCRIBE.duplicate()));
            for (String channel : message.getChannels()) {
                ByteBuf buf = ctx.alloc().buffer(channel.length());
                buf.writeCharSequence(channel, StandardCharsets.UTF_8);
                out.add(new FullBulkStringRedisMessage(buf));
            }
        }
    }
}
