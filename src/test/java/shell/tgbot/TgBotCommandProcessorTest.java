package shell.tgbot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.savior.rateprojection.core.entity.Currency;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.core.service.ProjectionServiceImpl;
import ru.savior.rateprojection.shell.tgbot.TgBotCommandProcessor;
import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;
import ru.savior.rateprojection.shell.tgbot.command.BotCommand;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactoryImpl;
import ru.savior.rateprojection.shell.tgbot.command.rate.RateCommand;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class TgBotCommandProcessorTest {
    @Test
    public void given_ratePeriodCommand_listOutput() {
        Map<String, Object> context = new HashMap<>();
        context.put(RateCommand.CONTEXT_DATA_PROJECTION, createSampleProjectionData(false));
        BotCommand command = new CommandFactoryImpl().getCommandFromString(TgBotCommandType.RATE_SINGLE_DATE,
                "rate TRY -date tomorrow -alg average");
        List<String> output = new TgBotCommandProcessor(new ProjectionServiceImpl()).processCommand(command, context);
        Assertions.assertTrue(output.size() > 0);
    }
    @Test
    public void given_ratePeriodCommand_graphOutput() {
        Map<String, Object> context = new HashMap<>();
        context.put(RateCommand.CONTEXT_DATA_PROJECTION, createSampleProjectionData(true));
        BotCommand command = new CommandFactoryImpl().getCommandFromString(TgBotCommandType.RATE_SINGLE_DATE,
                "rate USD, EUR -period week -alg mystical -output list");
        List<String> output = new TgBotCommandProcessor(new ProjectionServiceImpl()).processCommand(command, context);
        Assertions.assertTrue(output.size() > 0);
    }


    private List<DailyCurrencyRate> createSampleProjectionData(boolean isRandom) {
        List<DailyCurrencyRate> projectionData = new ArrayList<>();
        LocalDateTime currentDate = LocalDateTime.now().minusDays(1);
        BigDecimal rate = BigDecimal.valueOf(1.0);
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            if (isRandom) {
                rate = BigDecimal.valueOf(random.nextDouble(30.0D, 80.D));
            }
            projectionData.add(new DailyCurrencyRate(Currency.USD, currentDate, rate));
            if (isRandom) {
                rate = BigDecimal.valueOf(random.nextDouble(30.0D, 80.D));
            }
            projectionData.add(new DailyCurrencyRate(Currency.EUR, currentDate, rate));
            currentDate = currentDate.minusDays(1);
        }

        return projectionData;
    }
}
