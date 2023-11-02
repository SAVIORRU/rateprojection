package ru.savior.rateprojection.shell.tgbot.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.savior.rateprojection.core.entity.DailyCurrencyRate;
import ru.savior.rateprojection.shell.tgbot.TgBotCommandType;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class BotCommandImpl implements BotCommand {
    private final TgBotCommandType commandType;

    @Override
    public abstract List<String> execute(Map<String, Object> context);

    @Override
    public TgBotCommandType getCommandType() {
        return this.commandType;
    }

    protected Object getDataFromContext(Map<String, Object> contextData, String paramName) {
        Object data = null;
        try {
            if (contextData.containsKey(paramName)) {
                data = contextData.get(paramName);
            }
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("The context data for command is invalid");
        }
        if (data == null) {
            throw new IllegalArgumentException("The context data for command is invalid");
        }
        return data;
    }
}
