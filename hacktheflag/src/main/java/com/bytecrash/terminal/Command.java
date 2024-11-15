package com.bytecrash.terminal;

public interface Command {
    String execute(String argument);  // Alterado de void para String
    String getName();
}
