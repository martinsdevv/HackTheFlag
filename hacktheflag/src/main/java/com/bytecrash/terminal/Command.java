package com.bytecrash.terminal;

public interface Command {
    String execute(String argument);
    String getName();
    String getDescription();
}
