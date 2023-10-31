package ru.savior.rateprojection.shell.tgbot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TgProjectionBot extends TelegramLongPollingBot {

    private final Map<String, Object> context;
    private final TgBotCommandParser parser;
    private final TgBotCommandProcessor processor;

    private static final String TG_BOT_NAME = "projection721_bot";


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            List<String> output = processInput(update.getMessage().getText());
            sendOutput(update.getMessage().getChatId().toString(), output);
        }
    }

    @Override
    public String getBotUsername() {
        return TG_BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return "6726549785:AAH8LCuJEQQB9hhOBCLkNwDuj3gAAnEC1Zs";
    }

    private List<String> processInput(String input) {
        List<String> output = new ArrayList<>();
        try {
            TgBotCommand command = parser.parseCommandString(input);
            output = processor.processCommand(command, context);
        } catch (IllegalArgumentException exception) {
            output.add(exception.getMessage());
        }
        return output;
    }

    private void sendOutput(String chatId, List<String> output) {
        String messageText = "";
        for (String outputLine : output) {
            if (new File(outputLine).isFile()) {
                if (!messageText.isEmpty()) {
                    sendText(chatId, messageText);
                }
                messageText = "";
                sendFile(chatId, outputLine);
            } else {
                messageText += '\n';
                messageText += outputLine;
            }
        }
        if (!messageText.isEmpty()) {
            sendText(chatId, messageText);
        }
    }

    private void sendFile(String chatId, String filePath) {
        SendDocument sendDocument = new SendDocument();
        File file = new File(filePath);
        if (!(file.canRead() && file.exists())) {
            sendText(chatId, "An error occurred during file sending");
            log.error("Error during file process {}", filePath);
            return;
        }
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(file));
        try {
            execute(sendDocument);
        } catch (TelegramApiException exception) {
            log.error(exception.getMessage());
        }

    }

    private void sendText(String chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException exception) {
            log.error(exception.getMessage());
        }
    }
}
