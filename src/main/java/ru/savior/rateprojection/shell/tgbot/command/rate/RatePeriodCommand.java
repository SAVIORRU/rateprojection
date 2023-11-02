package ru.savior.rateprojection.shell.tgbot.command.rate;

import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionDataResponse;
import ru.savior.rateprojection.core.service.ProjectionService;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithmType;
import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;
import ru.savior.rateprojection.shell.tgbot.utils.ChartBuilder;

import java.util.*;

public class RatePeriodCommand extends RateCommand {
    public static final String PERIOD_OUTPUT_PARAM_LIST = "list";
    public static final String PERIOD_OUTPUT_PARAM_GRAPH = "graph";
    public static final String PERIOD_TIME_PARAM_WEEK = "week";
    public static final String PERIOD_TIME_PARAM_MONTH = "month";

    public static final String COMMAND_ARGUMENT_OUTPUT = "output";
    public static final String COMMAND_ARGUMENT_PERIOD = "period";

    private static final Set<String> PERIOD_OUTPUT_PARAM_VALUES = new HashSet<>() {{
        add(PERIOD_OUTPUT_PARAM_LIST);
        add(PERIOD_OUTPUT_PARAM_GRAPH);
    }};
    private static final Set<String> PERIOD_TIME_PARAM_VALUES = new HashSet<>() {{
        add(PERIOD_TIME_PARAM_WEEK);
        add(PERIOD_TIME_PARAM_MONTH);
    }};

    public RatePeriodCommand(Set<Currency> currencies,
                             ProjectionAlgorithmType algorithmType,
                             Map<String, String> additionalParamsRaw) {
        super(TgBotCommandType.RATE_PERIOD, currencies, algorithmType, additionalParamsRaw);
    }

    @Override
    protected List<String> executeRateCommand(List<DailyCurrencyRate> projectionData, ProjectionService projectionService) {
        List<String> commandOutput = new ArrayList<>();
        String periodParam = null;
        String outputParam = null;
        try {
            outputParam = getParamFromSet(super.getAdditionalParamsRaw(), COMMAND_ARGUMENT_OUTPUT,
                    PERIOD_OUTPUT_PARAM_VALUES);
        } catch (RuntimeException exception) {
            commandOutput.add("The following -output parameter value is invalid");
            return commandOutput;
        }
        try {
            periodParam = getParamFromSet(super.getAdditionalParamsRaw(), COMMAND_ARGUMENT_PERIOD,
                    PERIOD_TIME_PARAM_VALUES);
        } catch (RuntimeException exception) {
            commandOutput.add("The following -period parameter value is invalid");
            return commandOutput;
        }
        List<ProjectionDataResponse> responses = new ArrayList<>();
        for (Currency currency : super.getCurrencies()) {
            ProjectionDataResponse dataResponse = null;
            switch (periodParam) {
                case PERIOD_TIME_PARAM_WEEK -> {
                    dataResponse = projectionService
                            .projectForNextWeek(projectionData, currency, super.getAlgorithmType());}
                case PERIOD_TIME_PARAM_MONTH -> {
                    dataResponse = projectionService
                            .projectForNextMonth(projectionData, currency, super.getAlgorithmType());}
            }
            responses.add(dataResponse);
        }
        switch (outputParam) {
            case PERIOD_OUTPUT_PARAM_LIST -> {
                commandOutput.addAll(super.formatRateOutputToList(responses));}
            case PERIOD_OUTPUT_PARAM_GRAPH -> {
                commandOutput.addAll(formatRateOutputToGraph(responses));
            }
        }
        return commandOutput;
    }

    private String getParamFromSet(Map<String, String> rawParams, String paramName, Set<String> values) {
        String outputText = rawParams.get(paramName).toLowerCase();
        if (values.contains(outputText)) {
            return outputText;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private List<String> formatRateOutputToGraph(List<ProjectionDataResponse> dataResponses) {
        List<String> output = new ArrayList<>();
        List<ProjectionDataResponse> successfulResponses = new ArrayList<>();
        for (ProjectionDataResponse response: dataResponses) {
            if (!response.isSuccessful()) {
                output.add("The following currency did not added to graph due to errors:");
                output.addAll(response.format());
            } else {
                successfulResponses.add(response);
            }
        }
        if (successfulResponses.size() > 0) {
            output.addAll(ChartBuilder.buildChartFile(successfulResponses));
        }
        return output;
    }
}
