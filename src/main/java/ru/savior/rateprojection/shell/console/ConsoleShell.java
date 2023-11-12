package ru.savior.rateprojection.shell.console;

import lombok.extern.slf4j.Slf4j;
import ru.savior.rateprojection.core.entity.ProjectionDataResponse;
import ru.savior.rateprojection.datasource.DataSource;
import ru.savior.rateprojection.datasource.excel.ExcelCurrencyTexts;
import ru.savior.rateprojection.datasource.excel.ExcelDataSource;
import ru.savior.rateprojection.shell.Shell;

import java.io.File;
import java.util.*;

@Slf4j
public class ConsoleShell implements Shell {

    private final Map<String, Object> context = new HashMap<>();
    private final ConsoleCommandParser commandParser = new ConsoleCommandParser();
    private final ConsoleCommandProcessor commandProcessor = new ConsoleCommandProcessor();

    private boolean isRunning;

    public ConsoleShell() {
    }

    @Override
    public void runShell() {
        this.isRunning = true;
        printOutput(loadContext());
        Scanner inputScanner = new Scanner(System.in);
        while (this.isRunning) {
            System.out.println("Input your command");
            String input = inputScanner.nextLine();
            printOutput(processInput(input));
        }
        context.clear();
    }

    public void terminate() {

        this.isRunning = false;
    }

    private List<String> loadContext() {
        context.put(ConsoleCommand.CONTEXT_DATA_SHELL, this);
        List<String> loadingLog = new ArrayList<>();
        DataSource excelDataSource = new ExcelDataSource(new ExcelCurrencyTexts());
        try {
            Map<String, String> settings = new HashMap<>();
            String jarFilePath = ConsoleShell.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            File jarFile = new File(jarFilePath);
            settings.put("excelFilesFolder", jarFile.getParentFile().getAbsolutePath());
            excelDataSource.configure(settings);
        } catch (IllegalArgumentException exception) {
            loadingLog.add(exception.getMessage());
            terminate();
            return loadingLog;
        }
        ProjectionDataResponse dataResponse = excelDataSource.provideData();
        if (dataResponse.isSuccessful()) {
            context.put(ConsoleCommand.CONTEXT_DATA_PROJECTION, dataResponse.getProvidedData());

        } else {
            terminate();
        }
        loadingLog.addAll(dataResponse.getLog());
        return loadingLog;
    }

    private void printOutput(List<String> output) {
        for (String outputString : output) {
            System.out.println(outputString);
        }
    }

    private List<String> processInput(String input) {
        List<String> processLog = new ArrayList<>();
        ConsoleCommand consoleCommand = null;
        try {
            consoleCommand = commandParser.parseCommandLine(input);
            processLog.addAll(commandProcessor.processCommand(consoleCommand, context));
        } catch (IllegalArgumentException exception) {
            processLog.add("The following input cannot be recognized");
        }

        return processLog;
    }
}
