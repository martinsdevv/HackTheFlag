package com.bytecrash.terminal.commands;


import com.bytecrash.terminal.Command;
import com.bytecrash.terminal.CommandHandler;

public class HelpCommand implements Command {

    private final CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Exibe a lista de comandos disponíveis e suas descricoes. Uso: 'help'";
    }

    @Override
    public String execute(String args) {
        StringBuilder output = new StringBuilder();
        output.append("\n--- Comandos disponíveis ---\n");
        
        commandHandler.getCommands().forEach((name, command) -> {
            output.append(String.format(" - %s: %s\n", name, command.getDescription()));
        });
        
        return output.toString();
    }
}
