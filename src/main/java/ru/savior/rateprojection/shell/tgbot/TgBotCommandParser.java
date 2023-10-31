package ru.savior.rateprojection.shell.tgbot;

import java.util.*;

public class TgBotCommandParser {
    private static final int COMMAND_WORD_INDEX = 0;

    private static final String COMMAND_PARAM_PREFIX = "-";
    private static final String CURRENCY_PARAM_DELIMITER = ",";

    private static final String START_COMMAND_PREFIX = "/";

    public TgBotCommand parseCommandString(String commandString) {
        if (commandString.isEmpty() || commandString.isBlank()) {
            throw new IllegalArgumentException("The following input is empty");
        }
        if (!commandString.trim().startsWith(START_COMMAND_PREFIX)) {
            throw new IllegalArgumentException("The following input is not a command");
        }
        String trimmedCommandString = commandString.trim().substring(1).trim();
        TgBotCommand command = null;
        for (TgBotCommandType commandType : TgBotCommandType.values()) {
            Map<String, List<String>> commandStructure = extractCommandStructure(trimmedCommandString);
            TgBotCommandPattern commandPattern = TgBotCommandPattern.of(commandType);
            if (commandPattern.check(commandStructure)) {
                command = buildCommand(commandType, trimmedCommandString);
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
                commandStructure.put(TgBotCommandPattern.COMMAND_WORD_SETTING, new ArrayList<>() {{
                    add(commandTokens.get(COMMAND_WORD_INDEX).trim().toLowerCase());
                }});
            } else if (commandTokens.get(i).trim().startsWith(COMMAND_PARAM_PREFIX)) {
                String param = commandTokens.get(i).trim().substring(1).trim();
                if (!commandStructure.containsKey(TgBotCommandPattern.COMMAND_ARGUMENTS_SETTING)) {
                    commandStructure.put(TgBotCommandPattern.COMMAND_ARGUMENTS_SETTING, new ArrayList<>());
                }
                commandStructure.get(TgBotCommandPattern.COMMAND_ARGUMENTS_SETTING).add(param.toLowerCase());
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
                if (!commandStructure.containsKey(TgBotCommandPattern.CURRENCY_COUNT_SETTING)) {
                    commandStructure.put(TgBotCommandPattern.CURRENCY_COUNT_SETTING, new ArrayList<>() {{
                        add("0");
                    }});
                }
                currencyAmount += Long.parseLong(commandStructure
                        .get(TgBotCommandPattern.CURRENCY_COUNT_SETTING).get(0));
                commandStructure.get(TgBotCommandPattern.CURRENCY_COUNT_SETTING).set(0, Long.toString(currencyAmount));
            }
        }
        return commandStructure;
    }

    private TgBotCommand buildCommand(TgBotCommandType commandType, String commandString) {
        TgBotCommand command = new TgBotCommand(commandType, new ArrayList<>(), new HashMap<>());
        List<String> commandTokens = Arrays.asList(commandString.split("\\s* \\s*"));
        for (int i = 0; i < commandTokens.size(); i++) {
            if (i == COMMAND_WORD_INDEX) {
                continue;
            } else if (commandTokens.get(i).trim().startsWith(COMMAND_PARAM_PREFIX)) {
                String paramName = commandTokens.get(i).trim().substring(1).trim().toLowerCase();
                String paramValue = "";
                if (i + 1 <= commandTokens.size() - 1) {
                    paramValue = commandTokens.get(i + 1).trim().toLowerCase();
                    if (paramValue.startsWith(COMMAND_PARAM_PREFIX)) {
                        paramValue = "";
                    }
                }
                command.getParameters().put(paramName, paramValue);
            } else {
                if (i > 0) {
                    if (commandTokens.get(i - 1).trim().startsWith(COMMAND_PARAM_PREFIX)) {
                        continue;
                    }
                } else {
                    continue;
                }
                List<String> currencyTokens = Arrays
                        .stream(commandTokens.get(i).split("\\s*" + CURRENCY_PARAM_DELIMITER + "\\s*"))
                        .filter(token -> !(token.trim().isEmpty() || token.trim().isBlank()))
                        .map(token -> token.trim().toUpperCase())
                        .toList();
                command.getCurrencies().addAll(currencyTokens);

            }
        }
        return command;
    }
}
