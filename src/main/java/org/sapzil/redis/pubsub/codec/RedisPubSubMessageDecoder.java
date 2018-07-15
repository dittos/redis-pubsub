package org.sapzil.redis.pubsub.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import org.sapzil.redis.pubsub.message.MessageServerMessage;
import org.sapzil.redis.pubsub.message.SubscribeServerMessage;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RedisPubSubMessageDecoder extends MessageToMessageDecoder<RedisMessage> {
    private static final ByteBuf COMMAND_MESSAGE = Unpooled.copiedBuffer("message", StandardCharsets.UTF_8);
    private static final ByteBuf COMMAND_SUBSCRIBE = Unpooled.copiedBuffer("subscribe", StandardCharsets.UTF_8);

    @Override
    protected void decode(ChannelHandlerContext ctx, RedisMessage msg, List<Object> out) {
        if (msg instanceof ArrayRedisMessage) {
            List<RedisMessage> args = ((ArrayRedisMessage) msg).children();
            if (args.size() >= 1 && args.get(0) instanceof FullBulkStringRedisMessage) {
                ByteBuf cmd = ((FullBulkStringRedisMessage) args.get(0)).content();
                if (ByteBufUtil.equals(cmd, COMMAND_MESSAGE)) {
                    out.add(new MessageServerMessage(
                            ((FullBulkStringRedisMessage) args.get(1)).content().toString(StandardCharsets.UTF_8),
                            ByteBufUtil.getBytes(((FullBulkStringRedisMessage) args.get(2)).content())
                    ));
                } else if (ByteBufUtil.equals(cmd, COMMAND_SUBSCRIBE)) {
                    out.add(new SubscribeServerMessage(
                            ((FullBulkStringRedisMessage) args.get(1)).content().toString(StandardCharsets.UTF_8),
                            ((IntegerRedisMessage) args.get(2)).value()
                    ));
                }
            }
        }
    }
}
