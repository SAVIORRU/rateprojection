package ru.savior.rateprojection.core.service;

import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.algorithm.AverageProjection;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithm;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithmType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProjectionService {

    private final List<ProjectionAlgorithm> projectionAlgorithms;

    public ProjectionService() {
        this.projectionAlgorithms = new ArrayList<>();
        projectionAlgorithms.add(new AverageProjection());
    }

    public ProjectionDataResponse projectForNextDay(List<DailyCurrencyRate> projectionData,
                                               Currency currencyType, ProjectionAlgorithmType algorithmType){
        List<DailyCurrencyRate> completedProjectionData = prepareProjectionData(projectionData, currencyType);
        ProjectionAlgorithm algorithm = projectionAlgorithms.stream().filter(x -> x.getType().compareTo(algorithmType) == 0).
                findAny().get();
        return algorithm.projectForNextDay(completedProjectionData);
    }

    public ProjectionDataResponse projectForNextWeek(List<DailyCurrencyRate> projectionData,
                                               Currency currencyType, ProjectionAlgorithmType algorithmType){
        List<DailyCurrencyRate> completedProjectionData = prepareProjectionData(projectionData, currencyType);
        ProjectionAlgorithm algorithm = projectionAlgorithms.stream().filter(x -> x.getType().compareTo(algorithmType) == 0).
                findAny().get();
        return algorithm.projectForWeek(completedProjectionData);
    }

    private List<DailyCurrencyRate> prepareProjectionData(List<DailyCurrencyRate> projectionData,
                                                          Currency currencyType){
        List<DailyCurrencyRate> preparedProjectionData = new ArrayList<>(projectionData.
                stream().filter(dailyCurrencyRate -> dailyCurrencyRate.getCurrencyType().compareTo(currencyType) == 0).
                toList());
        preparedProjectionData.sort(new Comparator<DailyCurrencyRate>() {
            @Override
            public int compare(DailyCurrencyRate o1, DailyCurrencyRate o2) {
                return o1.getRateDate().compareTo(o2.getRateDate());
            }
        });
        return preparedProjectionData;
    }
}
