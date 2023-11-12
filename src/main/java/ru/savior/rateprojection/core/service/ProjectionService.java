package ru.savior.rateprojection.core.service;

import ru.savior.rateprojection.core.entity.ProjectionDataResponse;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.enums.ProjectionAlgorithmType;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

public interface ProjectionService {
    public ProjectionDataResponse projectForNextDay(List<DailyCurrencyRate> projectionData,
                                                    Currency currency, ProjectionAlgorithmType algorithmType);

    public ProjectionDataResponse projectForNextWeek(List<DailyCurrencyRate> projectionData,
                                                     Currency currency, ProjectionAlgorithmType algorithmType);

    public ProjectionDataResponse projectForNextMonth(List<DailyCurrencyRate> projectionData,
                                                      Currency currency, ProjectionAlgorithmType algorithmType);

    public ProjectionDataResponse projectForSpecificDate(List<DailyCurrencyRate> projectionData, Currency currency,
                                                         ProjectionAlgorithmType algorithmType, LocalDateTime targetDate);
}
