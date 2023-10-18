package core;

import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionService;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithmType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectionServiceTest {



    @Test
    public void projectionServiceTestForOneDayEqualRates() {
        ProjectionService projectionService = new ProjectionService();

        List<DailyCurrencyRate> testData = TestDataGenerator.generateTestProjectionData(10, true);

        DailyCurrencyRate dailyCurrencyRate = projectionService.projectForNextDay(
                testData,
                Currency.USD, ProjectionAlgorithmType.AVERAGE);

        assertEquals(testData.get(0).getRate(), dailyCurrencyRate.getRate());
        assertEquals(dailyCurrencyRate.getRateDate(), LocalDateTime.now().plusDays(1).
                withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    @Test
    public void projectionServiceTestForWeekEqualRates() {
        ProjectionService projectionService = new ProjectionService();

        List<DailyCurrencyRate> testData = TestDataGenerator.generateTestProjectionData(10, true);

        List<DailyCurrencyRate> dailyCurrencyRates = projectionService.projectForNextWeek(
                testData,
                Currency.USD, ProjectionAlgorithmType.AVERAGE);

        assertEquals(dailyCurrencyRates.get(dailyCurrencyRates.size() - 1).getRate(), testData.get(testData.size() - 1).getRate());
        assertEquals(dailyCurrencyRates.size(), 7);
        assertEquals(dailyCurrencyRates.get(dailyCurrencyRates.size() - 1).getRateDate(), LocalDateTime.now().plusDays(7).
                withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

     static class TestDataGenerator{

        public static List<DailyCurrencyRate> generateTestProjectionData(Integer recordCount , boolean generateEqualRates){
            List<DailyCurrencyRate> testData = new ArrayList<>();
            Double rate = 1D;
            LocalDateTime currentDate = LocalDateTime.of(2023, 9, 15, 0, 0);
            for (int i = 0; i < recordCount; i++) {
                if (!generateEqualRates) {
                    rate = Math.random();
                }
                testData.add(new DailyCurrencyRate(Currency.USD, currentDate, rate));
                currentDate = currentDate.plusDays(1);
            }
            return testData;
        }
    }
}
