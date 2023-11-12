package ru.savior.rateprojection.shell.tgbot.command.service;

import ru.savior.rateprojection.shell.tgbot.CommandType;
import ru.savior.rateprojection.shell.tgbot.command.CommandImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelpCommand extends CommandImpl {

    public static final String COMMAND_WORD = "help";
    public HelpCommand() {
        super(CommandType.HELP);
    }

    @Override
    public List<String> execute(Map<String, Object> context) {
        List<String> commandOutput = new ArrayList<>();
        commandOutput.add("Following rate command patterns available:");
        commandOutput.add("rate currency -date time -alg type");
        commandOutput.add("rate currency (up to 5 with , delimiter) -period time -alg type -output type");
        commandOutput.add("Following algorithms available:");
        commandOutput.add("average, mystical, past_year, extrapolate");
        commandOutput.add("Following -date arguments available:");
        commandOutput.add("tomorrow - for projection for tomorrow, or specific date in DD.MM.YYYY format");
        commandOutput.add("Following -period arguments available:");
        commandOutput.add("week, month");
        commandOutput.add("Following -output arguments available:");
        commandOutput.add("list - for text output, graph - for image graph output");
        return commandOutput;
    }
}
