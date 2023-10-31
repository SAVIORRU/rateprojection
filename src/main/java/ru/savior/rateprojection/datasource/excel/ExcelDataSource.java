package ru.savior.rateprojection.datasource.excel;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionDataResponse;
import ru.savior.rateprojection.datasource.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class ExcelDataSource implements DataSource {

    private static final String FOLDER_SETTING_NAME = "excelFilesFolder";
    private static final Map<String, Currency> currencyTexts = new HashMap<>() {{
        put("турецкая лира", Currency.TRY);
        put("доллар сша", Currency.USD);
        put("евро", Currency.EUR);
        put("болгарский лев", Currency.BGN);
        put("армянский драм", Currency.AMD);
    }};
    private static final String EXCEL_FIELD_NOMINAL = "nominal";
    private static final String EXCEL_FIELD_DATE = "data";
    private static final String EXCEL_FIELD_RATE = "curs";
    private static final String EXCEL_FIELD_CURRENCY = "cdx";

    private String fileFolderPath;

    public ExcelDataSource() {

    }

    @Override
    public ProjectionDataResponse provideData() {
        ProjectionDataResponse dataSourceResponse = new ProjectionDataResponse(new ArrayList<>(), new ArrayList<>(), false);
        log.info("Providing data from Excel data source...");
        if (!validateDirectory(fileFolderPath)) {
            log.error("Excel file directory {} is invalid", fileFolderPath);
            dataSourceResponse.getLog().add("The data source is not configured or its folder is invalid");
            return dataSourceResponse;
        }
        List<String> filesToRead = scanDirectoryForFiles(fileFolderPath);
        if (filesToRead.isEmpty()) {
            dataSourceResponse.getLog().add("No such files for data extraction");
            log.warn("No Excel files found for data extraction in folder {}", fileFolderPath);
            return dataSourceResponse;
        }
        return extractDataFromFiles(filesToRead);
    }

    private ProjectionDataResponse extractDataFromFiles(List<String> filesToRead) {
        ProjectionDataResponse dataSourceResponse = new ProjectionDataResponse(new ArrayList<>(), new ArrayList<>(), false);
        int validFilesCount = 0;
        for (String filePath : filesToRead) {
            try {
                dataSourceResponse.getProvidedData().addAll(extractDataFromFile(filePath));
            } catch (IOException ioEx) {
                dataSourceResponse.getLog().add("The following file " + filePath + " has I|O error during runtime");
            } catch (RuntimeException runEx) {
                dataSourceResponse.getLog().add("The following file " +
                        filePath + " has data conversion errors during execution");
            }
            validFilesCount++;
        }
        if (validFilesCount > 0) {
            dataSourceResponse.setSuccessful(true);
            log.info("Successfully loaded {} Excel files with {} rows total",
                    validFilesCount, dataSourceResponse.getProvidedData().size());
            dataSourceResponse.getLog().add("Successfully loaded " + validFilesCount + " Excel files with " +
                    dataSourceResponse.getProvidedData().size() + " rows total");
        } else {
            dataSourceResponse.setSuccessful(false);
            dataSourceResponse.getLog().add("No valid data has been found in " + filesToRead.size() + " files");
            log.warn("No valid data has been found in {} files", filesToRead.size());
        }
        return dataSourceResponse;
    }

    @Override
    public void configure(@NonNull Map<String, String> settings) throws IllegalArgumentException {

        if (settings.containsKey(FOLDER_SETTING_NAME)) {
            String fileDirectory = settings.get(FOLDER_SETTING_NAME);
            if (validateDirectory(fileDirectory)) {
                this.fileFolderPath = fileDirectory;
            } else {
                throw new IllegalArgumentException("The following directory " + fileDirectory + " is invalid");
            }
        } else {
            log.error("Unable to found {} setting, provided {}", FOLDER_SETTING_NAME, settings.keySet());
            throw new IllegalArgumentException("Settings for Excel data source must contain " +
                    FOLDER_SETTING_NAME + " setting");
        }
    }

    private boolean validateDirectory(String directoryPath) {
        boolean isValid = true;
        File folder = new File(directoryPath);
        if (!folder.exists()) {
            isValid = false;
            log.error("Path {} do not exist", directoryPath);
        }
        if (!folder.isDirectory()) {
            isValid = false;
            log.error("Path {} is not directory", directoryPath);
        }
        return isValid;
    }

    private @NotNull List<String> scanDirectoryForFiles(String directory) {
        List<String> files = new ArrayList<>();
        File folder = new File(directory);
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".xlsx")) {
                files.add(file.getAbsolutePath());
                log.info("Found Excel file: {}", file.getAbsolutePath());
            }
        }
        return files;
    }

    private List<DailyCurrencyRate> extractDataFromFile(String filePath) throws IOException,
            RuntimeException {

        List<Map<String, String>> dataList = new ArrayList<>();

        List<DailyCurrencyRate> extractedData = new ArrayList<>();

        log.info("Starting converting file at: {}", filePath);
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            int numColumns = headerRow.getPhysicalNumberOfCells();

            dataList = processSheet(sheet, headerRow, numColumns);
            extractedData.addAll(convertData(dataList));
        }

        log.info("Ending converting file at: {}", filePath);
        return extractedData;
    }

    private List<Map<String, String>> processSheet(Sheet sheet, Row headerRow, int numColumns) {
        List<Map<String, String>> dataList = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Map<String, String> rowData = new HashMap<>();

            for (int j = 0; j < numColumns; j++) {
                Cell headerCell = headerRow.getCell(j);
                Cell cell = row.getCell(j);

                if (headerCell != null && cell != null) {
                    String header = headerCell.getStringCellValue();

                    String value;
                    switch (cell.getCellType()) {
                        case NUMERIC -> {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                value = cell.getDateCellValue().toInstant().toString();
                            } else {
                                value = String.valueOf(cell.getNumericCellValue());
                            }
                        }
                        case STRING -> {
                            value = cell.getStringCellValue();

                        }
                        default -> {
                            continue;
                        }
                    }
                    rowData.put(header, value);
                }
            }
            if (!rowData.isEmpty()) {
                dataList.add(rowData);
            }

        }
        return dataList;
    }

    private List<DailyCurrencyRate> convertData(List<Map<String, String>> rawData) throws RuntimeException {
        List<DailyCurrencyRate> convertedData = new ArrayList<>();

        int rowIndex = 0;
        for (Map<String, String> rowData : rawData) {
            Double nominal = 0.0D;
            BigDecimal rate = new BigDecimal("0");
            rowIndex++;
            LocalDateTime rateDate = LocalDateTime.now();
            DailyCurrencyRate dailyRate;
            try {
                if (checkForFieldExist(rowData, EXCEL_FIELD_NOMINAL, rowIndex)) {
                    nominal = Double.valueOf(rowData.get(EXCEL_FIELD_NOMINAL));
                    if (nominal <= 0) {
                        log.error("Failed to convert {} field", EXCEL_FIELD_NOMINAL);
                        throw new IllegalArgumentException("The nominal field cannot be 0 or below 0");
                    }
                }
                if (checkForFieldExist(rowData, EXCEL_FIELD_DATE, rowIndex)) {
                    rateDate = Instant.parse(rowData.get(EXCEL_FIELD_DATE)).atZone(ZoneId.systemDefault()).toLocalDateTime();
                }
                if (checkForFieldExist(rowData, EXCEL_FIELD_RATE, rowIndex)) {
                    rate = new BigDecimal((rowData.get(EXCEL_FIELD_RATE)));
                }
                if (checkForFieldExist(rowData, EXCEL_FIELD_CURRENCY, rowIndex)) {
                    if (!currencyTexts.containsKey(rowData.get(EXCEL_FIELD_CURRENCY).toLowerCase())) {
                        log.error("Unable to find {} field in table structure, row {}", EXCEL_FIELD_CURRENCY, rowIndex);
                        throw new IllegalArgumentException("The following currency " +
                                rowData.get(EXCEL_FIELD_CURRENCY) + " not found");
                    }
                }

                rate = rate.divide(new BigDecimal(nominal), RoundingMode.UP);

                dailyRate = new DailyCurrencyRate(currencyTexts.get(rowData.get(EXCEL_FIELD_CURRENCY).toLowerCase()),
                        rateDate, rate);
            } catch (RuntimeException exception) {
                log.error("Error during converting row {}, reason: {}", rowIndex, exception.getMessage());
                throw new RuntimeException(exception.getMessage());
            }

            log.debug("Data converted: {}", dailyRate);
            convertedData.add(dailyRate);
        }

        return convertedData;
    }

    private boolean checkForFieldExist(Map<String, String> rowData, String fieldName, int rowIndex) {
        if (rowData.containsKey(fieldName)) {
            return true;
        } else {
            log.error("Unable to find {} field in table structure, row {}", fieldName, rowIndex);
            throw new NoSuchElementException("The following file has invalid format");
        }
    }


}
