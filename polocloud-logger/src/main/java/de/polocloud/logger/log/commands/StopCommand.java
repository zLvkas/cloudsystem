package de.polocloud.logger.log.commands;

import de.polocloud.api.commands.CloudCommand;

public class StopCommand extends CloudCommand {

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }
}
