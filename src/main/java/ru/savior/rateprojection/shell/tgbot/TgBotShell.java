package ru.savior.rateprojection.shell.tgbot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.savior.rateprojection.core.entity.ProjectionDataResponse;
import ru.savior.rateprojection.core.service.ProjectionServiceImpl;
import ru.savior.rateprojection.datasource.DataSource;
import ru.savior.rateprojection.datasource.excel.ExcelCurrencyTexts;
import ru.savior.rateprojection.datasource.excel.ExcelDataSource;
import ru.savior.rateprojection.shell.Shell;
import ru.savior.rateprojection.shell.console.ConsoleShell;
import ru.savior.rateprojection.shell.tgbot.command.CommandFactoryImpl;
import ru.savior.rateprojection.shell.tgbot.command.pattern.CommandPatternFactoryImpl;
import ru.savior.rateprojection.shell.tgbot.command.rate.RateCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TgBotShell implements Shell {
    @Override
    public void runShell() {
        try {
            Map<String, Object> context = getContext();
            ProjectionBot bot = new ProjectionBot(context, new CommandParser(new CommandFactoryImpl(),
                    new CommandPatternFactoryImpl()),
                    new CommandProcessor(new ProjectionServiceImpl()));
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(bot);
        } catch (TelegramApiException | RuntimeException exception) {
            log.error(exception.getMessage());
        }
    }

    private Map<String, Object> getContext() throws IllegalArgumentException{
        Map<String, Object> context = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        String jarFilePath = ConsoleShell.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(jarFilePath);
        settings.put("excelFilesFolder", jarFile.getParentFile().getAbsolutePath());
        DataSource excelDataSource = new ExcelDataSource(new ExcelCurrencyTexts());
        excelDataSource.configure(settings);
        ProjectionDataResponse dataResponse = excelDataSource.provideData();
        if (dataResponse.isSuccessful()) {
            context.put(RateCommand.CONTEXT_DATA_PROJECTION, dataResponse.getProvidedData());
        } else {
            throw new RuntimeException();
        }
        return context;
    }
}
