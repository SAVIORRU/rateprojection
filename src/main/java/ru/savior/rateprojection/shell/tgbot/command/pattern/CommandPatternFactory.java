package ru.savior.rateprojection.shell.tgbot.command.pattern;

import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;

public interface CommandPatternFactory {
    public BotCommandPattern getCommandPattern(TgBotCommandType commandType);
}
