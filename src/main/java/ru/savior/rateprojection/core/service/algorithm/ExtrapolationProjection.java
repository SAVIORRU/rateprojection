package ru.savior.rateprojection.core.service.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math.stat.regression.SimpleRegression;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExtrapolationProjection extends ProjectionAlgorithm {

    private static final int DAYS_FOR_EXTRAPOLATION = 30;

    public ExtrapolationProjection() {
        super(ProjectionAlgorithmType.EXTRAPOLATE);
    }

    @Override
    protected BigDecimal findRateValue(List<DailyCurrencyRate> projectionData, LocalDateTime targetDate) {
        if (projectionData.size() < DAYS_FOR_EXTRAPOLATION) {
                log.error("Not enough data for extrapolation projection, need {}, provided {}",
                        DAYS_FOR_EXTRAPOLATION, projectionData.size());
                throw new IllegalArgumentException("Not enough data for extrapolation projection for currency" +
                        projectionData.get(0).getCurrencyType().toString());
        }
        List<BigDecimal> xArgsForRegression = new ArrayList<>();
        List<BigDecimal> yArgsForRegression = new ArrayList<>();
        Period period = Period.between(projectionData.get(projectionData.size() - 1).getRateDate().toLocalDate(),
                targetDate.plusDays(1).toLocalDate());
        int daysCount = period.getDays();
        for (int i = 0; i < projectionData.size(); i++) {
            xArgsForRegression.add(new BigDecimal(i + 1));
            yArgsForRegression.add(projectionData.get(i).getRate());
        }
        LinearRegression regression = new LinearRegression(xArgsForRegression.toArray(new BigDecimal[0]),
                yArgsForRegression.toArray(new BigDecimal[0]));

        return regression.predict(new BigDecimal(daysCount + projectionData.size()));
    }

    private class LinearRegression {
        private final BigDecimal intercept, slope;


        public LinearRegression(BigDecimal[] x, BigDecimal[] y) {
            int n = x.length;

            BigDecimal sumx = new BigDecimal(0);
            BigDecimal sumy = new BigDecimal(0);
            BigDecimal sumx2 = new BigDecimal(0);
            for (int i = 0; i < n; i++) {
                sumx = sumx.add(x[i]);
                sumx2 = sumx2.add(x[i].multiply(x[i]));
                sumy = sumy.add(y[i]);
            }
            BigDecimal xbar = sumx.divide(new BigDecimal(n), RoundingMode.UP);
            BigDecimal ybar = sumy.divide(new BigDecimal(n), RoundingMode.UP);

            BigDecimal xxbar = new BigDecimal(0);
            BigDecimal yybar = new BigDecimal(0);
            BigDecimal xybar = new BigDecimal(0);
            for (int i = 0; i < n; i++) {
                xxbar = xxbar.add(x[i].subtract(xbar).pow(2));
                yybar = yybar.add(y[i].subtract(ybar).pow(2));
                xybar = xybar.add(x[i].subtract(xbar).multiply(y[i].subtract(ybar)));
            }
            slope = xybar.divide(xxbar, RoundingMode.UP);
            intercept = ybar.subtract(slope.multiply(xbar));

        }

        public BigDecimal predict(BigDecimal x) {
            return intercept.add(slope.multiply(x));
        }
    }
}
