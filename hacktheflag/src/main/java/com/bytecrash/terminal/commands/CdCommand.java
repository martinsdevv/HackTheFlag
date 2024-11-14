package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.Command;
import com.bytecrash.filesystem.Directory;

public class CdCommand implements Command {
    private FileSystem fileSystem;

    public CdCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void execute(String argument) {
        if (argument.equals("..")) {
            Directory parent = fileSystem.getCurrentDirectory().getParent();
            if (parent != null) {
                fileSystem.setCurrentDirectory(parent);
            } else {
                System.out.println("Você já está na raiz.");
            }
        } else {
            Directory dir = fileSystem.findDirectory(argument);
            if (dir != null) {
                fileSystem.setCurrentDirectory(dir);
            } else {
                System.out.println("Diretório não encontrado: " + argument);
            }
        }
    }

    @Override
    public String getName() {
        return "cd";
    }
}
