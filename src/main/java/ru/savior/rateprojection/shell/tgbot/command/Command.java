package ru.savior.rateprojection.shell.tgbot.command;

import ru.savior.rateprojection.shell.tgbot.CommandType;

import java.util.List;
import java.util.Map;

public interface Command {
    public List<String> execute(Map<String, Object> context);

    public CommandType getCommandType();
}
