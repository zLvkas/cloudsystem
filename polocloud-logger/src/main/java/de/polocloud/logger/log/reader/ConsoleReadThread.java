package de.polocloud.logger.log.reader;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandPool;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleReadThread extends Thread {

    private ConsoleReader consoleReader;

    public ConsoleReadThread(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;

        this.setDaemon(true);
        this.setPriority(Thread.MIN_PRIORITY);
        this.start();
    }

    @Override
    public void run() {
        String line;
        while (!isInterrupted()) {
            try {
                this.consoleReader.setPrompt("");
                this.consoleReader.resetPromptLine("", "", 0);
                while ((line = this.consoleReader.readLine(Logger.PREFIX)) != null && !line.trim().isEmpty()) {
                    this.consoleReader.setPrompt("");

                    String[] args = line.split(" ");

                    if (PoloCloudAPI.getInstance() != null) {
                        ICommandPool commandPool = PoloCloudAPI.getInstance().getCommandPool();
                        List<CloudCommand> commands = commandPool.getAllCachedCommands().stream().filter(key -> key.getName().equalsIgnoreCase(args[0])).collect(Collectors.toList());

                        if (commands.size() == 0) {
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "Unknown command... Please use the " + ConsoleColors.LIGHT_BLUE + "help " + ConsoleColors.GRAY + "command.");
                        } else {
                            for (CloudCommand command : commands) {
                                if (command.getCommandType().equals(CommandType.CONSOLE) || command.getCommandType().equals(CommandType.INGAME_CONSOLE)) {
                                    command.execute(PoloCloudAPI.getInstance().getCommandExecutor(), args);
                                }
                            }
                        }

                    }
                }
            } catch (IOException throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
