package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.entity.ProjectionDataResponse;
import ru.savior.rateprojection.core.service.algorithm.ExtrapolationProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtrapolationProjectionTest {
    private ExtrapolationProjection extrapolationProjection;

    @BeforeEach
    public void setUp() {
        extrapolationProjection = new ExtrapolationProjection();
    }

    @Test
    public void given_ValidData_whne_projectForWeek_successfulResponse() {
        List<DailyCurrencyRate> projectionData = createSampleProjectionData();

        ProjectionDataResponse response = extrapolationProjection.projectForDate(projectionData,
                LocalDate.now().plusDays(7).atStartOfDay());

        assertTrue(response.isSuccessful());
        assertEquals(7, response.getProvidedData().size());
        assertEquals(0, projectionData.get(0).getRate().compareTo(response.getProvidedData().get(0).getRate()));
    }

    @Test
    public void given_ValidData_when_projectForMonth_successfulResponse() {
        List<DailyCurrencyRate> projectionData = createSampleProjectionData();

        ProjectionDataResponse response = extrapolationProjection.projectForDate(projectionData,
                LocalDate.now().plusMonths(1).atStartOfDay());

        assertTrue(response.isSuccessful());
        assertEquals(0, projectionData.get(0).getRate().compareTo(response.getProvidedData().get(0).getRate()));
    }

    @Test
    public void given_ValidData_when_projectForDay_successfulResponse() {
        List<DailyCurrencyRate> projectionData = createSampleProjectionData();

        ProjectionDataResponse response = extrapolationProjection.projectForDate(projectionData,
                LocalDate.now().plusDays(1).atStartOfDay());

        assertTrue(response.isSuccessful());
        assertEquals(1, response.getProvidedData().size());
        assertEquals(0, projectionData.get(0).getRate().compareTo(response.getProvidedData().get(0).getRate()));
    }


    private List<DailyCurrencyRate> createSampleProjectionData() {
        List<DailyCurrencyRate> projectionData = new ArrayList<>();
        LocalDateTime currentDate = LocalDateTime.now().minusDays(1);
        BigDecimal rate = BigDecimal.valueOf(1.0);
        for (int i = 0; i < 32; i++) {
                projectionData.add(new DailyCurrencyRate(Currency.getInstance("USD"), currentDate, rate));
                currentDate = currentDate.minusDays(1);
        }

        return projectionData;
    }
}
