package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.FileSystemAwareCommand;

public class CatCommand implements FileSystemAwareCommand {
    private FileSystem fileSystem;
    private final CTFManager ctfManager;

    public CatCommand(FileSystem fileSystem, CTFManager ctfManager) {
        this.fileSystem = fileSystem;
        this.ctfManager = ctfManager;
    }

    @Override
    public String execute(String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            return "Uso: cat <arquivo>";
        }

        com.bytecrash.filesystem.File file = fileSystem.findFile(argument);
        if (file == null) {
            return "Arquivo não encontrado: " + argument;
        }

        if (file.getName().startsWith("pow_")) {
            activatePower(file);
            fileSystem.getCurrentDirectory().removeFile(file);
            return "✨ Poder ativado: " + file.getName();
        }

        if (file.getName().endsWith(".flag")) {
            String winner = ctfManager.isPlayerTurn() ? "Jogador" : "IA";
            ctfManager.endGameWithFlag(file.getName(), winner);
            return "🏳️ Bandeira encontrada: " + file.getName();
        }

        return file.getContent();
    }

    private void activatePower(com.bytecrash.filesystem.File powerFile) {
        String powerName = powerFile.getName();
    
        if (powerName.equals("pow_extra_moves.sh")) {
            if (ctfManager.isPlayerTurn()) {
                ctfManager.incrementPlayerCommands(5);
                System.out.println("🌀 Jogador ganhou 5 movimentos extras!");
            } else {
                ctfManager.incrementEnemyCommands(5);
                System.out.println("🤖 IA ganhou 5 movimentos extras!");
            }
        }
    
    }

    @Override
    public String getName() {
        return "cat";
    }

    @Override
    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
}
