package ru.savior.rateprojection.shell.tgbot.command.pattern.rate;

import ru.savior.rateprojection.shell.tgbot.command.pattern.BotCommandPattern;
import ru.savior.rateprojection.shell.tgbot.CommandType;
import ru.savior.rateprojection.shell.tgbot.command.pattern.CommandPatternFactoryImpl;
import ru.savior.rateprojection.shell.tgbot.command.rate.RateCommand;
import ru.savior.rateprojection.shell.tgbot.command.rate.RatePeriodCommand;
import ru.savior.rateprojection.shell.tgbot.command.rate.RateSingleDateCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.savior.rateprojection.shell.tgbot.command.CommandConstants.*;
import static ru.savior.rateprojection.shell.tgbot.command.rate.RateCommand.COMMAND_ARGUMENT_ALGORITHM;

public class RateCommandPatternFactory extends CommandPatternFactoryImpl {
    private static final int CURRENCY_COUNT_SINGLE = 1;
    private static final int CURRENCY_COUNT_MULTIPLE = 5;

    public BotCommandPattern getCommandPattern(CommandType commandType) {
        Map<String, List<String>> patternParams = new HashMap<>();
        patternParams.put(COMMAND_WORD_SETTING, new ArrayList<>() {{
            add(RateCommand.COMMAND_WORD);
        }});
        switch (commandType) {
            case RATE_SINGLE_DATE -> {
                patternParams = addRateSingleDatePattern(patternParams);
            }
            case RATE_PERIOD -> {
                patternParams = addRatePeriodPattern(patternParams);
            }
        }
        return new BotCommandPattern(commandType, patternParams);
    }

    private Map<String, List<String>> addRateSingleDatePattern(Map<String, List<String>> patternParams) {
        Map<String, List<String>> newPatternParams = new HashMap<>(patternParams);
        newPatternParams.put(CURRENCY_COUNT_SETTING, new ArrayList<>() {{
            add(Integer.toString(CURRENCY_COUNT_SINGLE));
        }});
        newPatternParams.put(COMMAND_ARGUMENTS_SETTING, new ArrayList<>() {{
            add(RateSingleDateCommand.COMMAND_ARGUMENT_DATE);
            add(COMMAND_ARGUMENT_ALGORITHM);
        }});
        return newPatternParams;
    }

    private Map<String, List<String>> addRatePeriodPattern(Map<String, List<String>> patternParams) {
        Map<String, List<String>> newPatternParams = new HashMap<>(patternParams);
        newPatternParams.put(CURRENCY_COUNT_SETTING, new ArrayList<>() {{
            add(Integer.toString(CURRENCY_COUNT_MULTIPLE));
        }});
        newPatternParams.put(COMMAND_ARGUMENTS_SETTING, new ArrayList<>() {{
            add(RatePeriodCommand.COMMAND_ARGUMENT_PERIOD);
            add(COMMAND_ARGUMENT_ALGORITHM);
            add(RatePeriodCommand.COMMAND_ARGUMENT_OUTPUT);
        }});
        return newPatternParams;
    }

}
