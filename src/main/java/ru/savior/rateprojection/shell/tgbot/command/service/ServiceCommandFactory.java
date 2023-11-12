package ru.savior.rateprojection.shell.tgbot.command.service;

import ru.savior.rateprojection.shell.tgbot.CommandType;
import ru.savior.rateprojection.shell.tgbot.command.Command;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactoryImpl;

public class ServiceCommandFactory extends CommandFactoryImpl {
    @Override
    public Command getCommandFromString(CommandType commandType, String input) throws IllegalArgumentException {
        switch (commandType) {
            case HELP -> {
                return new HelpCommand();}
            case START -> {
                return new StartCommand();
            }
            default -> {
                throw new IllegalArgumentException("Unsupported command type");
            }
        }
    }
}
