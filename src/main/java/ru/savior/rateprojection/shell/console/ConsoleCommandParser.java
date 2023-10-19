package ru.savior.rateprojection.shell.console;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ConsoleCommandParser {

    private static final int COMMAND_WORD_INDEX = 0;

    public ConsoleCommand parseCommandLine(String commandLine) throws IllegalArgumentException {
        ConsoleCommand parsedConsoleCommand = null;
        List<String> commandWords = Arrays.asList(commandLine.split("\\s* \\s*"));
        for (ConsoleCommandType commandType : ConsoleCommandType.values()) {

                parsedConsoleCommand = ConsoleCommand.of(commandType, new HashMap<>());

            if (parsedConsoleCommand.getCommandWords().size() != commandWords.size()) {
                parsedConsoleCommand = null;
                continue;
            }
            if (parsedConsoleCommand.getCommandWords().get(COMMAND_WORD_INDEX).
                    equals(commandWords.get(COMMAND_WORD_INDEX))) {
                if (commandWords.size() > 1) {
                    for (int i = COMMAND_WORD_INDEX + 1; i < commandWords.size(); i++) {
                        parsedConsoleCommand.getCommandArguments().put(parsedConsoleCommand.getCommandWords().get(i),
                                commandWords.get(i));
                    }
                }
                break;

            }
            parsedConsoleCommand = null;

        }
        if (parsedConsoleCommand == null) {
            throw new IllegalArgumentException();
        }
        return parsedConsoleCommand;

    }
}
