package ru.savior.rateprojection.shell.tgbot;

import lombok.RequiredArgsConstructor;
import ru.savior.rateprojection.core.service.ProjectionService;
import ru.savior.rateprojection.shell.tgbot.command.Command;
import ru.savior.rateprojection.shell.tgbot.command.rate.RateCommand;

import java.util.*;

@RequiredArgsConstructor
public class CommandProcessor {

    private final ProjectionService projectionService;


    public List<String> processCommand(Command command, Map<String, Object> contextData) {
         return new ArrayList<>(command
                .execute(addDataToContext(command.getCommandType(), contextData)));
    }

    private Map<String, Object> addDataToContext(CommandType commandType, Map<String, Object> contextData) {
        Map<String, Object> newContext = new HashMap<>(contextData);
        switch (commandType) {
            case RATE_SINGLE_DATE, RATE_PERIOD -> {
                newContext.put(RateCommand.CONTEXT_PROJECTION_SERVICE, projectionService);
            }
            default -> {throw new IllegalArgumentException("Unsupported command type");}
        }
        return newContext;
    }

}