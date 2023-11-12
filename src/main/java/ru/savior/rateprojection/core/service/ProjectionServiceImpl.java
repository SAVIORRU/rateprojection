package ru.savior.rateprojection.core.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.savior.rateprojection.core.entity.ProjectionDataResponse;
import ru.savior.rateprojection.core.enums.ProjectionAlgorithmType;
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
        projectionAlgorithms.add(new MysticalProjection(new Random()));
    }

    @Override
    public ProjectionDataResponse projectForNextDay(List<DailyCurrencyRate> projectionData,
                                                    Currency currency, ProjectionAlgorithmType algorithmType) {
        return project(projectionData, currency, algorithmType, LocalDate.now().plusDays(1).atStartOfDay());
    }

    @Override
    public ProjectionDataResponse projectForNextWeek(List<DailyCurrencyRate> projectionData,
                                                     Currency currency, ProjectionAlgorithmType algorithmType) {
        return project(projectionData, currency, algorithmType, LocalDate.now().plusDays(7).atStartOfDay());
    }


    @Override
    public ProjectionDataResponse projectForNextMonth(List<DailyCurrencyRate> projectionData,
                                                      Currency currency, ProjectionAlgorithmType algorithmType) {
        return project(projectionData, currency, algorithmType, LocalDate.now().plusMonths(1).atStartOfDay());
    }

    @Override
    public ProjectionDataResponse projectForSpecificDate(List<DailyCurrencyRate> projectionData,
                                                         Currency currency, ProjectionAlgorithmType algorithmType,
                                                         LocalDateTime targetDate) {
        ProjectionDataResponse dataResponse = project(projectionData, currency, algorithmType, targetDate);
        if (dataResponse.isSuccessful()) {
            dataResponse = getProjectionResponseWithLastRate(dataResponse);
        }
        return dataResponse;
    }

    private ProjectionDataResponse project(List<DailyCurrencyRate> projectionData, Currency currency,
                                           ProjectionAlgorithmType algorithmType, LocalDateTime targetDate) {
        List<DailyCurrencyRate> completedProjectionData = prepareProjectionData(projectionData, currency);
        ProjectionDataResponse dataResponse;
        if (completedProjectionData.isEmpty()) {
            log.error("No data for specific currency {} found", currency);
            dataResponse = new ProjectionDataResponse(false);
            dataResponse.getLog().add("No data for specific currency " + currency + " found");
            return dataResponse;
        }
        try {
            ProjectionAlgorithm algorithm = getProjectionAlgorithmForType(algorithmType);
            dataResponse = algorithm.projectForDate(completedProjectionData, targetDate);

        } catch (RuntimeException exception) {
            List<String> responseLog = new ArrayList<>();
            responseLog.add(exception.getMessage());
            log.error(exception.getMessage());
            log.debug(ExceptionUtils.getStackTrace(exception));
            return new ProjectionDataResponse(new ArrayList<>(), responseLog, false);
        }
        return dataResponse;
    }


    private ProjectionAlgorithm getProjectionAlgorithmForType(ProjectionAlgorithmType algorithmType) {
        Optional<ProjectionAlgorithm> algorithm = projectionAlgorithms.stream()
                .filter(x -> x.getType().compareTo(algorithmType) == 0)
                .findAny();
        return algorithm.orElseThrow(() ->  new NoSuchElementException("The following projection algorithm not found"));
    }

    private List<DailyCurrencyRate> prepareProjectionData(List<DailyCurrencyRate> projectionData,
                                                          Currency currency) {
        List<DailyCurrencyRate> preparedProjectionData = new ArrayList<>(projectionData.stream()
                .filter(dailyCurrencyRate -> dailyCurrencyRate.getCurrency().equals(currency))
                .toList());
        preparedProjectionData.sort(Comparator.comparing(DailyCurrencyRate::getRateDate));
        return preparedProjectionData;
    }

    private ProjectionDataResponse getProjectionResponseWithLastRate(ProjectionDataResponse dataResponse) {
        ProjectionDataResponse responseWithLastRate = new ProjectionDataResponse(new ArrayList<>(),
                dataResponse.getLog(), dataResponse.isSuccessful());
        DailyCurrencyRate lastDateRate = dataResponse.getProvidedData()
                .get(dataResponse.getProvidedData().size() - 1);
        responseWithLastRate.getProvidedData().add(lastDateRate);
        return responseWithLastRate;
    }

}
