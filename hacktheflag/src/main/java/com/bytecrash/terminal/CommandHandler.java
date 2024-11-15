package com.bytecrash.terminal;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.commands.CatCommand;
import com.bytecrash.terminal.commands.CdCommand;
import com.bytecrash.terminal.commands.LsCommand;
import com.bytecrash.terminal.commands.MkdirCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private FileSystem fileSystem;
    private Map<String, Command> commands = new HashMap<>();  // Mapa para armazenar os comandos registrados

    public CommandHandler(FileSystem fileSystem) {
        this.fileSystem = fileSystem;

        // Registro dos comandos
        registerCommand(new LsCommand(fileSystem));
        registerCommand(new CdCommand(fileSystem));
        registerCommand(new CatCommand(fileSystem));
        registerCommand(new MkdirCommand(fileSystem));
    }

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);  // Adiciona o comando ao mapa
    }

    public String executeCommand(String commandLine) {
        String[] parts = commandLine.split(" ", 2);
        String commandName = parts[0];
        String argument = parts.length > 1 ? parts[1] : null;
    
        Command command = commands.get(commandName);  // Busca o comando no mapa
        if (command != null) {
            return command.execute(argument);  // Assumindo que `execute` em cada comando retorna uma `String`
        } else {
            return "Comando não reconhecido: " + commandName;
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
}
