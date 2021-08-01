package de.polocloud.api.event.player;

import de.polocloud.api.player.ICloudPlayer;

public class CloudPlayerDisconnectEvent extends CloudPlayerEvent {

    public CloudPlayerDisconnectEvent(ICloudPlayer player) {
        super(player);
    }

}
