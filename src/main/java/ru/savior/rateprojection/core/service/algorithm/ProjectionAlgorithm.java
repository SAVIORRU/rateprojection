package ru.savior.rateprojection.core.service.algorithm;

import lombok.Getter;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionDataResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class ProjectionAlgorithm {

    private final ProjectionAlgorithmType type;

    protected ProjectionAlgorithm(ProjectionAlgorithmType type) {
        this.type = type;
    }


    public ProjectionDataResponse projectForWeek(List<DailyCurrencyRate> projectionData) {
        LocalDateTime targetDate = LocalDate.now().plusDays(7).atStartOfDay();
        return projectForDate(projectionData, targetDate);
    }


    public ProjectionDataResponse projectForMonth(List<DailyCurrencyRate> projectionData) {
        LocalDateTime targetDate = LocalDate.now().plusMonths(1).atStartOfDay();
        return projectForDate(projectionData, targetDate);
    }


    public ProjectionDataResponse projectForNextDay(List<DailyCurrencyRate> projectionData) {
        LocalDateTime targetDate = LocalDate.now().plusDays(1).atStartOfDay();
        return projectForDate(projectionData, targetDate);
    }

    public ProjectionDataResponse projectForDate(List<DailyCurrencyRate> projectionData, LocalDateTime targetDate) {
        ProjectionDataResponse dataResponse = new ProjectionDataResponse(new ArrayList<>(), new ArrayList<>(),
                true);
        if (targetDate.toLocalDate().atStartOfDay().isBefore(LocalDate.now().atStartOfDay())
                || targetDate.toLocalDate().atStartOfDay().isEqual(LocalDate.now().atStartOfDay())) {
            dataResponse.getLog().add("Specific date cannot be before or equal current date");
            dataResponse.setSuccessful(false);
            return dataResponse;
        }
        LocalDateTime currentDate = LocalDate.now().plusDays(1).atStartOfDay();
        while (targetDate.isAfter(currentDate) || targetDate.isEqual(currentDate)) {
                try {
                    DailyCurrencyRate lastRate = projectionData.get(projectionData.size() - 1);
                    BigDecimal rate = findRateValue(projectionData, currentDate);
                    dataResponse.getProvidedData().add(new DailyCurrencyRate(lastRate.getCurrencyType(), currentDate,
                            rate));

                } catch (RuntimeException exception) {
                    dataResponse.setSuccessful(false);
                    dataResponse.getLog().add(exception.getMessage());
                    return dataResponse;
                }
            currentDate = currentDate.plusDays(1);
        }
        return dataResponse;
    }

    protected abstract BigDecimal findRateValue(List<DailyCurrencyRate> projectionData, LocalDateTime targetDate);
}
