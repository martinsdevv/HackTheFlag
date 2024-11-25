package com.bytecrash.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileSystem {
    private Directory root;
    private Directory currentDirectory;
    private final String rootPath; // Diretório raiz do sistema de arquivos
    private final FileContentProvider contentProvider;
    private final Random random = new Random();

    // Construtor que aceita o caminho do sistema de arquivos
    public FileSystem(String rootPath) {
        this.rootPath = rootPath; // Caminho exclusivo para cada FileSystem
        deleteDirectory(new File(rootPath)); // Limpa o diretório antes de inicializar

        root = new Directory("root");
        currentDirectory = root;
        contentProvider = new FileContentProvider();

        createRootDirectory();
        setupBasicFileSystem();
    }

    private void createRootDirectory() {
        File rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            boolean created = rootDir.mkdirs();
            if (created) {
                System.out.println("Diretório '" + rootPath + "' criado para armazenar o sistema de arquivos.");
            } else {
                System.out.println("Falha ao criar o diretório '" + rootPath + "'.");
            }
        }
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file); // Recursivamente deleta subdiretórios
                    } else {
                        file.delete(); // Deleta arquivo
                    }
                }
            }
            directory.delete(); // Deleta o diretório vazio
            System.out.println("Diretório '" + rootPath + "' apagado com sucesso.");
        }
    }

    private void setupBasicFileSystem() {
        // Diretórios e arquivos padrão
        Directory home = new Directory("home", root);
        Directory etc = new Directory("etc", root);
        Directory usr = new Directory("usr", root);

        root.addDirectory(home);
        root.addDirectory(etc);
        root.addDirectory(usr);

        Directory userDir = new Directory("user", home);
        home.addDirectory(userDir);

        createPhysicalDirectory(home);
        createPhysicalDirectory(etc);
        createPhysicalDirectory(usr);
        createPhysicalDirectory(userDir);

        createFileInSystem(etc, "config.txt", contentProvider.getContent("config.txt"));
        createFileInSystem(usr, "README.md", contentProvider.getContent("README.md"));
        createFileInSystem(userDir, "welcome.txt", contentProvider.getContent("welcome.txt"));
    }

    public void createPhysicalDirectory(Directory directory) {
        Path dirPath = Paths.get(rootPath, getFullPath(directory));
        try {
            if (Files.notExists(dirPath)) {
                Files.createDirectory(dirPath);
                System.out.println("Diretório '" + directory.getName() + "' criado em " + dirPath);
            }
        } catch (IOException e) {
            System.out.println("Erro ao criar o diretório: " + directory.getName());
            e.printStackTrace();
        }
    }

    public void createFileInSystem(Directory directory, String fileName, String content) {
        Path filePath = Paths.get(rootPath, getFullPath(directory), fileName);
        try {
            if (Files.notExists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
                Files.writeString(filePath, content);
                directory.addFile(new com.bytecrash.filesystem.File(fileName, content));
                System.out.println("Arquivo '" + fileName + "' criado em " + filePath);
            } else {
                System.out.println("Arquivo '" + fileName + "' já existe em " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Erro ao criar o arquivo: " + fileName);
            e.printStackTrace();
        }
    }

    public String getFullPath(Directory directory) {
        StringBuilder fullPath = new StringBuilder(directory.getName());
        Directory parent = directory.getParent();
        while (parent != null && !parent.getName().equals("root")) {
            fullPath.insert(0, parent.getName() + File.separator);
            parent = parent.getParent();
        }
        return fullPath.toString();
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(Directory directory) {
        this.currentDirectory = directory;
    }

    public Directory getRoot() {
        return root;
    }

    public Directory findDirectory(String path) {
        if (path.startsWith("/")) {
            return findDirectoryAbsolute(path);
        } else {
            for (Directory dir : currentDirectory.getDirectories()) {
                if (dir.getName().equals(path)) {
                    return dir;
                }
            }
            return null;
        }
    }

    public Directory findDirectoryAbsolute(String path) {
        String[] parts = path.split("/");
        Directory current = root;

        for (String part : parts) {
            if (part.isEmpty()) continue; // Ignorar partes vazias do caminho
            current = current.getDirectories()
                    .stream()
                    .filter(dir -> dir.getName().equals(part))
                    .findFirst()
                    .orElse(null);

            if (current == null) {
                return null; // Caminho inválido
            }
        }
        return current;
    }

    public com.bytecrash.filesystem.File findFile(String fileName) {
        for (com.bytecrash.filesystem.File file : currentDirectory.getFiles()) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public Directory getRandomDirectory() {
        List<Directory> allDirectories = getAllDirectories(getRoot());
        return allDirectories.get(random.nextInt(allDirectories.size()));
    }

    private List<Directory> getAllDirectories(Directory directory) {
        List<Directory> directories = new ArrayList<>();
        directories.add(directory);
        for (Directory dir : directory.getDirectories()) {
            directories.addAll(getAllDirectories(dir));
        }
        return directories;
    }
}
