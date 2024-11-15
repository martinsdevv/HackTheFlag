package com.bytecrash.filesystem;

import com.bytecrash.game.CTFManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystem {
    private Directory root;
    private Directory currentDirectory;
    private final String rootPath = "filesystem"; // Diretório raiz do sistema de arquivos
    private final FileContentProvider contentProvider;
    private final CTFManager ctfManager;

    public FileSystem() {
        // Limpa o diretório `filesystem` antes de inicializar o sistema de arquivos
        deleteDirectory(Paths.get(rootPath).toFile());

        root = new Directory("root");
        currentDirectory = root;
        contentProvider = new FileContentProvider();
        ctfManager = new CTFManager(this);

        // Criação do diretório de armazenamento físico para o sistema de arquivos
        createRootDirectory();

        // Criação de uma estrutura de arquivos padrão
        setupBasicFileSystem();

        // Distribuição de bandeiras aleatórias nos diretórios
        ctfManager.distributeFlags();
    }

    private void createRootDirectory() {
        File rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            boolean created = rootDir.mkdirs();
            if (created) {
                System.out.println("Diretório 'filesystem' criado para armazenar o sistema de arquivos.");
            } else {
                System.out.println("Falha ao criar o diretório 'filesystem'.");
            }
        }
    }

    // Método para deletar o diretório `filesystem` e todos os seus conteúdos
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) { // Verifica se o diretório contém arquivos
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file); // Chama recursivamente para subdiretórios
                    } else {
                        file.delete(); // Deleta arquivo
                    }
                }
            }
            directory.delete(); // Deleta o diretório após esvaziá-lo
            System.out.println("Diretório 'filesystem' apagado com sucesso.");
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
    
        // Criação da estrutura física dos diretórios
        createPhysicalDirectory(rootPath, home);
        createPhysicalDirectory(rootPath, etc);
        createPhysicalDirectory(rootPath, usr);
        createPhysicalDirectory(rootPath + "/home", userDir);
    
        // Criação de arquivos padrão no sistema de arquivos virtual e físico
        createFileInSystem(etc, "config.txt", contentProvider.getContent("config.txt"));
        createFileInSystem(usr, "README.md", contentProvider.getContent("README.md"));
        createFileInSystem(userDir, "welcome.txt", contentProvider.getContent("welcome.txt"));
    }
    

    public void createPhysicalDirectory(String parentPath, Directory directory) {
        Path dirPath = Paths.get(rootPath, parentPath, directory.getName());
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

    public Directory findDirectory(String name) {
        for (Directory dir : currentDirectory.getDirectories()) {
            if (dir.getName().equals(name)) {
                return dir;
            }
        }
        return null;
    }

    public com.bytecrash.filesystem.File findFile(String fileName) {
        for (com.bytecrash.filesystem.File file : currentDirectory.getFiles()) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }
}
