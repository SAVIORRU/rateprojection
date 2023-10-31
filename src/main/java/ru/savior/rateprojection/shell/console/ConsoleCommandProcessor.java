package ru.savior.rateprojection.shell.console;

import lombok.RequiredArgsConstructor;
import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionDataResponse;
import ru.savior.rateprojection.core.service.ProjectionServiceImpl;
import ru.savior.rateprojection.core.service.algorithm.ProjectionAlgorithmType;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@RequiredArgsConstructor
public class ConsoleCommandProcessor {

    public List<String> processCommand(ConsoleCommand command, Map<String, Object> contextData) {
        List<String> commandOutput = new ArrayList<>();
        switch (command.getCommandType()) {
            case RATE -> {
                commandOutput.addAll(loadAndExecuteRateCommand(command, contextData));
            }

            case EXIT -> {
                commandOutput.addAll(loadAndExecuteExitCommand(contextData));

            }
            default -> {
                commandOutput.add("The executing command is not supported");
            }
        }
        return commandOutput;
    }


    private List<String> loadAndExecuteExitCommand(Map<String, Object> contextData) {
        ConsoleShell shell = null;
        List<String> commandOutput = new ArrayList<>();
        if (contextData.containsKey(ConsoleCommand.CONTEXT_DATA_SHELL)) {
            try {
                shell = (ConsoleShell) contextData.get(ConsoleCommand.CONTEXT_DATA_SHELL);
                if (shell == null) {
                    commandOutput.add("The context data for " + ConsoleCommandType.EXIT + " is invalid");
                    return commandOutput;
                }
            } catch (RuntimeException runtimeException) {
                commandOutput.add("The context data for " + ConsoleCommandType.EXIT + " is invalid");
                return commandOutput;
            }
            shell.terminate();
        } else {
            commandOutput.add("The context data for " + ConsoleCommandType.EXIT + " is invalid");
            return commandOutput;
        }
        return commandOutput;
    }


    private List<String> loadAndExecuteRateCommand(ConsoleCommand command, Map<String, Object> contextData) {
        List<DailyCurrencyRate> projectionData = new ArrayList<>();
        List<String> commandOutput = new ArrayList<>();
        if (contextData.containsKey(ConsoleCommand.CONTEXT_DATA_PROJECTION)) {
            try {
                projectionData = (List<DailyCurrencyRate>)
                        contextData.get(ConsoleCommand.CONTEXT_DATA_PROJECTION);
                if (projectionData == null) {
                    commandOutput.add("The context data for " + ConsoleCommandType.RATE + " is invalid");
                    return commandOutput;
                }
            } catch
            (RuntimeException runtimeException) {
                commandOutput.add("The context data for " + ConsoleCommandType.RATE + " is invalid");
                return commandOutput;
            }
            String currencyTypeRaw = command.getCommandArguments().get(ConsoleCommand.ARGUMENT_WORD_CURRENCY);
            String projectionTimeRaw = command.getCommandArguments().get(ConsoleCommand.ARGUMENT_WORD_TIME);
            commandOutput.addAll(processRateCommand(currencyTypeRaw, projectionTimeRaw, projectionData));
        } else {
            commandOutput.add("The context data for " + ConsoleCommandType.RATE + " is invalid");
            return commandOutput;
        }
        return commandOutput;
    }


    private List<String> processRateCommand(String currencyTypeRaw, String projectionTimeRaw,
                                            List<DailyCurrencyRate> projectionData) {
        Currency currencyType;
        List<String> commandOutput = new ArrayList<>();

        if (Arrays.stream(Currency.values())
                .anyMatch(x -> x.toString().equals(currencyTypeRaw.toUpperCase()))) {
            currencyType = Currency.valueOf(currencyTypeRaw.toUpperCase());
        } else {
            commandOutput.add("The following currency " + currencyTypeRaw + " not found");
            return commandOutput;
        }

        switch (projectionTimeRaw.toLowerCase()) {
            case ConsoleCommand.ARGUMENT_WORD_TOMORROW -> {
                commandOutput.addAll(processRateForDayCommand(projectionData,
                        currencyType, ProjectionAlgorithmType.AVERAGE));
            }
            case ConsoleCommand.ARGUMENT_WORD_WEEK -> {
                commandOutput.addAll(processRateForWeekCommand(projectionData,
                        currencyType, ProjectionAlgorithmType.AVERAGE));
            }
            default -> {
                commandOutput.add("The following projection time " + projectionTimeRaw + " is invalid");
            }
        }

        return commandOutput;
    }


    private List<String> processRateForDayCommand(List<DailyCurrencyRate> projectionData, Currency currencyType,
                                                  ProjectionAlgorithmType algorithmType) {
        ProjectionServiceImpl projectionService = new ProjectionServiceImpl();
        ProjectionDataResponse dataResponse = projectionService.projectForNextDay(projectionData, currencyType,
                algorithmType);

        return dataResponse.format();
    }

    private List<String> processRateForWeekCommand(List<DailyCurrencyRate> projectionData, Currency currencyType,
                                                   ProjectionAlgorithmType algorithmType) {
        ProjectionServiceImpl projectionService = new ProjectionServiceImpl();
        ProjectionDataResponse dataResponse = projectionService.projectForNextWeek(projectionData, currencyType,
                algorithmType);

        return dataResponse.format();
    }


}
