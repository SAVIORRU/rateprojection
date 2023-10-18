package ru.savior.rateprojection.core.service;

import lombok.*;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;

import java.util.List;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class ProjectionDataResponse {

    private final List<DailyCurrencyRate> providedData;

    private final List<String> log;

    @Setter
    private boolean isSuccessful;

}
