package ru.savior.rateprojection.core.service;

import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithmType;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectionService {
    public ProjectionDataResponse projectForNextDay(List<DailyCurrencyRate> projectionData,
                                                    Currency currencyType, ProjectionAlgorithmType algorithmType);

    public ProjectionDataResponse projectForNextWeek(List<DailyCurrencyRate> projectionData,
                                                     Currency currencyType, ProjectionAlgorithmType algorithmType);

    public ProjectionDataResponse projectForNextMonth(List<DailyCurrencyRate> projectionData,
                                                      Currency currencyType, ProjectionAlgorithmType algorithmType);

    public ProjectionDataResponse projectForSpecificDate(List<DailyCurrencyRate> projectionData, Currency currencyType,
                                                         ProjectionAlgorithmType algorithmType, LocalDateTime targetDate);
}
