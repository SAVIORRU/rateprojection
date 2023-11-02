package ru.savior.rateprojection.shell.tgbot;

import lombok.RequiredArgsConstructor;
import ru.savior.rateprojection.core.service.ProjectionService;
import ru.savior.rateprojection.shell.tgbot.command.BotCommand;
import ru.savior.rateprojection.shell.tgbot.command.rate.RateCommand;
import ru.savior.rateprojection.shell.tgbot.command.rate.RateCommandFactory;
import ru.savior.rateprojection.shell.tgbot.command.service.ServiceCommandFactory;

import java.util.*;

@RequiredArgsConstructor
public class TgBotCommandProcessor {

    private final ProjectionService projectionService;


    public List<String> processCommand(BotCommand command, Map<String, Object> contextData) {
         return new ArrayList<>(command
                .execute(addDataToContext(command.getCommandType(), contextData)));
    }

    private Map<String, Object> addDataToContext(TgBotCommandType commandType, Map<String, Object> contextData) {
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
