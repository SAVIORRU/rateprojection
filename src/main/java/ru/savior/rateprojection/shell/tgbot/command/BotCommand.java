package ru.savior.rateprojection.shell.tgbot.command;

import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;

import java.util.List;
import java.util.Map;

public interface BotCommand {
    public List<String> execute(Map<String, Object> context);

    public TgBotCommandType getCommandType();
}
