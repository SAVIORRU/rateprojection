package ru.savior.rateprojection.shell.tgbot.command;

import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;
import ru.savior.rateprojection.shell.tgbot.command.rate.RateCommandFactory;
import ru.savior.rateprojection.shell.tgbot.command.service.ServiceCommandFactory;

public class CommandFactoryImpl implements CommandFactory {
    protected static final String COMMAND_PARAM_PREFIX = BotCommandConstants.COMMAND_PARAM_PREFIX;
    @Override
    public BotCommand getCommandFromString(TgBotCommandType commandType, String input) throws IllegalArgumentException {
        CommandFactory factory = getFactory(commandType);
        return factory.getCommandFromString(commandType, input);
    }

    private CommandFactory getFactory(TgBotCommandType commandType) {
        switch (commandType) {
            case RATE_SINGLE_DATE, RATE_PERIOD -> {
                return new RateCommandFactory();
            }
            case HELP, START ->{
                return new ServiceCommandFactory();
            }
            default -> {throw new IllegalArgumentException("Unsupported command type");}
        }
    }

}
