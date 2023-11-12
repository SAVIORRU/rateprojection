package ru.savior.rateprojection.shell.tgbot.command.pattern;

import lombok.extern.slf4j.Slf4j;
import ru.savior.rateprojection.shell.tgbot.CommandType;
import ru.savior.rateprojection.shell.tgbot.command.pattern.rate.RateCommandPatternFactory;
import ru.savior.rateprojection.shell.tgbot.command.pattern.service.ServiceCommandPatternFactory;

@Slf4j
public class CommandPatternFactoryImpl implements CommandPatternFactory {
    @Override
    public BotCommandPattern getCommandPattern(CommandType commandType) {
        CommandPatternFactory factory = getFactory(commandType);
        return factory.getCommandPattern(commandType);
    }

    private CommandPatternFactory getFactory(CommandType commandType) {
        switch (commandType) {
            case RATE_SINGLE_DATE, RATE_PERIOD -> {
                return new RateCommandPatternFactory();
            }
            case HELP, START -> {
                return new ServiceCommandPatternFactory();
            }
            default -> {
                log.error("Unsupported command type {}", commandType);
                throw new IllegalArgumentException("Unsupported command type");
            }
        }
    }
}

