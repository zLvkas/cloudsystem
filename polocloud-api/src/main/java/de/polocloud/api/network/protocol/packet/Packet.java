package de.polocloud.api.network.protocol.packet;

import com.google.gson.Gson;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import io.netty.buffer.ByteBuf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.List;

public abstract class Packet {

    protected static Gson gson = new Gson();

    public abstract void write(ByteBuf byteBuf) throws IOException;

    public abstract void read(ByteBuf byteBuf) throws IOException;

    protected void writeGameServer(ByteBuf byteBuf, IGameServer gameServer) {

        writeString(byteBuf, gameServer.getName());
        writeString(byteBuf, gameServer.getStatus().toString());
        byteBuf.writeLong(gameServer.getSnowflake());

        writeTemplate(byteBuf, gameServer.getTemplate());

        byteBuf.writeLong(gameServer.getTotalMemory());
        byteBuf.writeInt(gameServer.getOnlinePlayers());
        byteBuf.writeInt(gameServer.getPort());
        byteBuf.writeLong(gameServer.getPing());
        byteBuf.writeLong(gameServer.getStartTime());

    }

    protected IGameServer readGameServer(ByteBuf byteBuf) {

        String name = readString(byteBuf);
        GameServerStatus status = GameServerStatus.valueOf(readString(byteBuf));
        long snowflake = byteBuf.readLong();

        ITemplate template = readTemplate(byteBuf);

        long totalMemory = byteBuf.readLong();
        int onlinePlayers = byteBuf.readInt();
        int port = byteBuf.readInt();
        long ping = byteBuf.readLong();

        long startTime = byteBuf.readLong();

        return new IGameServer() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public GameServerStatus getStatus() {
                return status;
            }

            @Override
            public long getSnowflake() {
                return snowflake;
            }

            @Override
            public ITemplate getTemplate() {
                return template;
            }

            @Override
            public List<ICloudPlayer> getCloudPlayers() {
                throw new NotImplementedException();
            }

            @Override
            public void setStatus(GameServerStatus status) {
                throw new NotImplementedException();
            }

            @Override
            public long getTotalMemory() {
                return totalMemory;
            }

            @Override
            public int getOnlinePlayers() {
                return onlinePlayers;
            }

            @Override
            public int getPort() {
                return port;
            }

            @Override
            public long getPing() {
                return ping;
            }

            @Override
            public long getStartTime() {
                return startTime;
            }

            @Override
            public void stop() {
                //TODO
                throw new NotImplementedException();
            }

            @Override
            public void sendPacket(Packet packet) {
                //TODO
                throw new NotImplementedException();
            }
        };

    }


    protected void writeTemplate(ByteBuf byteBuf, ITemplate template) {

        writeString(byteBuf, template.getName());

        byteBuf.writeInt(template.getMinServerCount());
        byteBuf.writeInt(template.getMaxServerCount());
        byteBuf.writeInt(template.getMaxPlayers());

        byteBuf.writeInt(template.getMaxMemory());

        writeString(byteBuf, template.getMotd());

        byteBuf.writeBoolean(template.isMaintenance());

        writeString(byteBuf, template.getTemplateType().toString());
        writeString(byteBuf, template.getVersion().toString());

        writeStringArray(byteBuf, template.getWrapperNames());

    }

    protected ITemplate readTemplate(ByteBuf byteBuf) {

        String name = readString(byteBuf);

        int minServers = byteBuf.readInt();
        int maxServers = byteBuf.readInt();

        int maxPlayers = byteBuf.readInt();
        int maxMemory = byteBuf.readInt();

        String motd = readString(byteBuf);

        boolean maintenance = byteBuf.readBoolean();

        TemplateType templateType = TemplateType.valueOf(readString(byteBuf));
        GameServerVersion version = GameServerVersion.valueOf(readString(byteBuf));

        String[] wrapperNames = readStringArray(byteBuf);

        return new ITemplate() {
            @Override
            public int getMinServerCount() {
                return minServers;
            }

            @Override
            public int getMaxServerCount() {
                return maxServers;
            }

            @Override
            public int getMaxPlayers() {
                return maxPlayers;
            }

            @Override
            public void setMaxPlayers(int maxPlayers) {
                //TODO send packet to server ? or block ?
                throw new NotImplementedException();
            }

            @Override
            public int getMaxMemory() {
                return maxMemory;
            }

            @Override
            public String getMotd() {
                return motd;
            }

            @Override
            public boolean isMaintenance() {
                return maintenance;
            }

            @Override
            public TemplateType getTemplateType() {
                return templateType;
            }

            @Override
            public GameServerVersion getVersion() {
                return version;
            }

            @Override
            public void setMaintenance(boolean state) {
                //TODO
                throw new NotImplementedException();
            }

            @Override
            public String[] getWrapperNames() {
                return wrapperNames;
            }

            @Override
            public String getName() {
                return name;
            }
        };

    }

    protected void writeStringArray(ByteBuf byteBuf, String[] arr) {
        byteBuf.writeInt(arr.length);
        for (String s : arr) {
            writeString(byteBuf, s);
        }
    }

    protected String[] readStringArray(ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            array[i] = readString(byteBuf);
        }
        return array;
    }

    protected void writeString(ByteBuf byteBuf, String s) {
        byte[] bArr = s.getBytes();
        byteBuf.writeInt(bArr.length);
        byteBuf.writeBytes(bArr);
    }

    protected String readString(ByteBuf byteBuf) {
        byte[] bArr = new byte[byteBuf.readInt()];
        byteBuf.readBytes(bArr);
        return new String(bArr);
    }


}