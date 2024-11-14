package com.bytecrash.terminal;

public interface Command {
    void execute(String argument);
    String getName();
}
