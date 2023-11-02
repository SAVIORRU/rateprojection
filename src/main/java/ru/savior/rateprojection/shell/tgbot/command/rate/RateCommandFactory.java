package ru.savior.rateprojection.shell.tgbot.command.rate;

import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithmType;
import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;
import ru.savior.rateprojection.shell.tgbot.command.BotCommand;
import ru.savior.rateprojection.shell.tgbot.command.BotCommandConstants;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactoryImpl;

import java.util.*;

public class RateCommandFactory extends CommandFactoryImpl {
    private static final int COMMAND_WORD_INDEX = 0;
    private static final String CURRENCY_PARAM_DELIMITER = BotCommandConstants.PARAM_VALUE_DELIMITER;
    private static final String COMMAND_ARGUMENT_ALGORITHM = RateCommand.COMMAND_ARGUMENT_ALGORITHM;
    private static final Map<String, ProjectionAlgorithmType> projectionAlgorithmsTexts =
            RateCommand.getProjectionAlgorithmsTexts();

    @Override
    public BotCommand getCommandFromString(TgBotCommandType commandType, String input) throws IllegalArgumentException {
        Set<Currency> currencies = extractCurrencyParam(input);
        Map<String, String> rawParams = extractRawParams(input);
        ProjectionAlgorithmType algorithmType = getProjectionAlgorithmTypeParam(rawParams);
        switch (commandType) {
            case RATE_SINGLE_DATE -> {
                return new RateSingleDateCommand(currencies, algorithmType, rawParams);
            }
            case RATE_PERIOD -> {
                return new RatePeriodCommand(currencies, algorithmType, rawParams);
            }
            default -> throw new IllegalArgumentException("Unsupported command type");
        }
    }

    private Set<Currency> extractCurrencyParam(String input) {
        List<String> commandTokens = Arrays.asList(input.split("\\s* \\s*"));
        List<String> currencyTexts = new ArrayList<>();
        for (int i = 0; i < commandTokens.size(); i++) {
            if (i == COMMAND_WORD_INDEX) {
                continue;
            } else {
                if (i > 0) {
                    if (commandTokens.get(i - 1).trim().startsWith(COMMAND_PARAM_PREFIX) ||
                            commandTokens.get(i).trim().startsWith(COMMAND_PARAM_PREFIX)) {
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
                currencyTexts.addAll(currencyTokens);

            }
        }
        Set<Currency> currencies = new HashSet<>();
        for (String currencyText : currencyTexts) {
            if (Arrays.stream(Currency.values()).anyMatch(x -> x.toString().equals(currencyText.toUpperCase()))) {
                currencies.add(Currency.valueOf(currencyText.toUpperCase()));
            } else {
                throw new IllegalArgumentException("The following currency " + currencyText + " is invalid");
            }
        }
        return currencies;
    }

    private Map<String, String> extractRawParams(String input) {
        Map<String, String> rawParams = new HashMap<>();
        List<String> commandTokens = Arrays.asList(input.split("\\s* \\s*"));
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
                rawParams.put(paramName, paramValue);
            }
        }
        return rawParams;
    }

    private ProjectionAlgorithmType getProjectionAlgorithmTypeParam(Map<String, String> rawParams) {
        String algorithmText = rawParams.get(COMMAND_ARGUMENT_ALGORITHM).toLowerCase();
        if (projectionAlgorithmsTexts
                .containsKey(algorithmText)) {
            return projectionAlgorithmsTexts.get(algorithmText);
        } else {
            throw new IllegalArgumentException("The following projection algorithm "
                    + algorithmText + " is not supported");
        }
    }

}
