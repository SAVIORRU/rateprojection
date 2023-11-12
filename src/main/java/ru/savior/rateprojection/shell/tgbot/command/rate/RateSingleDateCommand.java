package ru.savior.rateprojection.shell.tgbot.command.rate;

import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.entity.ProjectionDataResponse;
import ru.savior.rateprojection.core.service.ProjectionService;
import ru.savior.rateprojection.core.enums.ProjectionAlgorithmType;
import ru.savior.rateprojection.shell.tgbot.CommandType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RateSingleDateCommand extends RateCommand {
    public static final String SINGLE_DATE_PARAM_TOMORROW = "tomorrow";
    public static final String COMMAND_ARGUMENT_DATE = "date";

    public RateSingleDateCommand(Set<Currency> currencies,
                                 ProjectionAlgorithmType algorithmType,
                                 Map<String, String> additionalParamsRaw) {
        super(CommandType.RATE_SINGLE_DATE, currencies, algorithmType, additionalParamsRaw);
    }

    @Override
    protected List<String> executeRateCommand(List<DailyCurrencyRate> projectionData, ProjectionService projectionService) {
        List<String> commandOutput = new ArrayList<>();
        LocalDateTime targetDate = null;
        try {
            targetDate = getTargetDateParam(super.getAdditionalParamsRaw());
        } catch (RuntimeException exception) {
            commandOutput.add("The following -date parameter value is invalid");
            return commandOutput;
        }
        List<ProjectionDataResponse> dataResponses = new ArrayList<>();
        for (Currency currency : super.getCurrencies()) {
            dataResponses.add(projectionService.projectForSpecificDate(projectionData, currency,
                    super.getAlgorithmType(), targetDate));
        }
        commandOutput.addAll(super.formatRateOutputToList(dataResponses));
        return commandOutput;
    }

    private LocalDateTime getTargetDateParam(Map<String, String> rawParams) {
        String targetDateText = rawParams.get(COMMAND_ARGUMENT_DATE).toLowerCase();
        if (targetDateText.equals(SINGLE_DATE_PARAM_TOMORROW)) {
            return LocalDate.now().plusDays(1).atStartOfDay();
        } else {
            return LocalDate.parse(targetDateText, DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay();
        }
    }
}
