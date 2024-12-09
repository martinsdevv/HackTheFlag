package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.Command;

public class MkdirCommand implements Command {
    private FileSystem fileSystem;
    private final CTFManager ctfManager;

    public MkdirCommand(FileSystem fileSystem, CTFManager ctfManager) {
        this.fileSystem = fileSystem;
        this.ctfManager = ctfManager;
    }

    @Override
    public String execute(String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            return "Uso: mkdir <nome_do_diretório>";
        }

        Directory newDir = new Directory(argument, fileSystem.getCurrentDirectory());
        fileSystem.getCurrentDirectory().addDirectory(newDir);
        FileSystem currentFileSystem = ctfManager.getCurrentFileSystem();
        currentFileSystem.createPhysicalDirectory(newDir);

        return "Diretório '" + argument + "' criado com sucesso.";
    }

    @Override
    public String getName() {
        return "mkdir";
    }
}
