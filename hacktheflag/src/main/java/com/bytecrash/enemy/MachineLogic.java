package com.bytecrash.enemy;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.CommandHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MachineLogic {
    private final FileSystem machineFileSystem;
    private final CommandHandler commandHandler;
    private final Random random;

    public MachineLogic(FileSystem machineFileSystem, CommandHandler commandHandler) {
        this.machineFileSystem = machineFileSystem;
        this.commandHandler = commandHandler;
        this.random = new Random();
    }

    // Método para esconder a flag no primeiro turno
    public void hideFlag(String flagName) {
        Directory randomDir = getRandomDirectory();
        if (randomDir != null) {
            machineFileSystem.createFileInSystem(randomDir, flagName, "Conteúdo da flag");
            System.out.println("Máquina escondeu a flag no diretório: " + randomDir.getName());
        } else {
            System.out.println("Máquina falhou ao esconder a flag. Nenhum diretório disponível.");
        }
    }

    // Lógica para o turno da máquina
    public void executeTurn() {
        System.out.println("Máquina está decidindo sua ação...");
        int action = random.nextInt(2); // 0 para atacar, 1 para defender

        if (action == 0) {
            performAttack();
        } else {
            performDefense();
        }
    }

    // Ação de ataque
    private void performAttack() {
        System.out.println("Máquina está atacando...");
        // Aqui a lógica de ataque deve procurar flags no sistema do jogador
        String commandResult = commandHandler.executeCommand("ls"); // Exemplo de comando
        System.out.println("Máquina executou 'ls': " + commandResult);
    }

    // Ação de defesa
    private void performDefense() {
        System.out.println("Máquina está se defendendo...");
        String dirName = "defense_" + random.nextInt(100);
        machineFileSystem.getCurrentDirectory().addDirectory(new Directory(dirName, machineFileSystem.getCurrentDirectory()));
        System.out.println("Máquina criou o diretório: " + dirName + " como medida defensiva.");
    }

    // Obtem um diretório aleatório para ações
    private Directory getRandomDirectory() {
        List<Directory> allDirectories = getAllDirectories(machineFileSystem.getRoot());
        return allDirectories.isEmpty() ? null : allDirectories.get(random.nextInt(allDirectories.size()));
    }

    public List<Directory> getAllDirectories(Directory directory) {
        List<Directory> directories = new ArrayList<>();
        directories.add(directory); // Adiciona o diretório atual
    
        // Cria uma cópia da lista para evitar o ConcurrentModificationException
        List<Directory> subDirs = new ArrayList<>(directory.getDirectories());
        for (Directory subDir : subDirs) {
            directories.addAll(getAllDirectories(subDir)); // Chamada recursiva com subdiretórios
        }
    
        return directories;
    }
    
}
