package de.polocloud.bootstrap.pubsub;

import de.polocloud.api.network.protocol.packet.api.PublishPacket;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PubSubManager {

    private Map<String, List<ChannelHandlerContext>> subscriberMap = new ConcurrentHashMap<>();

    public void subscribe(ChannelHandlerContext ctx, String channel) {
        List<ChannelHandlerContext> channelHandlerContextList;

        if (subscriberMap.containsKey(channel)) {
            channelHandlerContextList = subscriberMap.get(channel);
        } else {
            channelHandlerContextList = new ArrayList<>();
        }

        if (channelHandlerContextList.contains(ctx)) {
            channelHandlerContextList.remove(ctx);
        } else {
            channelHandlerContextList.add(ctx);
        }

        subscriberMap.put(channel, channelHandlerContextList);

    }

    public void publish(PublishPacket packet) {

        String channel = packet.getChannel();

        if (subscriberMap.containsKey(channel)) {
            List<ChannelHandlerContext> channelHandlerContextList = subscriberMap.get(channel);

            for (ChannelHandlerContext channelHandlerContext : channelHandlerContextList) {
                channelHandlerContext.writeAndFlush(packet);
            }
        }
    }

}
