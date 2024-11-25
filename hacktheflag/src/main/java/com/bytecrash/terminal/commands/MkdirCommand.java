package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.Command;

public class MkdirCommand implements Command {
    private FileSystem fileSystem;

    public MkdirCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            return "Uso: mkdir <nome_do_diretório>";
        }

        Directory newDir = new Directory(argument, fileSystem.getCurrentDirectory());
        fileSystem.getCurrentDirectory().addDirectory(newDir);
        fileSystem.createPhysicalDirectory(newDir);
        return "Diretório '" + argument + "' criado com sucesso.";
    }

    @Override
    public String getName() {
        return "mkdir";
    }
}
