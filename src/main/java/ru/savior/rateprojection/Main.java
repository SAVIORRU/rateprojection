package ru.savior.rateprojection;

import ru.savior.rateprojection.shell.Shell;
import ru.savior.rateprojection.shell.console.ConsoleShell;
import ru.savior.rateprojection.shell.tgbot.TgBotShell;

public class Main {
    public static void main(String[] args) {
        Shell shell = new TgBotShell();
        shell.runShell();
    }
}
