package ru.savior.rateprojection.shell.tgbot.command.pattern;

import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;
import ru.savior.rateprojection.shell.tgbot.command.pattern.rate.RateCommandPatternFactory;
import ru.savior.rateprojection.shell.tgbot.command.pattern.service.ServiceCommandPatternFactory;

public class CommandPatternFactoryImpl implements CommandPatternFactory {
    @Override
    public BotCommandPattern getCommandPattern(TgBotCommandType commandType) {
        CommandPatternFactory factory = getFactory(commandType);
        return factory.getCommandPattern(commandType);
    }

    private CommandPatternFactory getFactory(TgBotCommandType commandType) {
        switch (commandType) {
            case RATE_SINGLE_DATE, RATE_PERIOD -> {
                return new RateCommandPatternFactory();
            }
            case HELP, START -> {
                return new ServiceCommandPatternFactory();
            }
            default -> {
                throw new IllegalArgumentException("Unsupported command type");
            }
        }
    }
}

