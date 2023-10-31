package ru.savior.rateprojection.core.service.algorithm;

import lombok.extern.slf4j.Slf4j;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class PastYearProjection extends ProjectionAlgorithm {

    public PastYearProjection() {
        super(ProjectionAlgorithmType.PAST_YEAR);
    }

    @Override
    protected BigDecimal findRateValue(List<DailyCurrencyRate> projectionData, LocalDateTime targetDate) {
        BigDecimal rate = null;
        LocalDateTime previousYearTargetDate = targetDate.minusYears(1);
        for (int i = 0; i < projectionData.size(); i++) {
            if (projectionData.get(i).getRateDate().isAfter(previousYearTargetDate)) {
                rate = projectionData.get(i - 1).getRate();
                break;
            }
        }
        if (rate == null) {
            log.error("No data found in previous year for past-year projection");
            throw new NoSuchElementException("No data found in previous year for past-year projection");
        }
        return rate;
    }
}
