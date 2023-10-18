package ru.savior.rateprojection.core.service.algorithm;


import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AverageProjection extends ProjectionAlgorithm {

    private static final int DAYS_FOR_AVERAGE = 7;

    public AverageProjection() {
        super(ProjectionAlgorithmType.AVERAGE);
    }

    @Override
    public List<DailyCurrencyRate> projectForWeek(List<DailyCurrencyRate> projectionData) {
        LocalDateTime targetDate = LocalDateTime.now().plusDays(7).withHour(0).
                withMinute(0).withSecond(0).withNano(0);
        List<DailyCurrencyRate> completedProjectionData = projectForDate(projectionData, targetDate);

        return completedProjectionData.
                subList(completedProjectionData.size() - 7, completedProjectionData.size());
    }

    @Override
    public DailyCurrencyRate projectForNextDay(List<DailyCurrencyRate> projectionData) {
        LocalDateTime targetDate = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<DailyCurrencyRate> completedProjectionData = projectForDate(projectionData, targetDate);
        return completedProjectionData.get(completedProjectionData.size() - 1);
    }

    private List<DailyCurrencyRate> projectForDate(List<DailyCurrencyRate> projectionData, LocalDateTime targetDate) {
        if (projectionData.size() < DAYS_FOR_AVERAGE) {
            throw new IllegalArgumentException("Not enough data for projection");
        }
        LocalDateTime currentDate = projectionData.get(projectionData.size() -1).getRateDate();
        List<DailyCurrencyRate> toDateProjectionData = new ArrayList<>(projectionData);
        while (targetDate.isAfter(currentDate)) {
            DailyCurrencyRate lastRate = toDateProjectionData.get(toDateProjectionData.size() - 1);
            Double average = collectAverage(toDateProjectionData);
            currentDate = currentDate.plusDays(1);
            toDateProjectionData.add(new DailyCurrencyRate(lastRate.getCurrencyType(), currentDate, average));
        }
        return toDateProjectionData;
    }

    private Double collectAverage(List<DailyCurrencyRate> projectionData) {
        Double average = 0D;
        for (int i = projectionData.size() - 1; i > projectionData.size() - DAYS_FOR_AVERAGE - 1 ; i--) {
            average += projectionData.get(i).getRate();
        }
        return average / DAYS_FOR_AVERAGE;
    }

}
