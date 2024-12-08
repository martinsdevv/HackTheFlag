package com.bytecrash.terminal.commands;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.Command;

public class HideFlagCommand implements Command {
    private final CTFManager ctfManager;

    public HideFlagCommand(CTFManager ctfManager) {
        this.ctfManager = ctfManager;
    }

    @Override
    public String execute(String argument) {
        if (argument == null || argument.isBlank()) {
            return "Por favor, forneça o nome de um diretório.";
        }

        Directory directory = ctfManager.getCurrentFileSystem().findDirectory(argument);
        if (directory == null) {
            return "Diretório não encontrado: " + argument;
        }

        boolean success = ctfManager.hideFlag(directory);
        return success ? "Bandeira escondida com sucesso no diretório: " + argument 
                    : "Falha ao esconder a bandeira.";
    }

    @Override
    public String getName() {
        return "hideflag";
    }
}
