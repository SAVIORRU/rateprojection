package ru.savior.rateprojection.core.service.algorithm;


import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionDataResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AverageProjection extends ProjectionAlgorithm {

    private static final int DAYS_FOR_AVERAGE = 7;

    public AverageProjection() {
        super(ProjectionAlgorithmType.AVERAGE);
    }

    @Override
    public ProjectionDataResponse projectForWeek(List<DailyCurrencyRate> projectionData) {
        LocalDateTime targetDate = LocalDateTime.now().plusDays(7).withHour(0).
                withMinute(0).withSecond(0).withNano(0);
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
        LocalDateTime targetDate = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
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
            dataResponse.setSuccessful(false);
            return dataResponse;
        }
        LocalDateTime currentDate = projectionData.get(projectionData.size() - 1).getRateDate();
        List<DailyCurrencyRate> toDateProjectionData = new ArrayList<>(projectionData);
        while (targetDate.isAfter(currentDate)) {
            DailyCurrencyRate lastRate = toDateProjectionData.get(toDateProjectionData.size() - 1);
            Double average = collectAverage(toDateProjectionData);
            currentDate = currentDate.plusDays(1);
            toDateProjectionData.add(new DailyCurrencyRate(lastRate.getCurrencyType(), currentDate, average));
        }
        dataResponse.getProvidedData().addAll(toDateProjectionData);
        return dataResponse;
    }

    private Double collectAverage(List<DailyCurrencyRate> projectionData) {
        Double average = 0D;
        for (int i = projectionData.size() - 1; i > projectionData.size() - DAYS_FOR_AVERAGE - 1; i--) {
            average += projectionData.get(i).getRate();
        }
        return average / DAYS_FOR_AVERAGE;
    }

}
