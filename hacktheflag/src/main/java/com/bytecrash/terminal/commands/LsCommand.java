package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.File;
import com.bytecrash.terminal.Command;

public class LsCommand implements Command {
    private FileSystem fileSystem;

    public LsCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String argument) {
        Directory currentDir = fileSystem.getCurrentDirectory();
        StringBuilder output = new StringBuilder("Conteúdo de " + currentDir.getName() + ":\n");

        for (Directory dir : currentDir.getDirectories()) {
            output.append("dir  ").append(dir.getName()).append("\n");
        }
        for (File file : currentDir.getFiles()) {
            output.append("file ").append(file.getName()).append("\n");
        }

        return output.toString();  // Retorna a saída como uma String
    }

    @Override
    public String getName() {
        return "ls";
    }
}
