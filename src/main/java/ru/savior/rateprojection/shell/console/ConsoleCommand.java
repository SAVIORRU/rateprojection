package ru.savior.rateprojection.shell.console;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class ConsoleCommand {

    public static final String COMMAND_WORD_RATE  = "rate";
    public static final String COMMAND_WORD_EXIT  = "exit";
    public static final String ARGUMENT_WORD_WEEK  = "week";
    public static final String ARGUMENT_WORD_TOMORROW  = "tomorrow";
    public static final String ARGUMENT_WORD_CURRENCY = "currency";
    public static final String ARGUMENT_WORD_TIME = "time";

    public static final String CONTEXT_DATA_PROJECTION = "projectionData";

    public static final String CONTEXT_DATA_SHELL = "runningShell";

    @Getter
    private final ConsoleCommandType commandType;

    @Getter
    private final List<String> commandWords;

    @Getter
    private final Map<String, String> commandArguments;

    private ConsoleCommand(ConsoleCommandType commandType, List<String> commandWords, Map<String, String> commandArguments) {
        this.commandType = commandType;
        this.commandWords = commandWords;
        this.commandArguments = commandArguments;
    }

    public static ConsoleCommand of( ConsoleCommandType commandType, Map<String, String> commandArguments )
            throws IllegalArgumentException {
        List<String> commandWords = new ArrayList<>();
        switch (commandType){
            case RATE -> {
                commandWords.add(COMMAND_WORD_RATE);
                commandWords.add(ARGUMENT_WORD_CURRENCY);
                commandWords.add(ARGUMENT_WORD_TIME);
            }
            case EXIT -> {
                commandWords.add(COMMAND_WORD_EXIT);
            }
            default -> {
                throw new IllegalArgumentException(); }

        }
        return new ConsoleCommand(commandType, commandWords, commandArguments);
    }
}
