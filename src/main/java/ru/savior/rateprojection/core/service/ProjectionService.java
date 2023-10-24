package ru.savior.rateprojection.core.service;

import lombok.extern.slf4j.Slf4j;
import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.algorithm.AverageProjection;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithm;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithmType;

import java.util.*;

@Slf4j
public class ProjectionService {

    private enum ProjectionTime {
        WEEK, DAY
    }


    private final List<ProjectionAlgorithm> projectionAlgorithms;

    public ProjectionService() {
        this.projectionAlgorithms = new ArrayList<>();
        projectionAlgorithms.add(new AverageProjection());
    }

    public ProjectionDataResponse projectForNextDay(List<DailyCurrencyRate> projectionData,
                                                    Currency currencyType, ProjectionAlgorithmType algorithmType) {
        return project(projectionData, currencyType, algorithmType, ProjectionTime.DAY);
    }

    public ProjectionDataResponse projectForNextWeek(List<DailyCurrencyRate> projectionData,
                                                     Currency currencyType, ProjectionAlgorithmType algorithmType) {
        return project(projectionData, currencyType, algorithmType, ProjectionTime.WEEK);
    }

    private ProjectionDataResponse project(List<DailyCurrencyRate> projectionData, Currency currencyType,
                                           ProjectionAlgorithmType algorithmType, ProjectionTime projectionTime) {
        List<DailyCurrencyRate> completedProjectionData = prepareProjectionData(projectionData, currencyType);
        ProjectionDataResponse dataResponse = null;
        try {
            ProjectionAlgorithm algorithm = getProjectionAlgorithmForType(algorithmType);
            switch (projectionTime) {
                case DAY -> {
                    dataResponse = algorithm.projectForNextDay(completedProjectionData);
                }
                case WEEK -> {
                    dataResponse = algorithm.projectForWeek(completedProjectionData);
                }
            }

        } catch (RuntimeException exception) {
            List<String> responseLog = new ArrayList<>();
            responseLog.add(exception.getMessage());
            log.error(exception.getMessage());
            return new ProjectionDataResponse(new ArrayList<>(), responseLog, false);
        }
        return dataResponse;
    }


    private ProjectionAlgorithm getProjectionAlgorithmForType(ProjectionAlgorithmType algorithmType) {
        Optional<ProjectionAlgorithm> algorithm = projectionAlgorithms.stream()
                .filter(x -> x.getType().compareTo(algorithmType) == 0)
                .findAny();
        if (algorithm.isPresent()) {
            return algorithm.get();
        } else {
            throw new NoSuchElementException("The following projection algorithm not found");
        }
    }

    private List<DailyCurrencyRate> prepareProjectionData(List<DailyCurrencyRate> projectionData,
                                                          Currency currencyType) {
        List<DailyCurrencyRate> preparedProjectionData = new ArrayList<>(projectionData.stream()
                .filter(dailyCurrencyRate -> dailyCurrencyRate.getCurrencyType().compareTo(currencyType) == 0)
                .toList());
        preparedProjectionData.sort(Comparator.comparing(DailyCurrencyRate::getRateDate));
        return preparedProjectionData;
    }


}
