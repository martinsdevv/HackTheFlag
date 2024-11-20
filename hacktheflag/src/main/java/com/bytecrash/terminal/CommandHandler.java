package com.bytecrash.terminal;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.commands.CatCommand;
import com.bytecrash.terminal.commands.CdCommand;
import com.bytecrash.terminal.commands.HideFlagCommand;
import com.bytecrash.terminal.commands.LsCommand;
import com.bytecrash.terminal.commands.MkdirCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private FileSystem fileSystem;
    private boolean isFirstRound = true;
    private Map<String, Command> commands = new HashMap<>();  // Mapa para armazenar os comandos registrados

    public CommandHandler(FileSystem fileSystem) {
        this.fileSystem = fileSystem;

        // Registro dos comandos
        registerCommand(new LsCommand(fileSystem));
        registerCommand(new CdCommand(fileSystem));
        registerCommand(new CatCommand(fileSystem));
        registerCommand(new MkdirCommand(fileSystem));
        registerCommand(new HideFlagCommand(fileSystem));
    }

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);  // Adiciona o comando ao mapa
    }

    public String executeCommand(String commandLine) {
        String[] parts = commandLine.split(" ", 2);
        String commandName = parts[0];
        String argument = parts.length > 1 ? parts[1] : null;

        if (commandName.equals("hideflag")) {
            return handleHideFlag(argument);
        }

        Command command = commands.get(commandName);
        if (command != null) {
            return command.execute(argument);
        } else {
            return "Comando não reconhecido: " + commandName;
        }
    }

    private String handleHideFlag(String argument) {
        if (!isFirstRound) {
            return "O comando 'hideflag' só pode ser usado no primeiro round.";
        }

        if (argument == null || argument.isBlank()) {
            return "Por favor, especifique um diretório para esconder a bandeira.";
        }

        Directory dir = fileSystem.findDirectory(argument);
        if (dir != null) {
            // Criar o arquivo 'flag.txt' no diretório especificado
            fileSystem.createFileInSystem(dir, "flag.txt", "Esta é a bandeira!");

            isFirstRound = false; // Termina o primeiro round
            return "Flag escondida com sucesso no diretório: " + argument + 
                   "\nO segundo round começou! Boa sorte!";
        } else {
            return "Diretório não encontrado: " + argument;
        }
    }

    public String getCurrentDirectoryPath() {
        Directory current = fileSystem.getCurrentDirectory();
        StringBuilder path = new StringBuilder(current.getName());

        // Constrói o caminho completo do diretório atual
        while (current.getParent() != null && !current.getParent().getName().equals("root")) {
            current = current.getParent();
            path.insert(0, current.getName() + "/");
        }

        return "/" + path.toString();
    }

    public boolean isFirstRound() {
        return isFirstRound;
    }
}
