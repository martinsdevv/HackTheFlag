package com.bytecrash.filesystem;

import java.util.ArrayList;
import java.util.List;

public class Directory {
    private String name;
    private List<File> files = new ArrayList<>();
    private List<Directory> directories = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
