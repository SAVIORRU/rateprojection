package ru.savior.rateprojection.datasource;

import ru.savior.rateprojection.core.service.ProjectionDataResponse;

import java.util.Map;

public interface DataSource {

    public ProjectionDataResponse provideData();

    public void configure(Map<String, String> settings) throws IllegalArgumentException;

}
