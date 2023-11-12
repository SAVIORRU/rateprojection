package ru.savior.rateprojection.shell.tgbot;

import lombok.RequiredArgsConstructor;
import ru.savior.rateprojection.shell.tgbot.command.Command;
import ru.savior.rateprojection.shell.tgbot.command.CommandConstants;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactory;
import ru.savior.rateprojection.shell.tgbot.command.pattern.BotCommandPattern;
import ru.savior.rateprojection.shell.tgbot.command.pattern.CommandPatternFactory;

import java.util.*;

@RequiredArgsConstructor
public class CommandParser {
    private static final int COMMAND_WORD_INDEX = 0;
    private static final String COMMAND_PARAM_PREFIX = CommandConstants.COMMAND_PARAM_PREFIX;
    private static final String CURRENCY_PARAM_DELIMITER = CommandConstants.PARAM_VALUE_DELIMITER;
    private static final String START_COMMAND_PREFIX = CommandConstants.START_COMMAND_PREFIX;
    private final CommandFactory commandFactory;

    private final CommandPatternFactory patternFactory;

    public Command parseCommandString(String commandString) {
        if (commandString.isEmpty() || commandString.isBlank()) {
            throw new IllegalArgumentException("The following input is empty");
        }
        if (!commandString.trim().startsWith(START_COMMAND_PREFIX)) {
            throw new IllegalArgumentException("The following input is not a command");
        }
        String trimmedCommandString = commandString.trim().substring(1).trim();
        Command command = null;
        for (CommandType commandType : CommandType.values()) {
            Map<String, List<String>> commandStructure = extractCommandStructure(trimmedCommandString);
            BotCommandPattern commandPattern = patternFactory.getCommandPattern(commandType);
            if (commandPattern.check(commandStructure)) {
                command = commandFactory.getCommandFromString(commandType, trimmedCommandString);
                break;
            }
        }
        if (command == null) {
            throw new IllegalArgumentException("The following input cannot be recognized");
        }
        return command;
    }

    private Map<String, List<String>> extractCommandStructure(String commandString) {
        List<String> commandTokens = Arrays.asList(commandString.split(CommandConstants.COMMAND_WORD_DELIMITER));
        Map<String, List<String>> commandStructure = new HashMap<>();
        for (int i = 0; i < commandTokens.size(); i++) {
            if (i == COMMAND_WORD_INDEX) {
                commandStructure.put(CommandConstants.COMMAND_WORD_SETTING, new ArrayList<>() {{
                    add(commandTokens.get(COMMAND_WORD_INDEX).trim().toLowerCase());
                }});
            } else if (commandTokens.get(i).trim().startsWith(COMMAND_PARAM_PREFIX)) {
                String param = commandTokens.get(i).trim().substring(1).trim();
                if (!commandStructure.containsKey(CommandConstants.COMMAND_ARGUMENTS_SETTING)) {
                    commandStructure.put(CommandConstants.COMMAND_ARGUMENTS_SETTING, new ArrayList<>());
                }
                commandStructure.get(CommandConstants.COMMAND_ARGUMENTS_SETTING).add(param.toLowerCase());
            } else {
                if (i - 1 >= 0) {
                    if (commandTokens.get(i - 1).trim().startsWith(COMMAND_PARAM_PREFIX)) {
                        continue;
                    }
                } else {
                    continue;
                }
                long currencyAmount = Arrays.stream(commandTokens.get(i).split(CURRENCY_PARAM_DELIMITER))
                        .filter(token -> !(token.trim().isEmpty() || token.trim().isBlank()))
                        .count();
                if (currencyAmount == 0) {
                    continue;
                }
                if (!commandStructure.containsKey(CommandConstants.CURRENCY_COUNT_SETTING)) {
                    commandStructure.put(CommandConstants.CURRENCY_COUNT_SETTING, new ArrayList<>() {{
                        add("0");
                    }});
                }
                currencyAmount += Long.parseLong(commandStructure
                        .get(CommandConstants.CURRENCY_COUNT_SETTING).get(0));
                commandStructure.get(CommandConstants.CURRENCY_COUNT_SETTING).set(0, Long.toString(currencyAmount));
            }
        }
        return commandStructure;
    }

}
