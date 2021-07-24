package de.polocloud.addons.signs.executes.loading;

import de.polocloud.addons.signs.SignService;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.api.CloudExecutor;

import java.util.concurrent.ExecutionException;

public class SignAutoLoading {

    SignService signService;

    public SignAutoLoading(SignService signService, ITemplate template) throws ExecutionException, InterruptedException {
        this.signService = signService;
        CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(template).thenAccept(key -> {
            for (IGameServer iGameServer : key) {
                signService.getAddSign().execute(iGameServer);
            }
        });

/*
        List<IGameServer> iGameServers = CloudExecutor.getInstance().getGameServerManager().getGameServersByTemplate(template).get();
        for (IGameServer iGameServer : iGameServers) {
            signService.getAddSign().execute(iGameServer);
        }

 */
    }
}
