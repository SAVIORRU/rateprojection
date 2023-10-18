package ru.savior.rateprojection.core.service.algorithm;

import lombok.Getter;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;

import java.util.List;

@Getter
public abstract class ProjectionAlgorithm {

    public abstract DailyCurrencyRate projectForNextDay(List<DailyCurrencyRate> projectionData);

    public abstract List<DailyCurrencyRate> projectForWeek(List<DailyCurrencyRate> projectionData);

    private final ProjectionAlgorithmType type;

    public ProjectionAlgorithm(ProjectionAlgorithmType type) {
        this.type = type;
    }
}
