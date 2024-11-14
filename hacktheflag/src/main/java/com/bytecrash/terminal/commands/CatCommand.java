package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.Command;
import com.bytecrash.filesystem.File;

public class CatCommand implements Command {
    private FileSystem fileSystem;

    public CatCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void execute(String argument) {
        File file = fileSystem.findFile(argument);
        if (file != null) {
            System.out.println(file.getContent());
        } else {
            System.out.println("Arquivo não encontrado: " + argument);
        }
    }

    @Override
    public String getName() {
        return "cat";
    }
}
