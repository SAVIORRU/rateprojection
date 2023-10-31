package ru.savior.rateprojection.core.service;

import lombok.extern.slf4j.Slf4j;
import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.algorithm.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class ProjectionServiceImpl implements ProjectionService {


    private final List<ProjectionAlgorithm> projectionAlgorithms;

    public ProjectionServiceImpl() {
        this.projectionAlgorithms = new ArrayList<>();
        projectionAlgorithms.add(new AverageProjection());
        projectionAlgorithms.add(new ExtrapolationProjection());
        projectionAlgorithms.add(new PastYearProjection());
        projectionAlgorithms.add(new MysticalProjection());
    }

    @Override
    public ProjectionDataResponse projectForNextDay(List<DailyCurrencyRate> projectionData,
                                                    Currency currencyType, ProjectionAlgorithmType algorithmType) {
        return project(projectionData, currencyType, algorithmType, LocalDate.now().plusDays(1).atStartOfDay());
    }

    @Override
    public ProjectionDataResponse projectForNextWeek(List<DailyCurrencyRate> projectionData,
                                                     Currency currencyType, ProjectionAlgorithmType algorithmType) {
        return project(projectionData, currencyType, algorithmType, LocalDate.now().plusDays(7).atStartOfDay());
    }


    @Override
    public ProjectionDataResponse projectForNextMonth(List<DailyCurrencyRate> projectionData,
                                                      Currency currencyType, ProjectionAlgorithmType algorithmType) {
        return project(projectionData, currencyType, algorithmType, LocalDate.now().plusMonths(1).atStartOfDay());
    }

    @Override
    public ProjectionDataResponse projectForSpecificDate(List<DailyCurrencyRate> projectionData,
                                                         Currency currencyType, ProjectionAlgorithmType algorithmType,
                                                         LocalDateTime targetDate) {
        ProjectionDataResponse dataResponse = project(projectionData, currencyType, algorithmType, targetDate);
        if (dataResponse.isSuccessful()) {
            DailyCurrencyRate singleDateRate = dataResponse.getProvidedData()
                    .get(dataResponse.getProvidedData().size() - 1);
            dataResponse.getProvidedData().clear();
            dataResponse.getProvidedData().add(singleDateRate);
        }
        return project(projectionData, currencyType, algorithmType, targetDate);
    }

    private ProjectionDataResponse project(List<DailyCurrencyRate> projectionData, Currency currencyType,
                                           ProjectionAlgorithmType algorithmType, LocalDateTime targetDate) {
        List<DailyCurrencyRate> completedProjectionData = prepareProjectionData(projectionData, currencyType);
        ProjectionDataResponse dataResponse = null;
        if (completedProjectionData.isEmpty()) {
            log.error("No data for specific currency {} found", currencyType);
            dataResponse = new ProjectionDataResponse(new ArrayList<>(), new ArrayList<>(), false);
            dataResponse.getLog().add("No data for specific currency " + currencyType + " found");
            return dataResponse;
        }
        try {
            ProjectionAlgorithm algorithm = getProjectionAlgorithmForType(algorithmType);
            dataResponse = algorithm.projectForDate(completedProjectionData, targetDate);

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
