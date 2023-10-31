package ru.savior.rateprojection.shell.tgbot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class TgBotCommand {

    private final TgBotCommandType commandType;
    private final List<String> currencies;
    private final Map<String, String> parameters;

}
