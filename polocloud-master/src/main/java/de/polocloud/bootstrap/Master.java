package de.polocloud.bootstrap;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.event.ChannelActiveEvent;
import de.polocloud.api.event.ChannelInactiveEvent;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.protocol.packet.api.APIRequestGameServerPacket;
import de.polocloud.api.network.server.SimpleNettyServer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.SimpleWrapperClientManager;
import de.polocloud.bootstrap.commands.*;
import de.polocloud.bootstrap.commands.ClouldStopCommand;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.creator.ServerCreatorRunner;
import de.polocloud.bootstrap.gameserver.SimpleGameServerManager;
import de.polocloud.bootstrap.guice.MasterGuiceModule;
import de.polocloud.bootstrap.listener.ChannelActiveListener;
import de.polocloud.bootstrap.listener.ChannelInactiveListener;
import de.polocloud.bootstrap.network.handler.*;
import de.polocloud.bootstrap.player.SimpleCloudPlayerManager;
import de.polocloud.bootstrap.template.SimpleTemplateService;
import de.polocloud.bootstrap.template.TemplateStorage;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.File;

public class Master implements IStartable, ITerminatable {

    private final CloudAPI cloudAPI;

    private SimpleNettyServer nettyServer;

    private final ITemplateService templateService;
    private final IWrapperClientManager wrapperClientManager;
    private final IGameServerManager gameServerManager;
    private final ICloudPlayerManager cloudPlayerManager;

    private boolean running = false;


    public Master() {

        this.wrapperClientManager = new SimpleWrapperClientManager();
        this.gameServerManager = new SimpleGameServerManager();
        this.templateService = new SimpleTemplateService();
        this.cloudPlayerManager = new SimpleCloudPlayerManager();


        this.cloudAPI = new PoloCloudAPI(new PoloAPIGuiceModule(), new MasterGuiceModule(loadConfig(), this, wrapperClientManager, this.gameServerManager, templateService, this.cloudPlayerManager));

        ((SimpleTemplateService) this.templateService).load(this.cloudAPI, TemplateStorage.FILE);
        this.templateService.getTemplateLoader().loadTemplates();


        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new TemplateCloudCommand(this.templateService));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new ClouldStopCommand());
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new ShutdownGameServerCommand(gameServerManager));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new CloudHelpCommand());
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new CloudEditCommand(templateService, gameServerManager));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new CloudInfoCommand(this.templateService, this.gameServerManager));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new ShutdownTemplateServerCommand(this.gameServerManager, this.templateService));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new LogMeCommand());

        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new GameServerExecuteCommand(this.gameServerManager));
        PoloCloudAPI.getInstance().getCommandPool().registerCommand(new GameServerCloudCommand(this.templateService, this.wrapperClientManager));

        Thread runnerThread = new Thread(PoloCloudAPI.getInstance().getGuice().getInstance(ServerCreatorRunner.class));
        runnerThread.start();

    }

    private MasterConfig loadConfig() {

        File configFile = new File("config.json");
        IConfigLoader configLoader = new SimpleConfigLoader();

        MasterConfig masterConfig = configLoader.load(MasterConfig.class, configFile);

        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(masterConfig, configFile);

        return masterConfig;
    }

    @Override
    public void start() {
        running = true;
        Logger.log(LoggerType.INFO, "Trying to start master...");

        this.nettyServer = this.cloudAPI.getGuice().getInstance(SimpleNettyServer.class);

        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(WrapperLoginPacketHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerRegisterPacketHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerPlayerRequestJoinHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(StatisticMemoryHandler.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerPlayerUpdateListener.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerPlayerDisconnectListener.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(GameServerControlPlayerListener.class));
        this.nettyServer.getProtocol().registerPacketHandler(PoloCloudAPI.getInstance().getGuice().getInstance(APIRequestGameServerHandler.class));

        //events
        EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(ChannelInactiveListener.class), ChannelInactiveEvent.class);
        EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(ChannelActiveListener.class), ChannelActiveEvent.class);

        new Thread(() -> nettyServer.start()).start();

        if (this.templateService.getLoadedTemplates().size() > 0) {
            StringBuilder builder = new StringBuilder();
            this.templateService.getLoadedTemplates().forEach(key -> builder.append(key.getName()).append(","));
            Logger.log(LoggerType.INFO, "Founded templates: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + builder.substring(0, builder.length() - 1));
        } else {
            Logger.log(LoggerType.INFO, "No templates founded.");
        }


        Logger.log(LoggerType.INFO, "The master is " + ConsoleColors.GREEN.getAnsiCode() + "successfully" + ConsoleColors.GRAY.getAnsiCode() + " started.");
    }


    @Override
    public boolean terminate() {
        this.running = false;
        return this.nettyServer.terminate();
    }

    public boolean isRunning() {
        return running;
    }
}
