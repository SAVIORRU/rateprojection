package ru.savior.rateprojection.shell.tgbot;

import lombok.RequiredArgsConstructor;
import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionDataResponse;
import ru.savior.rateprojection.core.service.ProjectionService;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithmType;
import ru.savior.rateprojection.shell.console.ConsoleCommand;
import ru.savior.rateprojection.shell.tgbot.utils.ChartBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
public class TgBotCommandProcessor {
    public static final String CONTEXT_DATA_PROJECTION = "projectionData";

    private static final String SINGLE_DATE_PARAM_TOMORROW = "tomorrow";
    private static final String PERIOD_OUTPUT_PARAM_LIST = "list";
    private static final String PERIOD_OUTPUT_PARAM_GRAPH = "graph";
    private static final String PERIOD_TIME_PARAM_WEEK = "week";
    private static final String PERIOD_TIME_PARAM_MONTH = "month";
    private static final Set<String> PERIOD_OUTPUT_PARAM_VALUES = new HashSet<>() {{
        add(PERIOD_OUTPUT_PARAM_LIST);
        add(PERIOD_OUTPUT_PARAM_GRAPH);
    }};
    private static final Set<String> PERIOD_TIME_PARAM_VALUES = new HashSet<>() {{
        add(PERIOD_TIME_PARAM_WEEK);
        add(PERIOD_TIME_PARAM_MONTH);
    }};

    private final ProjectionService projectionService;

    private static final Map<String, ProjectionAlgorithmType> projectionAlgorithmsTexts =
            new HashMap<>() {{
                put("average", ProjectionAlgorithmType.AVERAGE);
                put("past_year", ProjectionAlgorithmType.PAST_YEAR);
                put("mystical", ProjectionAlgorithmType.MYSTICAL);
                put("extrapolate", ProjectionAlgorithmType.EXTRAPOLATE);
            }};

    public List<String> processCommand(TgBotCommand command, Map<String, Object> contextData) {
        List<String> commandOutput = new ArrayList<>();
        if (command.getCommandType().toString().startsWith("RATE")) {
            commandOutput.addAll(loadAndExecuteRateCommand(command, contextData));
        }
        else {
            switch (command.getCommandType()) {
                case START -> {
                    commandOutput.add("Type /help for view available commands");}
                case HELP -> {
                    commandOutput.addAll(getHelp());
                }
            }
        }
        return commandOutput;
    }

    private List<String> loadAndExecuteRateCommand(TgBotCommand command, Map<String, Object> contextData) {
        List<String> commandOutput = new ArrayList<>();
        List<DailyCurrencyRate> projectionData = null;
        try {
            projectionData = getProjectionDataFromContext(contextData);
        } catch (RuntimeException exception) {
            commandOutput.add(exception.getMessage());
            return commandOutput;
        }
        ProjectionAlgorithmType algorithmType = null;
        try {
            algorithmType = getProjectionAlgorithmTypeParam(command);
        } catch (RuntimeException exception) {
            commandOutput.add("The following projection algorithm is not supported");
            return commandOutput;
        }
        List<Currency> currencies = null;
        try {
            currencies = getCurrencyParam(command);
        } catch (RuntimeException exception) {
            commandOutput.add("The following currency param is not supported");
            return commandOutput;
        }
        switch (command.getCommandType()) {
            case RATE_SINGLE_DATE ->
                    commandOutput.addAll(processRateSingleDateCommand(command, projectionData, algorithmType,
                            currencies));
            case RATE_PERIOD -> commandOutput.addAll(processRatePeriodCommand(command, projectionData, algorithmType,
                    currencies));
        }
        return commandOutput;
    }

    private List<String> processRateSingleDateCommand(TgBotCommand command, List<DailyCurrencyRate> projectionData,
                                                      ProjectionAlgorithmType algorithmType, List<Currency> currencies) {
        List<String> commandOutput = new ArrayList<>();
        LocalDateTime targetDate = null;
        try {
            targetDate = getTargetDateParam(command);
        } catch (RuntimeException exception) {
            commandOutput.add("The following -date parameter value is invalid");
            return commandOutput;
        }
        for (Currency currency : currencies) {
            commandOutput.addAll(projectionService.projectForSpecificDate(projectionData, currency,
                    algorithmType, targetDate).format());
        }
        return commandOutput;
    }

