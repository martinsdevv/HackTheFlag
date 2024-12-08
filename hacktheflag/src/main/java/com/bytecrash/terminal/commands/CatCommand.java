package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.Command;
import com.bytecrash.terminal.FileSystemAwareCommand;
import com.bytecrash.filesystem.File;

public class CatCommand implements FileSystemAwareCommand {
    private FileSystem fileSystem;
    private final CTFManager ctfManager;

    public CatCommand(FileSystem fileSystem, CTFManager ctfManager) {
        this.fileSystem = fileSystem;
        this.ctfManager = ctfManager;
    }

    @Override
    public String execute(String argument) {
        if (argument == null || argument.isEmpty()) {
            return "Uso: cat <arquivo>";
        }

        File file = fileSystem.findFile(argument);
        if (file == null) {
            return "Arquivo não encontrado: " + argument;
        }

        // Verificar se o arquivo é a bandeira inimiga
        if (argument.equals("flag.txt")) {
            if (ctfManager.isInEnemySystem()) {
                ctfManager.declareVictory("Jogador");
                return "🎉 Você encontrou a bandeira inimiga! Você venceu o jogo!";
            } else if (!ctfManager.isPlayerTurn()) {
                ctfManager.declareVictory("IA");
                return "A IA encontrou sua bandeira. Você perdeu o jogo.";
            }
        }

        return file.getContent();
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
