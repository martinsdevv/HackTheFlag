package com.bytecrash.terminal;

import com.bytecrash.filesystem.FileSystem;
import java.util.Scanner;

public class TerminalSimulator {
    private FileSystem fileSystem;
    private Scanner scanner;

    public TerminalSimulator() {
        fileSystem = new FileSystem();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Bem-vindo ao Hack the Flag!");
        while (true) {
            System.out.print("user@hacktheflag:~$ ");
            String command = scanner.nextLine();
            processCommand(command);
        }
    }

    private void processCommand(String command) {
        if (command.startsWith("ls")) {
            fileSystem.listFiles();
        } else if (command.startsWith("cd ")) {
            String dirName = command.substring(3).trim();
            fileSystem.changeDirectory(dirName);
        } else if (command.equals("exit")) {
            System.out.println("Encerrando o terminal...");
            System.exit(0);
        } else {
            System.out.println("Comando não reconhecido: " + command);
        }
    }
}
