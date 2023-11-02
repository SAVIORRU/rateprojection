package shell.tgbot;

import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.shell.tgbot.TgBotCommandParser;
import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;
import ru.savior.rateprojection.shell.tgbot.command.BotCommand;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactoryImpl;
import ru.savior.rateprojection.shell.tgbot.command.pattern.CommandPatternFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TgBotCommandParserTest {
    @Test
    public void given_ratePeriodCommand_parse_successfulParsing() {
        TgBotCommandParser parser = new TgBotCommandParser(new CommandFactoryImpl(), new CommandPatternFactoryImpl());
        BotCommand command = parser.parseCommandString("/rate USD,TRY -period month -alg mystical -output graph");
        assertEquals(TgBotCommandType.RATE_PERIOD, command.getCommandType());
        command = parser.parseCommandString("/rate  USD  ,  TRY -period  month -alg   average -output  graph");
        assertEquals(TgBotCommandType.RATE_PERIOD, command.getCommandType());
        command = parser.parseCommandString("/rate  USD -period  month -alg   extrapolate -output  graph");
        assertEquals(TgBotCommandType.RATE_PERIOD, command.getCommandType());
    }

    @Test
    public void given_rateSingleCommand_parse_successfulParsing() {
        TgBotCommandParser parser = new TgBotCommandParser(new CommandFactoryImpl(), new CommandPatternFactoryImpl());
        BotCommand command = parser.parseCommandString("/rate TRY -date   22.02.2030  -alg mystical");
        assertEquals(TgBotCommandType.RATE_SINGLE_DATE, command.getCommandType());
        command = parser.parseCommandString("/rate TRY  -date 22.02.2030 -alg average");
        assertEquals(TgBotCommandType.RATE_SINGLE_DATE, command.getCommandType());
        command = parser.parseCommandString("/rate TRY   -date 22.02.2030 -alg extrapolate");
        assertEquals(TgBotCommandType.RATE_SINGLE_DATE, command.getCommandType());
    }

}
