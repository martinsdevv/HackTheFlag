package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.File;
import com.bytecrash.terminal.FileSystemAwareCommand;

public class LsCommand implements FileSystemAwareCommand {
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

        return output.toString();
    }

    @Override
    public String getName() {
        return "ls";
    }

    @Override
    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String getDescription() {
        return "Lista os arquivos e diretórios no diretório atual. Uso: 'ls'";
    }
}
