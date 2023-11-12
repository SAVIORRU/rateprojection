package ru.savior.rateprojection.shell.tgbot.command.pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.savior.rateprojection.shell.tgbot.CommandType;
import ru.savior.rateprojection.shell.tgbot.command.CommandConstants;

import java.util.*;
@Slf4j
@RequiredArgsConstructor
public class BotCommandPattern {
    private static final String COMMAND_WORD_SETTING = CommandConstants.COMMAND_WORD_SETTING;
    private static final String CURRENCY_COUNT_SETTING = CommandConstants.CURRENCY_COUNT_SETTING;
    private static final String COMMAND_ARGUMENTS_SETTING = CommandConstants.COMMAND_ARGUMENTS_SETTING;

    private final CommandType commandType;

    private final Map<String, List<String>> patternParams;


    public boolean check(Map<String, List<String>> commandParts) {
        boolean isValid = true;
        try {
            for (String commandPart : patternParams.keySet()) {
                switch (commandPart) {
                    case COMMAND_WORD_SETTING -> {
                        isValid = checkForCommandWordSetting(commandParts);
                    }
                    case COMMAND_ARGUMENTS_SETTING -> {
                        isValid = checkForCommandArgumentSetting(commandParts);
                    }
                    case CURRENCY_COUNT_SETTING -> {
                        isValid = checkForCurrencyCountSetting(commandParts);
                    }
                }
                if (!isValid) {
                    break;
                }
            }
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            log.debug(ExceptionUtils.getStackTrace(exception));
            return false;
        }
        return isValid;
    }

    private boolean checkForCommandWordSetting(Map<String, List<String>> commandParts) {
        boolean isValid = true;
        if (commandParts.containsKey(COMMAND_WORD_SETTING)) {
            String commandWord = commandParts.get(COMMAND_WORD_SETTING).get(0);
            isValid = commandWord.toLowerCase().equals(patternParams.get(COMMAND_WORD_SETTING).get(0));
        } else {
            isValid = false;
        }
        return isValid;
    }

    private boolean checkForCurrencyCountSetting(Map<String, List<String>> commandParts) {
        boolean isValid = true;
        if (commandParts.containsKey(CURRENCY_COUNT_SETTING)) {
            int currencyCount = Integer.parseInt(commandParts.get(CURRENCY_COUNT_SETTING).get(0));
            int patternCurrencyCount = Integer.parseInt(patternParams.get(CURRENCY_COUNT_SETTING).get(0));
            if (currencyCount == 0 || currencyCount > patternCurrencyCount) {
                isValid = false;
            }
        } else {
            isValid = false;
        }
        return isValid;
    }

    private boolean checkForCommandArgumentSetting(Map<String, List<String>> commandParts) {
        boolean isValid = true;
        if (commandParts.containsKey(COMMAND_ARGUMENTS_SETTING)) {
            if (commandParts.get(COMMAND_ARGUMENTS_SETTING).size() == patternParams.get(COMMAND_ARGUMENTS_SETTING).size()) {
                Set<String> params = new HashSet<>();
                for (String param: commandParts.get(COMMAND_ARGUMENTS_SETTING)) {
                    boolean isNotDuplicate = params.add(param.toLowerCase());
                    if (!isNotDuplicate) {
                        isValid = false;
                        break;
                    }
                    if (!patternParams.get(COMMAND_ARGUMENTS_SETTING).contains(param.toLowerCase())) {
                        isValid = false;
                        break;
                    }
                }
            } else {
                isValid = false;
            }
        } else {
            isValid = false;
        }
        return isValid;
    }
}
