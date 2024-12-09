/*
package com.bytecrash.terminal.commands;

import com.bytecrash.terminal.Command;
import com.bytecrash.game.CTFManager;

public class SSHCommand implements Command {
    private final CTFManager ctfManager;

    public SSHCommand(CTFManager ctfManager) {
        this.ctfManager = ctfManager;
    }

    @Override
    public String execute(String argument) {
        if ("connect".equalsIgnoreCase(argument)) {
            ctfManager.connectToEnemySystem();
            return "Conectado ao sistema inimigo.";
        } else if ("exit".equalsIgnoreCase(argument)) {
            ctfManager.exitEnemySystem();
            return "Desconectado do sistema inimigo.";
        } else {
            return "Uso: ssh connect | ssh exit";
        }
    }


    @Override
    public String getName() {
        return "ssh";
    }
}
*/