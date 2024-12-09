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
    private final String rootPath;
    private final FileContentProvider contentProvider;

    public FileSystem(String rootPath) {
        this.rootPath = rootPath; 
        deleteDirectory(new File(rootPath)); 
    
        root = new Directory("root");
        currentDirectory = root;
        contentProvider = new FileContentProvider();
    
        createRootDirectory();
        setupBasicFileSystem();
    
        System.out.println("-> FileSystem criado: " + this + " com rootPath: " + rootPath);
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
        Directory var = new Directory("var", root);
    
        Directory playerHome = new Directory("player", home);
        Directory enemyHome = new Directory("enemy", home);
    
        Directory playerDocs = new Directory("documents", playerHome);
        Directory playerDownloads = new Directory("downloads", playerHome);
        Directory playerScripts = new Directory("scripts", playerHome);
    
        Directory enemyStrategies = new Directory("strategies", enemyHome);
        Directory enemyNotes = new Directory("notes", enemyHome);
    
        Directory etcSecurity = new Directory("security", etc);
    
        Directory usrBin = new Directory("bin", usr);
        Directory usrLib = new Directory("lib", usr);
        Directory usrShare = new Directory("share", usr);
        Directory usrHelp = new Directory("help", usrShare);

        root.addDirectory(home);
        root.addDirectory(etc);
        root.addDirectory(usr);
        root.addDirectory(var);
    
        home.addDirectory(playerHome);
        home.addDirectory(enemyHome);
    
        playerHome.addDirectory(playerDocs);
        playerHome.addDirectory(playerDownloads);
        playerHome.addDirectory(playerScripts);
    
        enemyHome.addDirectory(enemyStrategies);
        enemyHome.addDirectory(enemyNotes);
    
        etc.addDirectory(etcSecurity);
    
        usr.addDirectory(usrBin);
        usr.addDirectory(usrLib);
        usr.addDirectory(usrShare);
        usrShare.addDirectory(usrHelp);
    
        createPhysicalDirectory(home);
        createPhysicalDirectory(etc);
        createPhysicalDirectory(usr);
        createPhysicalDirectory(var);
    
        createPhysicalDirectory(playerHome);
        createPhysicalDirectory(enemyHome);
        createPhysicalDirectory(playerDocs);
        createPhysicalDirectory(playerDownloads);
        createPhysicalDirectory(playerScripts);
    
        createPhysicalDirectory(enemyStrategies);
        createPhysicalDirectory(enemyNotes);
        createPhysicalDirectory(etcSecurity);
    
        createPhysicalDirectory(usrBin);
        createPhysicalDirectory(usrLib);
        createPhysicalDirectory(usrShare);
        createPhysicalDirectory(usrHelp);
    
        createFileInSystem(playerHome, "welcome.txt", "Bem-vindo ao seu sistema de arquivos, jogador!");
        createFileInSystem(playerHome, "objectives.txt", "1. Esconda sua bandeira.\n2. Encontre a bandeira do inimigo.");
        createFileInSystem(playerDocs, "log.txt", "Registro de atividades do jogador.");
        createFileInSystem(playerDocs, "tasks.md", "- Completar o tutorial\n- Defender o sistema");
        createFileInSystem(playerDownloads, "cheatsheet.pdf", "PDF com dicas e truques.");
        createFileInSystem(playerScripts, "backup.sh", "#!/bin/bash\necho 'Backup iniciado.'");
        createFileInSystem(playerScripts, "scan.sh", "#!/bin/bash\necho 'Scan completo.'");
    
        createFileInSystem(enemyHome, "instructions.txt", "Inimigo: Esconda sua bandeira antes do jogador encontrá-la.");
        createFileInSystem(enemyStrategies, "defense.md", "Planejamento defensivo.");
        createFileInSystem(enemyStrategies, "attack.md", "Planejamento de ataque.");
        createFileInSystem(enemyNotes, "flag_location.txt", "A bandeira está escondida em /usr/share.");
    
        createFileInSystem(etc, "config.txt", contentProvider.getContent("config.txt"));
        createFileInSystem(etc, "system.conf", "system_mode=active\nversion=2.1");
        createFileInSystem(etcSecurity, "passwords.txt", "admin:1234\nroot:root");
        createFileInSystem(etcSecurity, "policy.conf", "Política de segurança: Alta");
    
        createFileInSystem(usrBin, "game.sh", "#!/bin/bash\necho 'Iniciando o jogo...'");
        createFileInSystem(usrBin, "run.sh", "#!/bin/bash\necho 'Executando.'");
        createFileInSystem(usrShare, "notice.txt", "Este é um espaço compartilhado entre o jogador e o inimigo.");
        createFileInSystem(usrHelp, "readme.md", "# Manual\nBem-vindo ao sistema!");
        createFileInSystem(usrHelp, "faq.txt", "Q: Como jogar?\nA: Leia as instruções no diretório home.");

        System.out.println("-> Sistema de arquivos inicial avançado configurado com sucesso!");
    }
    
    public String spawnPowerFile(String fileName, String content) {
        Directory randomDirectory = getRandomDirectory();
        
        if (randomDirectory == null) {
            System.out.println("!!! Não foi possível criar o poder. Nenhum diretório disponível.");
            return "Nenhum diretório disponível";
        }
    
        createFileInSystem(randomDirectory, fileName, content);
        
        return randomDirectory.getPath() + "/" + fileName;
    }
    
    

    public void createPhysicalDirectory(Directory directory) {
        Path dirPath = Paths.get(rootPath, getRelativePath(directory));
        try {
            if (!dirPath.normalize().startsWith(Paths.get(rootPath))) {
                throw new SecurityException("Tentativa de criar diretório fora do root: " + dirPath);
            }
            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("-> Diretório criado: " + dirPath);
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Erro ao criar o diretório: " + dirPath);
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

    private String getRelativePath(Directory directory) {
        StringBuilder path = new StringBuilder();
    
        while (directory != null && !directory.equals(root)) {
            path.insert(0, File.separator + directory.getName());
            directory = directory.getParent();
        }
    
        return path.toString().replaceFirst("^" + File.separator.replace("\\", "\\\\"), "");

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
        System.out.println("-> Buscando diretório no FileSystem: " + this + ", path: " + path);
    
        if (path.startsWith("/")) {
            return findDirectoryAbsolute(path);
        } else {
            for (Directory dir : currentDirectory.getDirectories()) {
                if (dir.getName().equals(path)) {
                    System.out.println("-> Diretório encontrado: " + dir.getName() + " (Referência: " + dir + ")");
                    return dir;
                }
            }
        }
    
        System.out.println("X Diretório não encontrado: " + path);
        return null;
    }
    
    
    
    public Directory findDirectoryAbsolute(String path) {
        if (!path.startsWith("/")) {
            System.out.println("X Caminho absoluto inválido: " + path);
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
                System.out.println("X Não foi possível localizar o diretório '" + part + "' em " + path);
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
            System.out.println("!!! Nenhum diretório disponível para spawnar o poder.");
            return null;
        }
        return allDirectories.get(new Random().nextInt(allDirectories.size()));
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
            System.out.println("X Diretório não encontrado: " + directoryName);
            return false;
        }
    
        System.out.println("🛠 Diretório atual: " + directory + ", Nome: " + directory.getName());
    
        if (isPlayer && directory.hasUserFlag()) {
            System.out.println("!!! Diretório já possui bandeira do jogador.");
            return false;
        } else if (!isPlayer && directory.hasIAFlag()) {
            System.out.println("!!! Diretório já possui bandeira da IA.");
            return false;
        }
    
        if (isPlayer) {
            directory.setHasUserFlag(true);
        } else {
            directory.setHasIAFlag(true);
        }
    
        System.out.println("-> Estado atualizado: hasUserFlag = " + directory.hasUserFlag() + ", hasIAFlag = " + directory.hasIAFlag());
    
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
