package com.bytecrash.terminal;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.commands.CatCommand;
import com.bytecrash.terminal.commands.CdCommand;
import com.bytecrash.terminal.commands.HideFlagCommand;
import com.bytecrash.terminal.commands.LsCommand;
import com.bytecrash.terminal.commands.MkdirCommand;
import com.bytecrash.terminal.commands.SSHCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final CTFManager ctfManager; 
    private final Map<String, Command> commands = new HashMap<>(); 

    public CommandHandler(CTFManager ctfManager) {
        this.ctfManager = ctfManager;
        registerDefaultCommands();
    }

    /**
     * Registra os comandos padrão no handler.
     */
    private void registerDefaultCommands() {
        registerCommand(new LsCommand(ctfManager.getCurrentFileSystem()));
        registerCommand(new CdCommand(ctfManager.getCurrentFileSystem()));
        registerCommand(new CatCommand(ctfManager.getCurrentFileSystem(), ctfManager));
        registerCommand(new MkdirCommand(ctfManager.getCurrentFileSystem()));
        registerCommand(new HideFlagCommand(ctfManager));
        registerCommand(new SSHCommand(ctfManager));
    }

    /**
     * Permite registrar comandos customizados.
     */
    public void registerCommand(Command command) {
        commands.put(command.getName(), command); 
    }

    /**
     * Executa o comando solicitado pelo jogador.
     */
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
    
        // Obtém o comando registrado
        Command command = commands.get(commandName);
        if (command == null) {
            return "Comando não reconhecido: " + commandName;
        }
    
        try {
            // Atualiza o FileSystem dinâmico antes de executar o comando
            FileSystem activeFileSystem = ctfManager.getCurrentFileSystem();
            if (command instanceof FileSystemAwareCommand) {
                ((FileSystemAwareCommand) command).setFileSystem(activeFileSystem);
            }
    
            // Executa o comando e captura a resposta
            String commandOutput = command.execute(argument);
    
            // Garante que nunca seja nulo
            if (commandOutput == null || commandOutput.isBlank()) {
                return "Comando executado, mas sem saída.";
            }
            
            return commandOutput;
        } catch (Exception e) {
            return "Erro ao executar o comando '" + commandName + "': " + e.getMessage();
        }
    }    
    

    /**
     * Trata comandos especiais como "skip" ou comandos que precisam de lógica extra.
     */
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

    /**
     * Executa um comando registrado no mapa de comandos.
     */
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
            return "/"; // Retorna uma string padrão quando o sistema de arquivos está nulo
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
