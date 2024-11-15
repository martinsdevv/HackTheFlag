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
    public String execute(String argument) {
        File file = fileSystem.findFile(argument);
        if (file != null) {
            return file.getContent();
        } else {
            return "Arquivo não encontrado: " + argument;
        }
    }

    @Override
    public String getName() {
        return "cat";
    }
}
