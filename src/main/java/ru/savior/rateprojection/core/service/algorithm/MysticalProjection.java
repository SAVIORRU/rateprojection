package ru.savior.rateprojection.core.service.algorithm;

import lombok.extern.slf4j.Slf4j;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
public class MysticalProjection extends ProjectionAlgorithm {


    public MysticalProjection() {
        super(ProjectionAlgorithmType.MYSTICAL);
    }


    @Override
    protected BigDecimal findRateValue(List<DailyCurrencyRate> projectionData, LocalDateTime targetDate) {
        BigDecimal rate = null;
        List<BigDecimal> previousYearsRates = projectionData
                .stream()
                .filter(x -> x.getRateDate().getMonth() == targetDate.getMonth() &&
                        x.getRateDate().getDayOfMonth() == targetDate.getDayOfMonth() &&
                        x.getRateDate().getYear() < targetDate.getYear())
                .map(DailyCurrencyRate::getRate)
                .toList();
        if (previousYearsRates.isEmpty()) {
            log.error("Not enough data for mystical projection, need at least 1");
            throw new IllegalArgumentException("Not enough data for mystical projection for currency" +
                    projectionData.get(0).getCurrencyType().toString());
        }
        Random random = new Random();
        int randomIndex = random.nextInt(0, previousYearsRates.size());
        return previousYearsRates.get(randomIndex);
    }
}
