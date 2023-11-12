package ru.savior.rateprojection.shell.tgbot.command.pattern.service;

import ru.savior.rateprojection.shell.tgbot.command.pattern.BotCommandPattern;
import ru.savior.rateprojection.shell.tgbot.CommandType;
import ru.savior.rateprojection.shell.tgbot.command.pattern.CommandPatternFactoryImpl;
import ru.savior.rateprojection.shell.tgbot.command.service.HelpCommand;
import ru.savior.rateprojection.shell.tgbot.command.service.StartCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.savior.rateprojection.shell.tgbot.command.CommandConstants.COMMAND_WORD_SETTING;

public class ServiceCommandPatternFactory extends CommandPatternFactoryImpl {
    public BotCommandPattern getCommandPattern(CommandType commandType) {
        Map<String, List<String>> patternParams = new HashMap<>();
        switch (commandType) {
            case START -> {
                patternParams = getStartCommandPattern();
            }
            case HELP -> {
                patternParams = getHelpCommandPattern();
            }
        }
        return new BotCommandPattern(commandType, patternParams);
    }

    private Map<String, List<String>> getHelpCommandPattern() {
        Map<String, List<String>> patternParams = new HashMap<>();
        patternParams.put(COMMAND_WORD_SETTING, new ArrayList<>() {
            {
                add(HelpCommand.COMMAND_WORD);
            }
        });
        return patternParams;
    }

    private Map<String, List<String>> getStartCommandPattern() {
        Map<String, List<String>> patternParams = new HashMap<>();
        patternParams.put(COMMAND_WORD_SETTING, new ArrayList<>() {
            {
                add(StartCommand.COMMAND_WORD);
            }
        });
        return patternParams;
    }

}
