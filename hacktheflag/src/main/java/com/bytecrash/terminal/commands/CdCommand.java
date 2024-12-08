package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.Command;
import com.bytecrash.terminal.FileSystemAwareCommand;

public class CdCommand implements FileSystemAwareCommand {
    private FileSystem fileSystem;

    public CdCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String argument) {
        if (argument == null || argument.isEmpty()) {
            return "Uso: cd <diretório>";
        }

        if (argument.equals("..")) {
            Directory parent = fileSystem.getCurrentDirectory().getParent();
            if (parent != null) {
                fileSystem.setCurrentDirectory(parent);
                return "Navegando para o diretório pai.";
            } else {
                return "Você já está na raiz.";
            }
        } else {
            Directory targetDir = fileSystem.findDirectory(argument);
            if (targetDir != null) {
                fileSystem.setCurrentDirectory(targetDir);
                return "Navegando para o diretório " + argument;
            } else {
                return "Diretório não encontrado: " + argument;
            }
        }
    }

    @Override
    public String getName() {
        return "cd";
    }

    @Override
    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
}
