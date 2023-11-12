package ru.savior.rateprojection.shell.tgbot.command.pattern;

import ru.savior.rateprojection.shell.tgbot.CommandType;

public interface CommandPatternFactory {
    public BotCommandPattern getCommandPattern(CommandType commandType) throws IllegalArgumentException;
}
