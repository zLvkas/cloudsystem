package de.polocloud.api.event.player;

import de.polocloud.api.player.ICloudPlayer;

public class CloudPlayerJoinNetworkEvent extends CloudPlayerEvent {

    public CloudPlayerJoinNetworkEvent(ICloudPlayer player) {
        super(player);
    }

}
