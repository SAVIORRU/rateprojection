package ru.savior.rateprojection.core.service.algorithm;


import lombok.extern.slf4j.Slf4j;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionDataResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class AverageProjection extends ProjectionAlgorithm {

    private static final int DAYS_FOR_AVERAGE = 7;

    public AverageProjection() {
        super(ProjectionAlgorithmType.AVERAGE);
    }

    @Override
    public ProjectionDataResponse projectForWeek(List<DailyCurrencyRate> projectionData) {
        LocalDateTime targetDate = LocalDate.now().plusDays(7).atStartOfDay();
        ProjectionDataResponse dataResponse = projectForDate(projectionData, targetDate);
        if (dataResponse.isSuccessful()) {
            List<DailyCurrencyRate> projectionDataForWeek = dataResponse.getProvidedData().subList(
                    dataResponse.getProvidedData().size() - 7, dataResponse.getProvidedData().size());
            dataResponse.setProvidedData(projectionDataForWeek);
        }
        return dataResponse;
    }

    @Override
    public ProjectionDataResponse projectForNextDay(List<DailyCurrencyRate> projectionData) {
        LocalDateTime targetDate = LocalDate.now().plusDays(1).atStartOfDay();
        ProjectionDataResponse dataResponse = projectForDate(projectionData, targetDate);
        if (dataResponse.isSuccessful()) {
            DailyCurrencyRate dailyCurrencyRate = dataResponse.getProvidedData().
                    get(dataResponse.getProvidedData().size() - 1);
            dataResponse.setProvidedData(new ArrayList<>());
            dataResponse.getProvidedData().add(dailyCurrencyRate);
        }

        return dataResponse;
    }

    private ProjectionDataResponse projectForDate(List<DailyCurrencyRate> projectionData, LocalDateTime targetDate) {
        ProjectionDataResponse dataResponse = new ProjectionDataResponse(new ArrayList<>(), new ArrayList<>(), true);
        if (projectionData.size() < DAYS_FOR_AVERAGE) {
            dataResponse.getLog().add("Not enough data for projection");
            log.error("Not enough data for average projection, need {}, provided {}",
                    DAYS_FOR_AVERAGE, projectionData.size());
            dataResponse.setSuccessful(false);
            return dataResponse;
        }
        LocalDateTime currentDate = projectionData.get(projectionData.size() - 1).getRateDate();
        List<DailyCurrencyRate> toDateProjectionData = new ArrayList<>(projectionData);
        while (targetDate.isAfter(currentDate)) {
            DailyCurrencyRate lastRate = toDateProjectionData.get(toDateProjectionData.size() - 1);
            BigDecimal average = collectAverage(toDateProjectionData);
            currentDate = currentDate.plusDays(1);
            toDateProjectionData.add(new DailyCurrencyRate(lastRate.getCurrencyType(), currentDate, average));
        }
        dataResponse.getProvidedData().addAll(toDateProjectionData);
        return dataResponse;
    }

    private BigDecimal collectAverage(List<DailyCurrencyRate> projectionData) {
        BigDecimal average = new BigDecimal("0");
        for (int i = projectionData.size() - 1; i > projectionData.size() - DAYS_FOR_AVERAGE - 1; i--) {
            average = average.add(projectionData.get(i).getRate());
        }
        return average.divide(new BigDecimal(DAYS_FOR_AVERAGE), RoundingMode.UP);
    }

}
