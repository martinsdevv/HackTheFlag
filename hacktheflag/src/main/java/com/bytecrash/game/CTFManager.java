package com.bytecrash.game;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.CommandHandler;
import com.bytecrash.enemy.EnemyAI;
import com.bytecrash.enemy.LlamaAPI;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class CTFManager {
    private final FileSystem playerFileSystem;
    private final CommandHandler commandHandler;
    private final EnemyAI enemyAI;

    private static final int MAX_TURNS = 20;

    private FileSystem currentFileSystem;

    private boolean isPlayerTurn = true;
    private boolean setupPhase = true;
    private boolean playerFlagHidden = false;
    private boolean enemyFlagHidden = false;
    private boolean isInEnemySystem = false;

    private int playerTimeRemaining = 600;
    private int playerTurns = 0;
    private int enemyTurns = 0;
    private int enemyMaxCommands = 5;
    private int playerMaxCommands = 7;

    private final MainGame mainGame;

    private int playerCommands = 0;
    private int enemyCommands = 0;

    LlamaAPI llamaAPI = new LlamaAPI("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");

    public CTFManager(FileSystem playerFileSystem, Stage stage, MainGame mainGame) {
        this.playerFileSystem = playerFileSystem;
        this.commandHandler = new CommandHandler(this);
        this.enemyAI = new EnemyAI(playerFileSystem, commandHandler, llamaAPI, this);
        this.mainGame = mainGame;

        this.currentFileSystem = playerFileSystem;
    }

    public void startSetupPhase() {
        setupPhase = true;
        isPlayerTurn = true;
        System.out.println("Fase de configuração iniciada.");
    }

    public void endSetupPhase() {
        setupPhase = false;
        System.out.println("Fase de configuração finalizada. O jogo começou!");
        startPlayerTurn();
    }

    public boolean hideFlag(Directory directoryPath) {
        if (!setupPhase) {
            mainGame.addLog("Erro: Não é permitido esconder a bandeira fora da fase de configuração.");
            System.out.println("!!! Tentativa de esconder bandeira fora da fase de configuração.");
            return false;
        }
    
        boolean success = false;
    
        if (isPlayerTurn) {
            if (playerFileSystem.findFile("player.flag") != null) {
                mainGame.addLog("Erro: O jogador já escondeu sua bandeira!");
                return false;
            }
            success = playerFileSystem.hideFlag(directoryPath.getName(), true);
            if (success) {
                playerFlagHidden = true;
                playerFileSystem.createFileInSystem(directoryPath, "player.flag", "Jogador escondeu a bandeira!");
                System.out.println("-> Jogador escondeu a bandeira em: " + directoryPath.getPath());
            }
        } else {
            if (playerFileSystem.findFile("machine.flag") != null) {
                mainGame.addLog("Erro: A IA já escondeu sua bandeira!");
                return false;
            }
            success = playerFileSystem.hideFlag(directoryPath.getName(), false);
            if (success) {
                enemyFlagHidden = true;
                playerFileSystem.createFileInSystem(directoryPath, "machine.flag", "IA escondeu a bandeira!");
                System.out.println("-> IA escondeu a bandeira em: " + directoryPath.getPath());
            }
        }
    
        if (success) {
            checkEndOfSetupPhase();
            switchTurn();
        }
    
        return success;
    }

    private void checkEndOfSetupPhase() {
        if (playerFlagHidden && enemyFlagHidden) {
            System.out.println("-> Fase de configuração finalizada. O jogo começou!");
            endSetupPhase();
        } else if (playerFlagHidden && !enemyFlagHidden && isPlayerTurn) {
            System.out.println("!!! Jogador escondeu a bandeira. Agora é a vez da IA.");
            switchTurn();
        }
    }   
    
    public void incrementPlayerCommands(int additionalCommands) {
        playerMaxCommands += additionalCommands;
        mainGame.addLog("Player usou um poder!");
        System.out.println("-> Jogador agora possui " + playerCommands + " movimentos neste turno.");
    }
    
    public void incrementEnemyCommands(int additionalCommands) {
        enemyMaxCommands += additionalCommands;
        mainGame.addLog("Inimigo usou um poder!");
        System.out.println("-> IA agora possui " + enemyCommands + " movimentos neste turno.");
    }

    public void spawnPowersAtTurnStart() {
        String powerName = "pow_extra_moves.sh";
        String powerContent = "Este script concede 5 movimentos extras!";
        
        String playerSpawnLocation = playerFileSystem.spawnPowerFile(powerName, powerContent);
        System.out.println("-> Poder criado para o jogador em: " + playerSpawnLocation);
    
        String enemySpawnLocation = playerFileSystem.spawnPowerFile(powerName, powerContent);
        System.out.println("-> Poder criado para a IA em: " + enemySpawnLocation);
    }    

    public void switchTurn() {
        if (setupPhase) {
            if (isPlayerTurn && playerFlagHidden) {
                isPlayerTurn = false;
                System.out.println("Turno da IA iniciado para esconder a bandeira.");
                startEnemyTurn();
            } else if (!isPlayerTurn && enemyFlagHidden) {
                isPlayerTurn = true;
                System.out.println("Turno do jogador iniciado para esconder a bandeira.");
            }
            checkEndOfSetupPhase();
            return;
        }
    
        if (isPlayerTurn) {
            playerCommands = 0;
            playerTurns++;
        } else {
            enemyCommands = 0;
            enemyTurns++;
        }
    
        isPlayerTurn = !isPlayerTurn;
    
        if (isPlayerTurn) {
            mainGame.addLog("Turno do jogador iniciado.");
        } else {
            mainGame.addLog("Turno da IA iniciado.");
            startEnemyTurn();
        }
    }
    

    public void decrementPlayerTime() {
        if (isPlayerTurn) {
            playerTimeRemaining--;
        }
    }

    public void endGame() {
        mainGame.addLog("O jogo terminou!");
        if (playerTurns >= MAX_TURNS) {
            mainGame.addLog("O jogador venceu!");
        } else if (enemyTurns >= MAX_TURNS) {
            mainGame.addLog("A máquina venceu!");
        }
    }

    public void startPlayerTurn() {
        isPlayerTurn = true;
        spawnPowersAtTurnStart();
        System.out.println("Turno do jogador iniciado.");
    }

    public void startEnemyTurn() {
        System.out.println("Turno da IA iniciado.");
        spawnPowersAtTurnStart();
    
        enemyCommands = 0; 
    
        if (setupPhase) {
            boolean success = enemyAI.decideAndHideFlag();
            if (!success) {
                System.out.println("IA falhou ao esconder a bandeira.");
            } else {
                enemyFlagHidden = true;
                System.out.println("IA escondeu a bandeira com sucesso.");
            }
        
            checkEndOfSetupPhase();
            return;
        }
    
        executeAICommands();
    }
    
    
    public void updateTime(int deltaTime) {
        if (setupPhase) return;
    
        playerTimeRemaining -= deltaTime;
    
        if (playerTimeRemaining <= 0) {
            System.out.println("Tempo do jogador esgotado.");
            switchTurn();
        }
    }

    public void incrementAITime() {
        if (isPlayerTurn || setupPhase) return;
    
        if (enemyCommands < enemyMaxCommands) {
            executeAICommands();
        }
    
        if (enemyCommands >= enemyMaxCommands) {
            mainGame.addLog("IA completou seu turno.");
            switchTurn();
        }
    }
    
    
    
    public boolean performPlayerAction(String command) {
        if (!isPlayerTurn) {
            mainGame.addLog("Erro: Não é o turno do jogador.");
            return false;
        }
    
        if (command.startsWith("hideflag") && !setupPhase) {
            mainGame.addLog("Erro: O comando 'hideflag' só pode ser usado na fase de configuração.");
            return false;
        }
    
        if (playerCommands >= playerMaxCommands) {
            mainGame.addLog("Limite de comandos do jogador atingido neste turno!");
            switchTurn();
            return false;
        }
    
        playerCommands++;
        mainGame.addLog("Comando do jogador: " + command);
    
        if (playerCommands >= playerMaxCommands) {
            mainGame.addLog("Limite de comandos do jogador atingido! Turno encerrado.");
            switchTurn();
        }
    
        return true;
    }   
     
    public boolean performEnemyAction(String command) {
        if (isPlayerTurn) {
            mainGame.addLog("Erro: Não é o turno da IA.");
            return false;
        }
    
        if (enemyCommands >= enemyMaxCommands) {
            mainGame.addLog("Limite de comandos da IA atingido neste turno.");
            return false;
        }
    
        enemyCommands++;
        mainGame.addLog("IA executou o comando: " + command);
        System.out.println("Comando atual da IA: " + enemyCommands + "/" + enemyMaxCommands);
    
        if (enemyCommands >= enemyMaxCommands) {
            mainGame.addLog("IA atingiu o limite de comandos neste turno. Turno encerrado.");
            return false;
        }
    
        return true;
    }
    
    
    private void executeAICommands() {
        System.out.println("-> IA começando a executar comandos...");
    
        while (enemyCommands < enemyMaxCommands) {
            boolean success = enemyAI.performAction();
    
            if (!success) {
                mainGame.addLog("IA falhou ao executar o comando.");
            } else {
                mainGame.addLog("IA completou o comando com sucesso.");
            }
    
            enemyCommands++;
            System.out.println("-> Comando da IA executado. Total: " + enemyCommands + "/" + enemyMaxCommands);
    
            if (enemyCommands >= enemyMaxCommands) {
                break;
            }
        }
    
        System.out.println("-> IA atingiu o limite de comandos para este turno.");
        switchTurn();
    }
    
    
    public boolean isInEnemySystem() {
        return isInEnemySystem;
    }

    public FileSystem getCurrentFileSystem() {
        return playerFileSystem;
    }    
    
    /* 
    public void connectToEnemySystem() {
        if (isInEnemySystem) {
            System.out.println("⚠️ Você já está no sistema de arquivos inimigo.");
            return;
        }
        isInEnemySystem = true;
        currentFileSystem = enemyFileSystem;
        System.out.println("🛠 Conectado ao sistema de arquivos inimigo.");
    }
    
    public void exitEnemySystem() {
        if (!isInEnemySystem) {
            System.out.println("⚠️ Você já está no seu sistema de arquivos.");
            return;
        }
        isInEnemySystem = false;
        currentFileSystem = playerFileSystem;
        System.out.println("🛠 Desconectado do sistema de arquivos inimigo. Voltando ao sistema do jogador.");
    }    

    */

    public void declareVictory(String winner) {
        mainGame.addLog("🎉 " + winner + " venceu o jogo!");
        endGame();
    }
    

    public boolean allFlagsHidden() {
        boolean playerFlagFound = playerFileSystem.findFile("player.flag") != null;
        boolean machineFlagFound = playerFileSystem.findFile("machine.flag") != null;
    
        return playerFlagFound && machineFlagFound;
    }

    public void endGameWithFlag(String flagName, String winner) {
        mainGame.showEndScreen("Bandeira encontrada: " + flagName + "\nObrigado por jogar!\nVencedor: " + winner);
    }
    
    

    public FileSystem getPlayerFileSystem() {
        return playerFileSystem;
    }

    public FileSystem getEnemyFileSystem() {
        return playerFileSystem;
    }

    public boolean isSetupPhase() {
        return setupPhase;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public int getPlayerTimeRemaining() {
        return playerTimeRemaining;
    }
}
