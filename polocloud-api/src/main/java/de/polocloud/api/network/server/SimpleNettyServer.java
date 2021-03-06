package de.polocloud.api.network.server;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.PacketRegistry;
import de.polocloud.api.network.protocol.packet.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.inject.Named;

public class SimpleNettyServer implements INettyServer {

    @Inject
    @Named(value = "setting_server_start_port")
    private int port;

    @Inject
    private IProtocol protocol;

    @Override
    public void start() {
        PacketRegistry.registerDefaultInternalPackets();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            (serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                        .addLast(new NettyPacketLengthDeserializer())
                        .addLast(new PacketDecoder())
                        .addLast(new NettyPacketLengthSerializer())
                        .addLast(new PacketEncoder())
                        .addLast(new NetworkHandler(protocol));
                }
            }).option(ChannelOption.SO_BACKLOG, 128);
            ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        System.out.println("Starting new Server on port ?? " + this.port);
    }

    @Override
    public boolean terminate() {
        return true;
    }
    
    @Override
    public IProtocol getProtocol() {
        return protocol;
    }
}
