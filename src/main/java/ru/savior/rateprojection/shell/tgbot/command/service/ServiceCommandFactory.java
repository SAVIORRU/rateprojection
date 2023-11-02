package ru.savior.rateprojection.shell.tgbot.command.service;

import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;
import ru.savior.rateprojection.shell.tgbot.command.BotCommand;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactoryImpl;

public class ServiceCommandFactory extends CommandFactoryImpl {
    @Override
    public BotCommand getCommandFromString(TgBotCommandType commandType, String input) throws IllegalArgumentException {
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
