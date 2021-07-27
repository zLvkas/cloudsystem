package de.polocloud.signs.executes;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.signs.CloudSign;
import de.polocloud.signs.SignService;

public class SignRemoveExecute implements SignExecute {

    private SignService signService;

    public SignRemoveExecute(SignService signService) {
        this.signService = signService;
    }

    @Override
    public void execute(IGameServer gameServer) {
        CloudSign sign = signService.getSignByGameServer(gameServer);

        if (sign == null) return;
        sign.setGameServer(null);
        sign.setSign();

    }
}