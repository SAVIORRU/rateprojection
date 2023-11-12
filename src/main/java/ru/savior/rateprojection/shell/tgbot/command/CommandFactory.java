package ru.savior.rateprojection.shell.tgbot.command;

import ru.savior.rateprojection.shell.tgbot.CommandType;

public interface CommandFactory {
    public Command getCommandFromString(CommandType commandType, String input) throws IllegalArgumentException;
}
