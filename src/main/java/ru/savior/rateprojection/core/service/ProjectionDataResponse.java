package ru.savior.rateprojection.core.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
public class ProjectionDataResponse {

    private List<DailyCurrencyRate> providedData;

    private List<String> log;

    private boolean isSuccessful;

    public List<String> format() {
        List<String> output = new ArrayList<>();
        if (isSuccessful()) {
            for (DailyCurrencyRate dailyCurrencyRate : providedData) {
                output.add(dailyCurrencyRate.format());
            }
        } else {
            output.addAll(log);
        }
        return output;
    }

}
