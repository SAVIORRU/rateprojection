package shell.tgbot.command;

import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.shell.tgbot.CommandType;
import ru.savior.rateprojection.shell.tgbot.command.Command;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactory;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactoryImpl;
import org.junit.jupiter.api.Assertions;
import ru.savior.rateprojection.shell.tgbot.command.rate.RateSingleDateCommand;

public class CommandFactoryTest {
    @Test
    public void given_rateSingleDateInput_getCommandFromString_rateSingleDateCommand() {
        CommandFactory factory = new CommandFactoryImpl();
        Command command = factory.getCommandFromString(CommandType.RATE_SINGLE_DATE,
                "rate TRY -date tomorrow -alg average");
        Assertions.assertTrue(command instanceof RateSingleDateCommand);
    }

    @Test
    public void given_ratePeriodInput_getCommandFromString_ratePeriodCommand() {
        CommandFactory factory = new CommandFactoryImpl();
        Command command = factory.getCommandFromString(CommandType.RATE_SINGLE_DATE,
                "rate USD -period week -alg mystical -output list");
        Assertions.assertTrue(command instanceof RateSingleDateCommand);
    }
}
