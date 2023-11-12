package shell.tgbot;

import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.shell.tgbot.CommandParser;
import ru.savior.rateprojection.shell.tgbot.CommandType;
import ru.savior.rateprojection.shell.tgbot.command.Command;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactoryImpl;
import ru.savior.rateprojection.shell.tgbot.command.pattern.CommandPatternFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TgCommandParserTest {
    @Test
    public void given_ratePeriodCommand_parse_successfulParsing() {
        CommandParser parser = new CommandParser(new CommandFactoryImpl(), new CommandPatternFactoryImpl());
        Command command = parser.parseCommandString("/rate USD,TRY -period month -alg mystical -output graph");
        assertEquals(CommandType.RATE_PERIOD, command.getCommandType());
        command = parser.parseCommandString("/rate  USD  ,  TRY -period  month -alg   average -output  graph");
        assertEquals(CommandType.RATE_PERIOD, command.getCommandType());
        command = parser.parseCommandString("/rate  USD -period  month -alg   extrapolate -output  graph");
        assertEquals(CommandType.RATE_PERIOD, command.getCommandType());
    }

    @Test
    public void given_rateSingleCommand_parse_successfulParsing() {
        CommandParser parser = new CommandParser(new CommandFactoryImpl(), new CommandPatternFactoryImpl());
        Command command = parser.parseCommandString("/rate TRY -date   22.02.2030  -alg mystical");
        assertEquals(CommandType.RATE_SINGLE_DATE, command.getCommandType());
        command = parser.parseCommandString("/rate TRY  -date 22.02.2030 -alg average");
        assertEquals(CommandType.RATE_SINGLE_DATE, command.getCommandType());
        command = parser.parseCommandString("/rate TRY   -date 22.02.2030 -alg extrapolate");
        assertEquals(CommandType.RATE_SINGLE_DATE, command.getCommandType());
    }

}
