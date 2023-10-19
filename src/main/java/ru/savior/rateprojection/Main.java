package ru.savior.rateprojection;

import ru.savior.rateprojection.shell.Shell;
import ru.savior.rateprojection.shell.console.ConsoleShell;

public class Main {
    public static void main(String[] args) {
        Shell consoleShell = new ConsoleShell();
        consoleShell.runShell();
    }
}
