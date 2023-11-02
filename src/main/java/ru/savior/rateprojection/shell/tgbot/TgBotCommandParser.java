package ru.savior.rateprojection.shell.tgbot;

import lombok.RequiredArgsConstructor;
import ru.savior.rateprojection.shell.tgbot.command.BotCommand;
import ru.savior.rateprojection.shell.tgbot.command.BotCommandConstants;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactory;
import ru.savior.rateprojection.shell.tgbot.command.pattern.BotCommandPattern;
import ru.savior.rateprojection.shell.tgbot.command.pattern.CommandPatternFactory;

import java.util.*;

@RequiredArgsConstructor
public class TgBotCommandParser {
    private static final int COMMAND_WORD_INDEX = 0;
    private static final String COMMAND_PARAM_PREFIX = BotCommandConstants.COMMAND_PARAM_PREFIX;
    private static final String CURRENCY_PARAM_DELIMITER = BotCommandConstants.PARAM_VALUE_DELIMITER;
    private static final String START_COMMAND_PREFIX = BotCommandConstants.START_COMMAND_PREFIX;
    private final CommandFactory commandFactory;

    private final CommandPatternFactory patternFactory;

    public BotCommand parseCommandString(String commandString) {
        if (commandString.isEmpty() || commandString.isBlank()) {
            throw new IllegalArgumentException("The following input is empty");
        }
        if (!commandString.trim().startsWith(START_COMMAND_PREFIX)) {
            throw new IllegalArgumentException("The following input is not a command");
        }
        String trimmedCommandString = commandString.trim().substring(1).trim();
        BotCommand command = null;
        for (TgBotCommandType commandType : TgBotCommandType.values()) {
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
        List<String> commandTokens = Arrays.asList(commandString.split("\\s* \\s*"));
        Map<String, List<String>> commandStructure = new HashMap<>();
        for (int i = 0; i < commandTokens.size(); i++) {
            if (i == COMMAND_WORD_INDEX) {
                commandStructure.put(BotCommandConstants.COMMAND_WORD_SETTING, new ArrayList<>() {{
                    add(commandTokens.get(COMMAND_WORD_INDEX).trim().toLowerCase());
                }});
            } else if (commandTokens.get(i).trim().startsWith(COMMAND_PARAM_PREFIX)) {
                String param = commandTokens.get(i).trim().substring(1).trim();
                if (!commandStructure.containsKey(BotCommandConstants.COMMAND_ARGUMENTS_SETTING)) {
                    commandStructure.put(BotCommandConstants.COMMAND_ARGUMENTS_SETTING, new ArrayList<>());
                }
                commandStructure.get(BotCommandConstants.COMMAND_ARGUMENTS_SETTING).add(param.toLowerCase());
            } else {
                if (i - 1 >= 0) {
                    if (commandTokens.get(i - 1).trim().startsWith(COMMAND_PARAM_PREFIX)) {
                        continue;
                    }
                } else {
                    continue;
                }
                List<String> currencyTokens = Arrays
                        .asList(commandTokens.get(i).split("\\s*" + CURRENCY_PARAM_DELIMITER + "\\s*"));
                long currencyAmount = currencyTokens
                        .stream()
                        .filter(token -> !(token.trim().isEmpty() || token.trim().isBlank()))
                        .count();
                if (currencyAmount == 0) {
                    continue;
                }
                if (!commandStructure.containsKey(BotCommandConstants.CURRENCY_COUNT_SETTING)) {
                    commandStructure.put(BotCommandConstants.CURRENCY_COUNT_SETTING, new ArrayList<>() {{
                        add("0");
                    }});
                }
                currencyAmount += Long.parseLong(commandStructure
                        .get(BotCommandConstants.CURRENCY_COUNT_SETTING).get(0));
                commandStructure.get(BotCommandConstants.CURRENCY_COUNT_SETTING).set(0, Long.toString(currencyAmount));
            }
        }
        return commandStructure;
    }

}
