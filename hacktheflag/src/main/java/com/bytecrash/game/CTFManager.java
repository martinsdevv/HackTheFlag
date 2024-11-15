package com.bytecrash.game;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.filesystem.Flag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CTFManager {
    private final FileSystem fileSystem;
    private final List<Flag> flags;
    private final Random random;

    public CTFManager(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.flags = new ArrayList<>();
        this.random = new Random();
        initializeFlags();
    }

    // Inicializa as bandeiras com nomes e conteúdos
    private void initializeFlags() {
        flags.add(new Flag("flag1.txt", "Parabéns! Você encontrou a primeira bandeira."));
        flags.add(new Flag("flag2.txt", "Ótimo! Esta é a segunda bandeira."));
        flags.add(new Flag("flag3.txt", "Incrível! Você encontrou a bandeira final!"));
    }

    // Distribui as bandeiras aleatoriamente nos diretórios
    public void distributeFlags() {
        for (Flag flag : flags) {
            Directory randomDir = getRandomDirectory(fileSystem.getRoot());
            fileSystem.createFileInSystem(randomDir, flag.getFileName(), flag.getContent());
        }
    }

    // Retorna um diretório aleatório do sistema de arquivos
    private Directory getRandomDirectory(Directory directory) {
        List<Directory> allDirectories = getAllDirectories(directory);
        return allDirectories.get(random.nextInt(allDirectories.size()));
    }

    // Coleta todos os diretórios em uma lista
    private List<Directory> getAllDirectories(Directory directory) {
        List<Directory> directories = new ArrayList<>();
        directories.add(directory);
        for (Directory dir : directory.getDirectories()) {
            directories.addAll(getAllDirectories(dir));
        }
        return directories;
    }

    // Retorna a lista de bandeiras
    public List<Flag> getFlags() {
        return flags;
    }
}
