package shell.tgbot;
import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.shell.tgbot.TgBotCommand;
import ru.savior.rateprojection.shell.tgbot.TgBotCommandParser;
import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TgBotCommandParserTest {
    @Test
    public void given_ratePeriodCommand_parse_successfulParsing() {
        TgBotCommandParser parser = new TgBotCommandParser();
        TgBotCommand command = parser.parseCommandString("rate USD,TRY -period month -alg moon -output graph");
        assertEquals(TgBotCommandType.RATE_PERIOD, command.getCommandType());
        command = parser.parseCommandString("rate  USD  ,  TRY -period  month -alg   moon -output  graph");
        assertEquals(TgBotCommandType.RATE_PERIOD, command.getCommandType());
        command = parser.parseCommandString("rate  USD -period  month -alg   moon -output  graph");
        assertEquals(TgBotCommandType.RATE_PERIOD, command.getCommandType());
    }
    public void given_rateSingleCommand_parse_successfulParsing() {
        TgBotCommandParser parser = new TgBotCommandParser();
        TgBotCommand command = parser.parseCommandString("rate TRY -date   22.02.2030  -alg mist");
        assertEquals(TgBotCommandType.RATE_SINGLE_DATE, command.getCommandType());
        command = parser.parseCommandString("rate TRY  -date 22.02.2030 -alg mist");
        assertEquals(TgBotCommandType.RATE_SINGLE_DATE, command.getCommandType());
        command = parser.parseCommandString("rate TRY   -date 22.02.2030 -alg mist");
        assertEquals(TgBotCommandType.RATE_SINGLE_DATE, command.getCommandType());
    }

}
