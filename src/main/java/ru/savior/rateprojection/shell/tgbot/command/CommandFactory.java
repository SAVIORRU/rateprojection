package ru.savior.rateprojection.shell.tgbot.command;

import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;

public interface CommandFactory {
    public BotCommand getCommandFromString(TgBotCommandType commandType, String input) throws IllegalArgumentException;
}
