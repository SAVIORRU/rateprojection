package ru.savior.rateprojection.shell.tgbot.command.service;

import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;
import ru.savior.rateprojection.shell.tgbot.command.BotCommandImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartCommand extends BotCommandImpl {

    public static final String COMMAND_WORD = "start";
    public StartCommand() {
        super(TgBotCommandType.START);
    }

    @Override
    public List<String> execute(Map<String, Object> context) {
        List<String> commandOutput = new ArrayList<>();
        commandOutput.add("Type /help for view available commands");
        return commandOutput;
    }
}
