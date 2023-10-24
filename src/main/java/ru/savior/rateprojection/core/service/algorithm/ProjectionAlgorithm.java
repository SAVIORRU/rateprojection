package ru.savior.rateprojection.core.service.algorithm;

import lombok.Getter;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionDataResponse;

import java.util.List;

@Getter
public abstract class ProjectionAlgorithm {

    private final ProjectionAlgorithmType type;

    public ProjectionAlgorithm(ProjectionAlgorithmType type) {
        this.type = type;
    }

    public abstract ProjectionDataResponse projectForNextDay(List<DailyCurrencyRate> projectionData);

    public abstract ProjectionDataResponse projectForWeek(List<DailyCurrencyRate> projectionData);
}
