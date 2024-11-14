package com.bytecrash.terminal;

import com.bytecrash.filesystem.FileSystem;
import java.util.Scanner;

public class TerminalSimulator {
    private CommandHandler commandHandler;
    private Scanner scanner;

    public TerminalSimulator() {
        FileSystem fileSystem = new FileSystem();
        this.commandHandler = new CommandHandler(fileSystem);

        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Bem-vindo ao Hack the Flag!");
        while (true) {
            System.out.print("user@hacktheflag:" + commandHandler.getCurrentDirectoryPath() + "$ ");
            String command = scanner.nextLine();
            commandHandler.executeCommand(command);
        }
    }
}
