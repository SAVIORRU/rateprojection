package ru.savior.rateprojection.shell.tgbot.command.rate;

import lombok.AccessLevel;
import lombok.Getter;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.entity.ProjectionDataResponse;
import ru.savior.rateprojection.core.service.ProjectionService;
import ru.savior.rateprojection.core.enums.ProjectionAlgorithmType;
import ru.savior.rateprojection.shell.tgbot.CommandType;
import ru.savior.rateprojection.shell.tgbot.ContextConstants;
import ru.savior.rateprojection.shell.tgbot.command.CommandImpl;

import java.util.*;


public abstract class RateCommand extends CommandImpl {
    public static final String CONTEXT_DATA_PROJECTION = ContextConstants.CONTEXT_DATA_PROJECTION;
    public static final String CONTEXT_PROJECTION_SERVICE = ContextConstants.CONTEXT_PROJECTION_SERVICE;

    public static final String COMMAND_WORD = "rate";
    public static final String COMMAND_ARGUMENT_ALGORITHM = "alg";
    private static final Map<String, ProjectionAlgorithmType> projectionAlgorithmsTexts =
            new HashMap<>() {{
                put("average", ProjectionAlgorithmType.AVERAGE);
                put("past_year", ProjectionAlgorithmType.PAST_YEAR);
                put("mystical", ProjectionAlgorithmType.MYSTICAL);
                put("extrapolate", ProjectionAlgorithmType.EXTRAPOLATE);
            }};
    @Getter(AccessLevel.PROTECTED)
    private final Set<Currency> currencies;
    @Getter(AccessLevel.PROTECTED)
    private final ProjectionAlgorithmType algorithmType;
    @Getter(AccessLevel.PROTECTED)
    private final Map<String, String> additionalParamsRaw;

    public RateCommand(CommandType commandType,
                       Set<Currency> currencies,
                       ProjectionAlgorithmType algorithmType,
                       Map<String, String> additionalParamsRaw) {
        super(commandType);
        this.currencies = currencies;
        this.algorithmType = algorithmType;
        this.additionalParamsRaw = additionalParamsRaw;
    }

    public static Map<String, ProjectionAlgorithmType> getProjectionAlgorithmsTexts(){
        return new HashMap<>(projectionAlgorithmsTexts);
    }

    @Override
    public List<String> execute(Map<String, Object> context) {
        List<String> commandOutput = new ArrayList<>();
        List<DailyCurrencyRate> projectionData = null;
        ProjectionService projectionService = null;
        try {
            projectionData = getProjectionDataFromContext(context);
            projectionService = getProjectionServiceFromContext(context);
        } catch (RuntimeException exception) {
            commandOutput.add(exception.getMessage());
            return commandOutput;
        }
        commandOutput.addAll(executeRateCommand(projectionData, projectionService));
        return commandOutput;
    }

    protected List<String> formatRateOutputToList(List<ProjectionDataResponse> dataResponses) {
        List<String> output = new ArrayList<>();
        for (ProjectionDataResponse dataResponse : dataResponses) {
            if (dataResponse.isSuccessful()) {
                String currency = dataResponse.getProvidedData().get(0).getCurrency().getCurrencyCode();
                output.add("Projection results for currency " + currency + ":");
            }
            output.addAll(dataResponse.format());
        }
        return output;
    }

    protected abstract List<String> executeRateCommand(List<DailyCurrencyRate> projectionData,
                                                       ProjectionService projectionService);

    private List<DailyCurrencyRate> getProjectionDataFromContext(Map<String, Object> contextData) {
        return (List<DailyCurrencyRate>) getDataFromContext(contextData, CONTEXT_DATA_PROJECTION);
    }

    private ProjectionService getProjectionServiceFromContext(Map<String, Object> contextData) {
        return (ProjectionService) getDataFromContext(contextData, CONTEXT_PROJECTION_SERVICE);
    }
}
