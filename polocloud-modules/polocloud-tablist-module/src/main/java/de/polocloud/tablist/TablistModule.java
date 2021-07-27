package de.polocloud.tablist;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.module.Module;
import de.polocloud.tablist.cache.CloudPlayerTabCache;
import de.polocloud.tablist.collective.CloudPlayerDisconnectListener;
import de.polocloud.tablist.collective.CloudPlayerJoinListener;
import de.polocloud.tablist.config.TablistConfig;
import de.polocloud.tablist.scheduler.IntervalRunnable;

import java.io.File;

public class TablistModule {


    private static TablistModule instance;

    private TablistConfig tablistConfig;
    private IntervalRunnable intervalRunnable;
    private CloudPlayerTabCache cloudPlayerTabCache;


    public TablistModule(Module module) {
        instance = this;
        this.tablistConfig = loadTablistConfig(module);
        this.cloudPlayerTabCache = new CloudPlayerTabCache();

        if (tablistConfig.isActiveModule()) {
            EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(CloudPlayerJoinListener.class), CloudPlayerJoinNetworkEvent.class);
            EventRegistry.registerListener(PoloCloudAPI.getInstance().getGuice().getInstance(CloudPlayerDisconnectListener.class), CloudPlayerDisconnectEvent.class);

            intervalRunnable = new IntervalRunnable(tablistConfig);
        }

    }

    public TablistConfig loadTablistConfig(Module module) {
        File configPath = new File("modules/tablist/");
        if (!configPath.exists()) configPath.mkdirs();

        File configFile = new File("modules/tablist/config.json");

        TablistConfig tablistConfig = module.getConfigLoader().load(TablistConfig.class, configFile);
        module.getConfigSaver().save(tablistConfig, configFile);
        return tablistConfig;
    }

    public IntervalRunnable getIntervalRunnable() {
        return intervalRunnable;
    }

    public TablistConfig getTablistConfig() {
        return tablistConfig;
    }

    public void setTablistConfig(TablistConfig tablistConfig) {
        this.tablistConfig = tablistConfig;
    }

    public static TablistModule getInstance() {
        return instance;
    }

    public CloudPlayerTabCache getTabCache() {
        return cloudPlayerTabCache;
    }
}