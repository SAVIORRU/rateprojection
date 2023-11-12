package ru.savior.rateprojection.core.entity;

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

    private List<DailyCurrencyRate> providedData = new ArrayList<>();

    private List<String> log = new ArrayList<>();

    private boolean isSuccessful;

    public ProjectionDataResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

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
