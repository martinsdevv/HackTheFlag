package com.bytecrash.filesystem;

import java.util.ArrayList;
import java.util.List;

public class Directory {
    private String name;
    private Directory parent;
    private List<File> files = new ArrayList<>();
    private List<Directory> directories = new ArrayList<>();

    private boolean hasUserFlag = false;
    private boolean hasIAFlag = false;

    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
        this.hasUserFlag = false;
    }

    public Directory(String name) {
        this(name, null);
    }

    public String getPath() {
        StringBuilder path = new StringBuilder(name);
        Directory current = parent;
        while (current != null) {
            // ⚠️ Corrigir essa parte: Remova "root" da montagem do caminho
            if (!current.getName().equalsIgnoreCase("root")) {
                path.insert(0, current.name + "/");
            }
            current = current.parent;
        }
        return "/" + path;
    }

    @SuppressWarnings("unlikely-arg-type")
    public boolean containsFile(String fileName) {
        if (files == null || files.isEmpty()) {
            return false;
        }
        return files.contains(fileName);
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
        directory.parent = this; // Atualiza o pai corretamente
    }

    public boolean hasUserFlag() {
        return hasUserFlag;
    }

    public void setHasUserFlag(boolean hasUserFlag) {
        this.hasUserFlag = hasUserFlag;
    }

    public void setHasIAFlag(boolean hasIAFlag) {
        this.hasIAFlag = hasIAFlag;
        System.out.println("🔧 Estado hasIAFlag alterado para: " + this.hasIAFlag + " no diretório: " + this.name);
    }
    
    public boolean hasIAFlag() {
        System.out.println("📋 Verificando hasIAFlag: " + this.hasIAFlag + " no diretório: " + this.name);
        return this.hasIAFlag;
    }
    
}
