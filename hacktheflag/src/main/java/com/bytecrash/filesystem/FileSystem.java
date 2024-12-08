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

    public FileSystem(String rootPath) {
        this.rootPath = rootPath; 
        deleteDirectory(new File(rootPath)); 
    
        root = new Directory("root");
        currentDirectory = root;
        contentProvider = new FileContentProvider();
    
        createRootDirectory();
        setupBasicFileSystem();
    
        System.out.println("🆕 FileSystem criado: " + this + " com rootPath: " + rootPath);
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
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
            System.out.println("Diretório '" + rootPath + "' apagado com sucesso.");
        }
    }

    private void setupBasicFileSystem() {
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
        System.out.println("🔍 Buscando diretório no FileSystem: " + this + ", path: " + path);
    
        if (path.startsWith("/")) {
            return findDirectoryAbsolute(path);
        } else {
            for (Directory dir : currentDirectory.getDirectories()) {
                if (dir.getName().equals(path)) {
                    System.out.println("✅ Diretório encontrado: " + dir.getName() + " (Referência: " + dir + ")");
                    return dir;
                }
            }
        }
    
        System.out.println("❌ Diretório não encontrado: " + path);
        return null;
    }
    
    
    
    public Directory findDirectoryAbsolute(String path) {
        if (!path.startsWith("/")) {
            System.out.println("❌ Caminho absoluto inválido: " + path);
            return null;
        }
    
        String[] parts = path.split("/");
        Directory current = root;
    
        for (String part : parts) {
            if (part.isEmpty()) continue;
            current = current.getDirectories()
                    .stream()
                    .filter(dir -> dir.getName().equals(part))
                    .findFirst()
                    .orElse(null);
    
            if (current == null) {
                System.out.println("❌ Não foi possível localizar o diretório '" + part + "' em " + path);
                return null;
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
        if (allDirectories.isEmpty()) {
            System.out.println("⚠️  Nenhum diretório disponível para esconder a bandeira.");
            return null;
        }
        Directory randomDirectory = allDirectories.get(new Random().nextInt(allDirectories.size()));
        
        // ⚠️ Se o diretório sorteado for "root", tente novamente
        if (randomDirectory.getName().equalsIgnoreCase("root")) {
            System.out.println("⚠️  Diretório raiz não pode ser escolhido. Sorteando novamente...");
            return getRandomDirectory();
        }
        return randomDirectory;
    }
    

    public List<Directory> getAllDirectories(Directory directory) {
        List<Directory> directories = new ArrayList<>();
        directories.add(directory);
        for (Directory dir : directory.getDirectories()) {
            directories.addAll(getAllDirectories(dir));
        }
        return directories;
    }

    public boolean hideFlag(String directoryName, boolean isPlayer) {
        Directory directory = findDirectory(directoryName);
        if (directory == null) {
            System.out.println("❌ Diretório não encontrado: " + directoryName);
            return false;
        }
    
        // Verifica o estado do diretório
        System.out.println("🛠 Diretório atual: " + directory + ", Nome: " + directory.getName());
    
        // Verifica se já há uma bandeira escondida
        if (isPlayer && directory.hasUserFlag()) {
            System.out.println("⚠️ Diretório já possui bandeira do jogador.");
            return false;
        } else if (!isPlayer && directory.hasIAFlag()) {
            System.out.println("⚠️ Diretório já possui bandeira da IA.");
            return false;
        }
    
        // Esconde a bandeira
        if (isPlayer) {
            directory.setHasUserFlag(true);
        } else {
            directory.setHasIAFlag(true);
        }
    
        // Confirma o estado após a alteração
        System.out.println("✅ Estado atualizado: hasUserFlag = " + directory.hasUserFlag() + ", hasIAFlag = " + directory.hasIAFlag());
    
        // Cria o arquivo no sistema
        createFileInSystem(directory, "flag.txt", isPlayer ? "Bandeira do jogador!" : "Bandeira da IA!");
        System.out.println((isPlayer ? "Jogador" : "IA") + " escondeu a bandeira no diretório: " + directoryName);
    
        return true;
    }    
    
    
    public boolean hasHiddenFlag() {
        return hasHiddenFlag(root);
    }
    
    private boolean hasHiddenFlag(Directory directory) {
        if (directory.hasUserFlag()) {
            return true;
        }
        for (Directory subDirectory : directory.getDirectories()) {
            if (hasHiddenFlag(subDirectory)) {
                return true;
            }
        }
        return false;
    }
    
}
