package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionDataResponse;
import ru.savior.rateprojection.core.service.algorithm.MysticalProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MysticalProjectionTest {
    private MysticalProjection mysticalProjection;

    @BeforeEach
    public void setUp() {
        mysticalProjection = new MysticalProjection();
    }

    @Test
    public void given_ValidData_whne_projectForWeek_successfulResponse() {
        List<DailyCurrencyRate> projectionData = createSampleProjectionData();

        ProjectionDataResponse response = mysticalProjection.projectForWeek(projectionData);

        assertTrue(response.isSuccessful());
        assertEquals(7, response.getProvidedData().size());
        assertEquals(0, projectionData.get(0).getRate().compareTo(response.getProvidedData().get(0).getRate()));
    }

    @Test
    public void given_ValidData_when_projectForMonth_successfulResponse() {
        List<DailyCurrencyRate> projectionData = createSampleProjectionData();

        ProjectionDataResponse response = mysticalProjection.projectForMonth(projectionData);

        assertTrue(response.isSuccessful());
        assertEquals(0, projectionData.get(0).getRate().compareTo(response.getProvidedData().get(0).getRate()));
    }

    @Test
    public void given_ValidData_when_projectForDay_successfulResponse() {
        List<DailyCurrencyRate> projectionData = createSampleProjectionData();

        ProjectionDataResponse response = mysticalProjection.projectForNextDay(projectionData);

        assertTrue(response.isSuccessful());
        assertEquals(1, response.getProvidedData().size());
        assertEquals(0, projectionData.get(0).getRate().compareTo(response.getProvidedData().get(0).getRate()));
    }


    private List<DailyCurrencyRate> createSampleProjectionData() {
        List<DailyCurrencyRate> projectionData = new ArrayList<>();
        LocalDateTime currentDate = LocalDateTime.now().minusYears(1);
        BigDecimal rate = BigDecimal.valueOf(1.0);
        for (int i = 0; i < 4; i++) {
            while (currentDate.isBefore(LocalDate.now().atStartOfDay())) {
                projectionData.add(new DailyCurrencyRate(Currency.USD, currentDate.minusYears(i), rate));
                currentDate = currentDate.plusDays(1);
            }
        }

        return projectionData;
    }
}
