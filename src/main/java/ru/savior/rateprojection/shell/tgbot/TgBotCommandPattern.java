package ru.savior.rateprojection.shell.tgbot;

import java.util.*;

public class TgBotCommandPattern {
    public static final String COMMAND_WORD_SETTING = "commandWord";
    public static final String COMMAND_WORD_RATE = "rate";
    public static final String COMMAND_WORD_START = "start";
    public static final String COMMAND_WORD_HELP = "help";
    public static final String CURRENCY_COUNT_SETTING = "currencyCount";
    public static final int CURRENCY_COUNT_SINGLE = 1;
    public static final int CURRENCY_COUNT_MULTIPLE = 5;
    public static final String COMMAND_ARGUMENTS_SETTING = "commandArguments";
    public static final String COMMAND_ARGUMENT_DATE = "date";
    public static final String COMMAND_ARGUMENT_ALGORITHM = "alg";
    public static final String COMMAND_ARGUMENT_OUTPUT = "output";
    public static final String COMMAND_ARGUMENT_PERIOD = "period";

    private final TgBotCommandType commandType;

    private final Map<String, List<String>> patternParams;

    protected TgBotCommandPattern(TgBotCommandType commandType, Map<String, List<String>> patternParams) {
        this.commandType = commandType;
        this.patternParams = patternParams;
    }

    public static TgBotCommandPattern of(TgBotCommandType commandType) {
        Map<String, List<String>> patternParams = new HashMap<>();
        switch (commandType) {
            case RATE_SINGLE_DATE -> {
                patternParams = addRateSingleDatePattern(patternParams);
            }
            case RATE_PERIOD-> {
                patternParams = addRatePeriodPattern(patternParams);
            }
            case HELP -> {
                patternParams = addHelPattern(patternParams);
            }
            case START -> {
                patternParams = addStartPattern(patternParams);
            }
        }
        return new TgBotCommandPattern(commandType, patternParams);
    }

    private static Map<String, List<String>> addRateCommandCommonPart(Map<String, List<String>> patternParams) {
        Map<String, List<String>> newPatternParams = new HashMap<>(patternParams);
        newPatternParams.put(COMMAND_WORD_SETTING, new ArrayList<>() {{
            add(COMMAND_WORD_RATE);
        }});
        return newPatternParams;
    }

    private static Map<String, List<String>> addRateSingleDatePattern(Map<String, List<String>> patternParams) {
        Map<String, List<String>> newPatternParams = addRateCommandCommonPart(patternParams);
        newPatternParams.put(CURRENCY_COUNT_SETTING, new ArrayList<>() {{
            add(Integer.toString(CURRENCY_COUNT_SINGLE));
        }});
        newPatternParams.put(COMMAND_ARGUMENTS_SETTING, new ArrayList<>() {{
            add(COMMAND_ARGUMENT_DATE);
            add(COMMAND_ARGUMENT_ALGORITHM);
        }});
        return newPatternParams;
    }

    private static Map<String, List<String>> addRatePeriodPattern(Map<String, List<String>> patternParams) {
        Map<String, List<String>> newPatternParams = addRateCommandCommonPart(patternParams);
        newPatternParams.put(CURRENCY_COUNT_SETTING, new ArrayList<>() {{
            add(Integer.toString(CURRENCY_COUNT_MULTIPLE));
        }});
        newPatternParams.put(COMMAND_ARGUMENTS_SETTING, new ArrayList<>() {{
            add(COMMAND_ARGUMENT_PERIOD);
            add(COMMAND_ARGUMENT_ALGORITHM);
            add(COMMAND_ARGUMENT_OUTPUT);
        }});
        return newPatternParams;
    }

    private static Map<String, List<String>> addHelPattern(Map<String, List<String>> patternParams) {
        Map<String, List<String>> newPatternParams = addRateCommandCommonPart(patternParams);
        newPatternParams.put(COMMAND_WORD_SETTING, new ArrayList<>() {
            {
                add(COMMAND_WORD_HELP);
            }
        });
        return newPatternParams;
    }

    private static Map<String, List<String>> addStartPattern(Map<String, List<String>> patternParams) {
        Map<String, List<String>> newPatternParams = addRateCommandCommonPart(patternParams);
        newPatternParams.put(COMMAND_WORD_SETTING, new ArrayList<>() {
            {
                add(COMMAND_WORD_START);
            }
        });
        return newPatternParams;
    }

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
