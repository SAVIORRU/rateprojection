package ru.savior.rateprojection.core.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;

import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
public class ProjectionDataResponse {

    private List<DailyCurrencyRate> providedData;

    private List<String> log;

    private boolean isSuccessful;

}
