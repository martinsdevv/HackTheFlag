package com.bytecrash.terminal;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.commands.CatCommand;
import com.bytecrash.terminal.commands.CdCommand;
import com.bytecrash.terminal.commands.HideFlagCommand;
import com.bytecrash.terminal.commands.LsCommand;
import com.bytecrash.terminal.commands.MkdirCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final CTFManager ctfManager; // Gerenciador do fluxo do jogo
    private final Map<String, Command> commands = new HashMap<>(); // Mapa para armazenar os comandos registrados

    public CommandHandler(CTFManager ctfManager) {
        this.ctfManager = ctfManager;

        // Registro dos comandos
        registerCommand(new LsCommand(ctfManager.getCurrentFileSystem()));
        registerCommand(new CdCommand(ctfManager.getCurrentFileSystem()));
        registerCommand(new CatCommand(ctfManager.getCurrentFileSystem()));
        registerCommand(new MkdirCommand(ctfManager.getCurrentFileSystem()));
        registerCommand(new HideFlagCommand(ctfManager));
    }

    public void registerCommand(Command command) {
        commands.put(command.getName(), command); // Adiciona o comando ao mapa
    }

    public String executeCommand(String commandLine) {
        String[] parts = commandLine.split(" ", 2);
        String commandName = parts[0];
        String argument = parts.length > 1 ? parts[1] : null;
    
        if (!ctfManager.isPlayerTurn()) {
            return "Não é o seu turno.";
        }
    
        if (ctfManager.isSetupPhase() && commandName.equals("ssh")) {
            return "O comando 'ssh' não pode ser usado durante a fase de setup.";
        }
    
        if (commandName.equals("hideflag")) {
            return handleHideFlag(argument);
        }
    
        Command command = commands.get(commandName);
        if (command != null) {
            boolean actionPerformed = ctfManager.performPlayerAction(commandName);
            if (actionPerformed) {
                return command.execute(argument);
            } else {
                return "Você não pode executar mais comandos neste turno.";
            }
        } else if (commandName.equals("skip")) {
            ctfManager.switchTurn();
            return "Turno pulado. É a vez do adversário.";
        } else {
            return "Comando não reconhecido: " + commandName;
        }
    }
    

    private String handleHideFlag(String argument) {
        return new HideFlagCommand(ctfManager).execute(argument);
    }
    

    public String getCurrentDirectoryPath() {
        Directory current = ctfManager.getCurrentFileSystem().getCurrentDirectory();
        StringBuilder path = new StringBuilder(current.getName());

        // Constrói o caminho completo do diretório atual
        while (current.getParent() != null && !current.getParent().getName().equals("root")) {
            current = current.getParent();
            path.insert(0, current.getName() + "/");
        }

        return "/" + path.toString();
    }
}
