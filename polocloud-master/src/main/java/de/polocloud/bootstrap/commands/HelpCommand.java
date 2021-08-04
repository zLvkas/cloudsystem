package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.bootstrap.Master;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Arrays;

@CloudCommand.Info(name = "Help", description = "Shows all available commands", aliases = "?", commandType = CommandType.CONSOLE)
public class HelpCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Help]----");
        int ingame = 0;
        int ingameConsole = 0;
        int console = 0;
        for (CloudCommand command : Master.getInstance().getCloudAPI().getCommandPool().getAllCachedCommands()) {
            Logger.newLine();
            Logger.log(LoggerType.INFO, Logger.PREFIX + "--[" + ConsoleColors.LIGHT_BLUE.getAnsiCode() + command.getName() + "-Command" + ConsoleColors.GRAY.getAnsiCode() + "]--");
            Logger.log(LoggerType.INFO, Logger.PREFIX + "Name » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + command.getName());
            Logger.log(LoggerType.INFO, Logger.PREFIX + "Description » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + command.getDescription());
            Logger.log(LoggerType.INFO, Logger.PREFIX + "Aliases » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + Arrays.toString(command.getAliases()));
            Logger.log(LoggerType.INFO, Logger.PREFIX + "Type » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + command.getCommandType());
            Logger.log(LoggerType.INFO, Logger.PREFIX + "--[/" + ConsoleColors.LIGHT_BLUE.getAnsiCode() + command.getName() + ConsoleColors.GRAY.getAnsiCode() + "]--");
            if (command.getCommandType().equals(CommandType.CONSOLE)) {
                console++;
            } else if (command.getCommandType().equals(CommandType.INGAME_CONSOLE)) {
                ingameConsole++;
            } else if (command.getCommandType().equals(CommandType.INGAME)) {
                ingame++;
            }
        }
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Found » ");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Ingame-Commands » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + ingame);
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Ingame-Console-Commands » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + ingameConsole);
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Console-Commands » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + console);
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Help]----");
    }
}
