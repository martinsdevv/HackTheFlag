package com.bytecrash.game;

import com.bytecrash.enemy.MachineLogic;
import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.filesystem.Flag;
import com.bytecrash.terminal.CommandHandler;

import java.util.ArrayList;
import java.util.List;

public class CTFManager {
    private final FileSystem playerFileSystem;
    private final FileSystem machineFileSystem;
    private final List<Flag> playerFlags; // Bandeiras do jogador
    private final List<Flag> machineFlags; // Bandeiras da máquina
    private boolean isPlayerTurn = true;
    private int playerTimeRemaining = 600; // Tempo total em segundos para o jogador
    private int machineTimeRemaining = 600; // Tempo total em segundos para a máquina
    private int playerMovesRemaining = 5; // Movimentos disponíveis por turno para o jogador
    private int machineMovesRemaining = 5; // Movimentos disponíveis por turno para a máquina
    private boolean setupPhase = true; // Indica se estamos na fase inicial de esconder bandeiras
    private final MachineLogic machineLogic;
    private final CommandHandler commandHandler = new CommandHandler(this);

    public CTFManager(FileSystem playerFileSystem, FileSystem machineFileSystem) {
        this.playerFileSystem = playerFileSystem;
        this.machineFileSystem = machineFileSystem;
        this.machineLogic = new MachineLogic(machineFileSystem, commandHandler);
        this.playerFlags = new ArrayList<>();
        this.machineFlags = new ArrayList<>();
    }

    public boolean hideFlag(String directoryPath) {
        if (!setupPhase) {
            return false; // Apenas permitido na fase inicial
        }
    
        FileSystem currentFileSystem = isPlayerTurn ? playerFileSystem : machineFileSystem;
    
        // Verifica se o diretório especificado existe
        Directory dir = currentFileSystem.findDirectory(directoryPath);
        if (dir == null) {
            System.out.println("Diretório não encontrado: " + directoryPath);
            return false;
        }
    
        if (isPlayerTurn) {
            // Verifica se já existe uma flag no diretório
            if (dir.hasUserFlag()) {
                System.out.println("Já existe uma bandeira no diretório: " + directoryPath);
                return false;
            }
    
            // Marca o diretório como contendo uma bandeira do jogador
            dir.setHasUserFlag(true);
            currentFileSystem.createFileInSystem(dir, "flag.txt", "Bandeira do jogador!");
            System.out.println("Jogador escondeu a bandeira em: " + directoryPath);
            switchTurn(); // Passa o turno para a máquina
            return true;
        } else {
            // Máquina esconde a bandeira em um diretório aleatório
            machineLogic.hideFlag("flag");
            switchTurn(); // Passa o turno para o jogador
            return true;
        }
    }
    

    // Finaliza a fase de esconder bandeiras
    public void endSetupPhase() {
        setupPhase = false;
        System.out.println("Fase de esconder bandeiras concluída. O jogo começou!");
    }

    public void switchTurn() {
        if (setupPhase) {
            isPlayerTurn = !isPlayerTurn; // Alterna o turno durante o setup
            if (!isPlayerTurn) {
                machineLogic.hideFlag("flag"); // Máquina esconde bandeira
            }
            if (allFlagsHidden()) {
                endSetupPhase();
                isPlayerTurn = true; // Jogador começa o segundo turno
                System.out.println("Todas as bandeiras foram escondidas! Vamos começar o segundo turno.");
            }
        } else {
            isPlayerTurn = !isPlayerTurn; // Alterna entre jogador e máquina
            if (!isPlayerTurn) {
                machineLogic.executeTurn(); // Máquina joga no turno dela
            }
        }
    }
    
    

    public void updateTime(int deltaTime) {
        if (setupPhase) {
            // Durante o setup, o tempo não é contado
            return;
        }
    
        if (isPlayerTurn) {
            playerTimeRemaining -= deltaTime;
            if (playerTimeRemaining <= 0 || playerMovesRemaining <= 0) {
                System.out.println("Tempo do jogador esgotado ou movimentos terminados. Passando para a máquina.");
                switchTurn();
            }
        } else {
            machineTimeRemaining -= deltaTime;
            if (machineTimeRemaining <= 0 || machineMovesRemaining <= 0) {
                System.out.println("Tempo da máquina esgotado ou movimentos terminados. Passando para o jogador.");
                switchTurn();
            }
        }
    }
    

    public boolean performPlayerAction(String command) {
        if (!isPlayerTurn) {
            System.out.println("Não é o turno do jogador.");
            return false;
        }
    
        if (setupPhase) {
            // No setup, não há limite de movimentos
            System.out.println("Jogador executou: " + command);
            return true;
        }
    
        if (playerMovesRemaining <= 0) {
            System.out.println("Você não pode executar mais comandos neste turno. Passando o turno para a máquina.");
            switchTurn();
            return false;
        }
    
        System.out.println("Jogador executou: " + command);
        playerMovesRemaining--;
        if (playerMovesRemaining <= 0) {
            System.out.println("Movimentos esgotados. Passando o turno para a máquina.");
            switchTurn();
        }
        return true;
    }
    
    public void performMachineAction() {
        if (setupPhase) {
            // Máquina esconde bandeiras sem limite
            Directory randomDir = machineFileSystem.getRandomDirectory();
            hideFlag(randomDir.getPath());
        } else {
            // Máquina realiza ações no jogo
            System.out.println("Máquina executa uma ação.");
            machineMovesRemaining--;
            if (machineMovesRemaining <= 0) {
                System.out.println("Movimentos da máquina esgotados. Passando o turno para o jogador.");
                switchTurn();
            }
        }
    }
    
    

    public FileSystem getCurrentFileSystem() {
        return isPlayerTurn ? playerFileSystem : machineFileSystem;
    }

    public int getPlayerTimeRemaining() {
        return playerTimeRemaining;
    }
    
    public int getMachineTimeRemaining() {
        return machineTimeRemaining;
    }
    

    // Verifica se todas as bandeiras foram escondidas
    public boolean allFlagsHidden() {
        return playerFlags.isEmpty() && machineFlags.isEmpty();
    }

    public boolean isSetupPhase() {
        return setupPhase;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
}
