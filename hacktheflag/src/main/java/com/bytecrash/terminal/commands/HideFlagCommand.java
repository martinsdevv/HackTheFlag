package com.bytecrash.terminal.commands;

import com.bytecrash.game.CTFManager;
import com.bytecrash.terminal.Command;

public class HideFlagCommand implements Command {
    private final CTFManager ctfManager;

    public HideFlagCommand(CTFManager ctfManager) {
        this.ctfManager = ctfManager;
    }

    @Override
    public String execute(String argument) {
        if (argument == null || argument.isEmpty()) {
            return "Por favor, forneça o nome de um diretório para esconder a flag.";
        }

        boolean success = ctfManager.hideFlag(argument);
        if (success) {
            if (ctfManager.allFlagsHidden()) {
                ctfManager.endSetupPhase();
                return "Flag escondida com sucesso no diretório: " + argument +
                        "\nTodas as bandeiras foram escondidas! O segundo round começou!";
            } else {
                return "Flag escondida com sucesso no diretório: " + argument;
            }
        } else {
            return "Falha ao esconder a bandeira. Verifique o diretório ou se você já usou todas as bandeiras.";
        }
    }

    @Override
    public String getName() {
        return "hideflag";
    }
}
