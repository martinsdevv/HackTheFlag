package com.bytecrash.filesystem;

import java.util.ArrayList;
import java.util.List;

public class Directory {
    private String name;
    private Directory parent;  // Novo campo para o diretório pai
    private List<File> files = new ArrayList<>();
    private List<Directory> directories = new ArrayList<>();

    // Construtor atualizado para aceitar o diretório pai
    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    // Construtor alternativo para a raiz (sem pai)
    public Directory(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
    }

    public List<File> getFiles() {
        return files;
    }

    public List<Directory> getDirectories() {
        return directories;
    }

    public void addFile(File file) {
        files.add(file);
    }

    public void addDirectory(Directory directory) {
        directories.add(directory);
    }
}