    private List<String> processRatePeriodCommand(TgBotCommand command, List<DailyCurrencyRate> projectionData,
                                                  ProjectionAlgorithmType algorithmType, List<Currency> currencies) {
        List<String> commandOutput = new ArrayList<>();
        String outputParam = null;
        try {
            outputParam = getParamFromSet(command, TgBotCommandPattern.COMMAND_ARGUMENT_OUTPUT,
                    PERIOD_OUTPUT_PARAM_VALUES);
        } catch (RuntimeException exception) {
            commandOutput.add("The following -output parameter value is invalid");
            return commandOutput;
        }
        String periodParam = null;
        try {
            periodParam = getParamFromSet(command, TgBotCommandPattern.COMMAND_ARGUMENT_PERIOD,
                    PERIOD_TIME_PARAM_VALUES);
        } catch (RuntimeException exception) {
            commandOutput.add("The following -period parameter value is invalid");
            return commandOutput;
        }
        List<ProjectionDataResponse> responses = new ArrayList<>();
        for (Currency currency : currencies) {
            ProjectionDataResponse dataResponse = null;
            switch (periodParam) {
                case PERIOD_TIME_PARAM_WEEK -> {
                    dataResponse = projectionService
                            .projectForNextWeek(projectionData, currency, algorithmType);}
                case PERIOD_TIME_PARAM_MONTH -> {
                    dataResponse = projectionService
                            .projectForNextMonth(projectionData, currency, algorithmType);}
            }
            responses.add(dataResponse);
            if (!dataResponse.isSuccessful()) {
                commandOutput.add("The following errors have occurred during projection for currency "
                        + currency.toString() + ":");
                commandOutput.addAll(dataResponse.format());
                return commandOutput;
            }
        }
        switch (outputParam) {
            case PERIOD_OUTPUT_PARAM_LIST -> {
                commandOutput.addAll(formatRatePeriodOutputToList(responses));}
            case PERIOD_OUTPUT_PARAM_GRAPH -> {
                commandOutput.addAll(ChartBuilder.buildChartFile(responses));
            }
        }
        return commandOutput;
    }

    private List<DailyCurrencyRate> getProjectionDataFromContext(Map<String, Object> contextData) {
        List<DailyCurrencyRate> projectionData = null;
        try {
            if (contextData.containsKey(CONTEXT_DATA_PROJECTION)) {
                projectionData = (List<DailyCurrencyRate>)
                        contextData.get(ConsoleCommand.CONTEXT_DATA_PROJECTION);
            }
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("The context data for command is invalid");
        }
        if (projectionData == null) {
            throw new IllegalArgumentException("The context data for command is invalid");
        }
        return projectionData;
    }

    private ProjectionAlgorithmType getProjectionAlgorithmTypeParam(TgBotCommand command) {
        String algorithmText = command.getParameters().get(TgBotCommandPattern.
                COMMAND_ARGUMENT_ALGORITHM).toLowerCase();
        if (projectionAlgorithmsTexts
                .containsKey(algorithmText)) {
            return projectionAlgorithmsTexts.get(algorithmText);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private LocalDateTime getTargetDateParam(TgBotCommand command) {
        String targetDateText = command.getParameters().get(TgBotCommandPattern.COMMAND_ARGUMENT_DATE).toLowerCase();
        if (targetDateText.equals(SINGLE_DATE_PARAM_TOMORROW)) {
            return LocalDate.now().plusDays(1).atStartOfDay();
        } else {
            return LocalDate.parse(targetDateText, DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay();
        }
    }

    private List<Currency> getCurrencyParam(TgBotCommand command) {
        List<Currency> currencies = new ArrayList<>();
        for (String currencyText : command.getCurrencies()) {
            if (Arrays.stream(Currency.values()).noneMatch(x -> x.toString().equals(currencyText.toUpperCase()))) {
                throw new IllegalArgumentException();
            }
            currencies.add(Currency.valueOf(currencyText.toUpperCase()));
        }
        return currencies;
    }

    private String getParamFromSet(TgBotCommand command, String paramName, Set<String> values) {
        String outputText = command.getParameters().get(paramName).toLowerCase();
        if (values.contains(outputText)) {
            return outputText;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private List<String> formatRatePeriodOutputToList(List<ProjectionDataResponse> dataResponses) {
        List<String> output = new ArrayList<>();
        for (ProjectionDataResponse dataResponse : dataResponses) {
            String currency = dataResponse.getProvidedData().get(0).getCurrencyType().toString();
            output.add("Projection results for currency " + currency + ":");
            output.addAll(dataResponse.format());
        }
        return output;
    }

    private List<String> getHelp() {
        List<String> commandOutput = new ArrayList<>();
        commandOutput.add("Following rate command patterns available:");
        commandOutput.add("rate currency -date time -alg type");
        commandOutput.add("rate currency (up to 5 with , delimiter) -period time -alg type -output type");
        commandOutput.add("Following algorithms available:");
        commandOutput.add("average, mystical, past_year, extrapolate");
        commandOutput.add("Following -date arguments available:");
        commandOutput.add("tomorrow - for projection for tomorrow, or specific date in DD.MM.YYYY format");
        commandOutput.add("Following -period arguments available:");
        commandOutput.add("week, month");
        commandOutput.add("Following -output arguments available:");
        commandOutput.add("list - for text output, graph - for image graph output");
        return commandOutput;
    }

}
