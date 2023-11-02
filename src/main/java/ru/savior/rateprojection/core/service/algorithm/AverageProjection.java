package ru.savior.rateprojection.core.service.algorithm;


import lombok.extern.slf4j.Slf4j;
import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    protected BigDecimal findRateValue(List<DailyCurrencyRate> projectionData, LocalDateTime targetDate) {
        if (projectionData.size() < DAYS_FOR_AVERAGE) {
            log.error("Not enough data for average projection, need {}, provided {}",
                    DAYS_FOR_AVERAGE, projectionData.size());
            throw new IllegalArgumentException("Not enough data for average projection for currency" +
                    projectionData.get(0).getCurrencyType().toString());
        }
        List<DailyCurrencyRate> projectedData = new ArrayList<>(projectionData);
        LocalDateTime currentDate = projectionData.get(projectionData.size() - 1).getRateDate();
        Currency currencyType = projectionData.get(projectionData.size() - 1).getCurrencyType();
        while (targetDate.isAfter(currentDate)) {
            projectedData.add(new DailyCurrencyRate(currencyType, currentDate, collectAverage(projectedData)));
            currentDate = currentDate.plusDays(1);
        }
        return projectedData.get(projectedData.size() - 1).getRate();
    }

    private BigDecimal collectAverage(List<DailyCurrencyRate> projectionData) {
        BigDecimal average = new BigDecimal(0);

        for (int i = projectionData.size() - 1; i > projectionData.size() - DAYS_FOR_AVERAGE - 1; i--) {
            average = average.add(projectionData.get(i).getRate());
        }
        return average.divide(new BigDecimal(DAYS_FOR_AVERAGE), RoundingMode.UP);
    }

}
