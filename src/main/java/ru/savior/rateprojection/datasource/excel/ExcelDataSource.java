package ru.savior.rateprojection.datasource.excel;


import lombok.NonNull;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class ExcelDataSource implements DataSource {

    private static final String FOLDER_SETTING_NAME = "excelFilesFolder";
    private static final Map<String, Currency> currencyTexts = new HashMap<>() {{
        put("турецкая лира", Currency.TRY);
        put("доллар сша", Currency.USD);
        put("евро", Currency.EUR);
    }};
    private String fileFolderPath;

    public ExcelDataSource() {

    }

    @Override
    public ProjectionDataResponse provideData() {
        ProjectionDataResponse dataSourceResponse = new ProjectionDataResponse(new ArrayList<>(), new ArrayList<>(), false);
        if (!validateDirectory(fileFolderPath)) {
            dataSourceResponse.getLog().add("The data source is not configured or its folder is invalid");
            return dataSourceResponse;
        }
        List<String> filesToRead = scanDirectoryForFiles(fileFolderPath);
        if (filesToRead.isEmpty()) {
            dataSourceResponse.getLog().add("No such files for data extraction");
            return dataSourceResponse;
        }
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
            dataSourceResponse.getLog().add("Successfully loaded " + validFilesCount + " Excel files with " +
                    dataSourceResponse.getProvidedData().size() + " rows total");
        } else {
            dataSourceResponse.setSuccessful(false);
            dataSourceResponse.getLog().add("No valid data has been found in " + filesToRead.size() + " files");
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
            throw new IllegalArgumentException("Settings for Excel data source must contain " +
                    FOLDER_SETTING_NAME + " setting");
        }
    }

    private boolean validateDirectory(String directoryPath) {

        File folder = new File(directoryPath);
        return folder.exists() && folder.isDirectory();
    }

    private @NotNull List<String> scanDirectoryForFiles(String directory) {
        List<String> files = new ArrayList<>();
        File folder = new File(directory);
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".xlsx")) {
                files.add(file.getAbsolutePath());
            }
        }
        return files;
    }

    private List<DailyCurrencyRate> extractDataFromFile(String filePath) throws IOException,
            RuntimeException {

        List<Map<String, String>> dataList = new ArrayList<>();

        List<DailyCurrencyRate> extractedData = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            int numColumns = headerRow.getPhysicalNumberOfCells();

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
            extractedData.addAll(convertData(dataList));
        }


        return extractedData;
    }

    private List<DailyCurrencyRate> convertData(List<Map<String, String>> rawData) throws RuntimeException {
        List<DailyCurrencyRate> convertedData = new ArrayList<>();


        for (Map<String, String> rowData : rawData) {
            Double nominal = 0.0D;
            Double rate = 0.0D;
            LocalDateTime rateDate;
            if (rowData.containsKey("nominal")) {
                nominal = Double.valueOf(rowData.get("nominal"));
                if (nominal == 0 || nominal < 0) {
                    throw new IllegalArgumentException("The nominal field cannot be 0 or below 0");
                }
            } else {
                throw new NoSuchElementException("The following file has invalid format");
            }
            if (rowData.containsKey("data")) {
                rateDate = Instant.parse(rowData.get("data")).atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else {
                throw new NoSuchElementException("The following file has invalid format");
            }
            if (rowData.containsKey("curs")) {
                rate = Double.valueOf(rowData.get("curs"));
            } else {
                throw new NoSuchElementException("The following file has invalid format");
            }
            if (rowData.containsKey("cdx")) {
                if (!currencyTexts.containsKey(rowData.get("cdx").toLowerCase())) {
                    throw new IllegalArgumentException("The following currency " + rowData.get("cdx") + " not found");
                }
            } else {
                throw new NoSuchElementException("The following file has invalid format");
            }

            if (!(nominal == 1.0D)) {
                rate /= nominal;
            }

            DailyCurrencyRate dailyRate = new DailyCurrencyRate(currencyTexts.get(rowData.get("cdx").toLowerCase()),
                    rateDate, rate);

            convertedData.add(dailyRate);
        }

        return convertedData;
    }


}
