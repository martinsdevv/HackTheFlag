package com.bytecrash.filesystem;

import java.util.ArrayList;
import java.util.List;

public class FileSystem {
    private Directory root;
    private Directory currentDirectory;

    public FileSystem() {
        root = new Directory("root");
        currentDirectory = root;
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public void changeDirectory(String name) {
        for (Directory dir : currentDirectory.getDirectories()) {
            if (dir.getName().equals(name)) {
                currentDirectory = dir;
                return;
            }
        }
        System.out.println("Diretório não encontrado: " + name);
    }

    public void listFiles() {
        for (Directory dir : currentDirectory.getDirectories()) {
            System.out.println("dir  " + dir.getName());
        }
        for (File file : currentDirectory.getFiles()) {
            System.out.println("file " + file.getName());
        }
    }
}
