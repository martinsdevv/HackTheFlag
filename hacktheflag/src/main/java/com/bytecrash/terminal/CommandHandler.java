package com.bytecrash.terminal;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.commands.CatCommand;
import com.bytecrash.terminal.commands.CdCommand;
import com.bytecrash.terminal.commands.HelpCommand;
import com.bytecrash.terminal.commands.HideFlagCommand;
import com.bytecrash.terminal.commands.JinxCommand;
import com.bytecrash.terminal.commands.LsCommand;
import com.bytecrash.terminal.commands.MkdirCommand;
// import com.bytecrash.terminal.commands.SSHCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final CTFManager ctfManager; 
    private final Map<String, Command> commands = new HashMap<>(); 

    public CommandHandler(CTFManager ctfManager) {
        this.ctfManager = ctfManager;
        registerDefaultCommands();
    }

    private void registerDefaultCommands() {
        registerCommand(new LsCommand(ctfManager.getCurrentFileSystem()));
        registerCommand(new CdCommand(ctfManager.getCurrentFileSystem()));
        registerCommand(new CatCommand(ctfManager.getCurrentFileSystem(), ctfManager));
        registerCommand(new MkdirCommand(ctfManager.getCurrentFileSystem(), ctfManager));
        registerCommand(new HideFlagCommand(ctfManager));
        registerCommand(new HelpCommand(this));
        registerCommand(new JinxCommand());
        //registerCommand(new SSHCommand(ctfManager));
    }

    public Map<String, Command> getCommands() {
        Map<String, Command> visibleCommands = new HashMap<>(commands);
        visibleCommands.remove("jinx");
        return visibleCommands;
    }

    public void registerCommand(Command command) {
        commands.put(command.getName(), command); 
    }
    
    public void setFileSystem(FileSystem fileSystem) {
        commands.values().forEach(command -> {
            if (command instanceof FileSystemAwareCommand) {
                ((FileSystemAwareCommand) command).setFileSystem(fileSystem);
            }
        });
    }
    
    public String executeCommand(String commandLine) {
        if (commandLine == null || commandLine.isBlank()) {
            return "Comando vazio. Digite um comando válido.";
        }
    
        String[] parts = commandLine.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : null;
    
        if (ctfManager.isPlayerTurn()) {
            if (!ctfManager.performPlayerAction(commandName)) {
                return "Você não pode executar mais comandos neste turno.";
            }
        } else {
            if (!ctfManager.performEnemyAction(commandName)) {
                return "IA não pode executar mais comandos neste turno.";
            }
        }        
    
        Command command = commands.get(commandName);
        if (command == null) {
            return "Comando não reconhecido: " + commandName;
        }
    
        try {
            FileSystem activeFileSystem = ctfManager.getCurrentFileSystem();
            if (command instanceof FileSystemAwareCommand) {
                ((FileSystemAwareCommand) command).setFileSystem(activeFileSystem);
            }
    
            String commandOutput = command.execute(argument);
    
            if (commandOutput == null || commandOutput.isBlank()) {
                return "Comando executado, mas sem saída.";
            }
            
            return commandOutput;
        } catch (Exception e) {
            return "Erro ao executar o comando '" + commandName + "': " + e.getMessage();
        }
    }    
    
    private boolean handleSpecialCommands(String commandName, String argument) {
        switch (commandName) {
            case "skip":
                ctfManager.switchTurn();
                System.out.println("Turno pulado. Agora é o turno do inimigo.");
                return true;

            case "hideflag":
                String hideFlagResult = handleHideFlag(argument);
                System.out.println(hideFlagResult);
                return true;

            default:
                return false;
        }
    }

    private String executeRegisteredCommand(String commandName, String argument) {
        Command command = commands.get(commandName);
        if (command == null) {
            return "Comando não reconhecido: " + commandName;
        }

        boolean actionPerformed = ctfManager.performPlayerAction(commandName);
        if (!actionPerformed) {
            return "Você não pode executar mais comandos neste turno.";
        }

        try {
            return command.execute(argument);
        } catch (Exception e) {
            return "Erro ao executar o comando '" + commandName + "': " + e.getMessage();
        }
    }

    private String handleHideFlag(String argument) {
        if (argument == null || argument.isBlank()) {
            return "Por favor, forneça o nome de um diretório para esconder a flag.";
        }
    
        Directory directory = ctfManager.getCurrentFileSystem().findDirectory(argument);
        if (directory == null) {
            return "Diretório não encontrado: " + argument;
        }
    
        if (directory.hasUserFlag()) {
            return "Já existe uma bandeira no diretório: " + argument;
        }
    
        directory.setHasUserFlag(true);
        ctfManager.getCurrentFileSystem().createFileInSystem(directory, "flag.txt", "Bandeira escondida com sucesso!");
        return "Bandeira escondida com sucesso no diretório: " + argument;
    }
    
    

    public String getCurrentDirectoryPath() {
        FileSystem currentFileSystem = ctfManager.getCurrentFileSystem();
        if (currentFileSystem == null) {
            return "/";
        }
    
        Directory current = currentFileSystem.getCurrentDirectory();
        if (current == null) {
            return "/";
        }
    
        StringBuilder path = new StringBuilder(current.getName());
        while (current.getParent() != null && !"root".equals(current.getParent().getName())) {
            current = current.getParent();
            path.insert(0, current.getName() + "/");
        }
        return "/" + path;
    }
    
}
