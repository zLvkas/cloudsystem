package de.polocloud.plugin.bootstrap;

import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.function.NetworkRegisterFunction;
import de.polocloud.plugin.listener.CollectiveProxyEvents;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.register.NetworkPluginRegister;
import de.polocloud.plugin.protocol.register.NetworkProxyRegister;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyBootstrap extends Plugin implements BootstrapFunction, NetworkRegisterFunction {

    private CloudPlugin cloudPlugin;

    @Override
    public void onEnable() {
        cloudPlugin = new CloudPlugin(this);
        cloudPlugin.callListeners(this);
    }

    @Override
    public void executeCommand(String command) {
        getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), command.substring(0, command.length() - 1));
    }

    @Override
    public int getNetworkPort() {
        return -1;
    }

    @Override
    public void callNetwork(NetworkClient networkClient) {
        System.out.println("NetworkClient ? " + networkClient);
        new NetworkProxyRegister(networkClient, cloudPlugin, this);
        new NetworkPluginRegister(networkClient, this);
    }

    @Override
    public void registerEvents(CloudPlugin cloudPlugin) {
        new CollectiveProxyEvents(this, cloudPlugin);
    }

    @Override
    public void shutdown() {
        ProxyServer.getInstance().stop();
    }


}
