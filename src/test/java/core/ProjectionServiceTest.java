package core;

import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.entity.ProjectionDataResponse;
import ru.savior.rateprojection.core.service.ProjectionServiceImpl;
import ru.savior.rateprojection.core.enums.ProjectionAlgorithmType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ProjectionServiceTest {



    @Test
    public void given_validData_when_projectForNextDay_successfulResponse() {
        ProjectionServiceImpl projectionService = new ProjectionServiceImpl();

        List<DailyCurrencyRate> testData = TestDataGenerator.generateTestProjectionData(10, true);

        ProjectionDataResponse dataResponse = projectionService.projectForNextDay(
                testData,
                Currency.getInstance("USD"), ProjectionAlgorithmType.AVERAGE);

        assertEquals(testData.get(0).getRate(), dataResponse.getProvidedData().get(0).getRate());
        assertEquals(dataResponse.getProvidedData().get(0).getRateDate(), LocalDateTime.now().plusDays(1).
                withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    @Test
    public void given_validData_when_projectForNextWeek_successfulResponse() {
        ProjectionServiceImpl projectionService = new ProjectionServiceImpl();

        List<DailyCurrencyRate> testData = TestDataGenerator.generateTestProjectionData(10, true);

        ProjectionDataResponse dataResponse = projectionService.projectForNextWeek(
                testData,
                Currency.getInstance("USD"), ProjectionAlgorithmType.AVERAGE);

        assertEquals(dataResponse.getProvidedData().get(dataResponse.getProvidedData().size() - 1).
                getRate(), testData.get(testData.size() - 1).getRate());
        assertEquals( 7, dataResponse.getProvidedData().size());
        assertEquals(dataResponse.getProvidedData().get(dataResponse.getProvidedData().size() - 1).
                getRateDate(), LocalDateTime.now().plusDays(7).
                withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

     static class TestDataGenerator{

        public static List<DailyCurrencyRate> generateTestProjectionData(Integer recordCount , boolean generateEqualRates){
            List<DailyCurrencyRate> testData = new ArrayList<>();
            BigDecimal rate = new BigDecimal("1");
            LocalDateTime currentDate = LocalDateTime.of(2023, 9, 15, 0, 0);
            for (int i = 0; i < recordCount; i++) {
                if (!generateEqualRates) {
                    rate = BigDecimal.valueOf(Math.random());
                }
                testData.add(new DailyCurrencyRate(Currency.getInstance("USD"), currentDate, rate));
                currentDate = currentDate.plusDays(1);
            }
            return testData;
        }
    }
}
