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
    public void execute(String argument) {
        Directory currentDir = fileSystem.getCurrentDirectory();
        System.out.println("Conteúdo de " + currentDir.getName() + ":");
        for (Directory dir : currentDir.getDirectories()) {
            System.out.println("dir  " + dir.getName());
        }
        for (File file : currentDir.getFiles()) {
            System.out.println("file " + file.getName());
        }
    }

    @Override
    public String getName() {
        return "ls";
    }
}
