package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.Command;

public class HideFlagCommand implements Command {
    private FileSystem fileSystem;

    public HideFlagCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String argument) {
        if (argument == null || argument.isEmpty()) {
            return "Por favor, forneça o nome de um diretório para esconder a flag.";
        }

        Directory directory = fileSystem.findDirectory(argument);
        if (directory != null) {
            directory.setHasUserFlag(true); // Marcamos o diretório como contendo a flag
            return "Flag escondida com sucesso no diretório: " + argument;
        } else {
            return "Diretório não encontrado: " + argument;
        }
    }

    @Override
    public String getName() {
        return "hideflag";
    }
}
