package com.bytecrash.game;

import com.bytecrash.filesystem.Directory;
import com.bytecrash.filesystem.FileSystem;
import com.bytecrash.terminal.CommandHandler;
import com.bytecrash.enemy.EnemyAI;
import com.bytecrash.enemy.LlamaAPI;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class CTFManager {
    private final FileSystem playerFileSystem;
    private final FileSystem enemyFileSystem;
    private final CommandHandler commandHandler;
    private final EnemyAI enemyAI;

    private static final int IA_COMMAND_PADDING = 5;
    private static final int MAX_TURNS = 20;
    private static final int MAX_COMMANDS = 5;

    private FileSystem currentFileSystem;

    private int aiTimeCounter = 0;

    private boolean isPlayerTurn = true;
    private boolean setupPhase = true;
    private boolean playerFlagHidden = false;
    private boolean enemyFlagHidden = false;
    private boolean isInEnemySystem = false;

    private int playerTimeRemaining = 600;
    private int playerTurns = 0;
    private int enemyTurns = 0;

    private final MainGame mainGame;

    private int playerCommands = 0;
    private int enemyCommands = 0;

    LlamaAPI llamaAPI = new LlamaAPI("http://localhost:1234/v1/chat/completions");

    public CTFManager(FileSystem playerFileSystem, FileSystem enemyFileSystem, Stage stage, MainGame mainGame) {
        this.playerFileSystem = playerFileSystem;
        this.enemyFileSystem = enemyFileSystem;
        this.commandHandler = new CommandHandler(this);
        this.enemyAI = new EnemyAI(enemyFileSystem, commandHandler, llamaAPI, this);
        this.mainGame = mainGame;
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
            System.out.println("⚠️ Tentativa de esconder bandeira fora da fase de configuração.");
            return false; // Bloqueia o comando fora da fase de configuração
        }
    
        boolean success = false;
    
        if (isPlayerTurn) {
            success = playerFileSystem.hideFlag(directoryPath.getName(), true);
            if (success) {
                playerFlagHidden = true;
                playerFileSystem.createFileInSystem(directoryPath, "playerFlag.txt", "Jogador escondeu a bandeira!");
                System.out.println("Jogador escondeu a bandeira em: " + directoryPath);
            }
        } else {
            success = enemyFileSystem.hideFlag(directoryPath.getName(), false);
            if (success) {
                enemyFlagHidden = true;
                enemyFileSystem.createFileInSystem(directoryPath, "machineFlag.txt", "IA escondeu a bandeira!");
                System.out.println("IA escondeu a bandeira em: " + directoryPath);
            }
        }
    
        if (success) {
            checkEndOfSetupPhase(); // Verifica se ambos esconderam a bandeira
            switchTurn(); // Troca o turno automaticamente
        }
    
        return success;
    }

    private void checkEndOfSetupPhase() {
        if (playerFlagHidden && enemyFlagHidden) {
            System.out.println("🚀 Fase de configuração finalizada. O jogo começou!");
            endSetupPhase(); // Chama explicitamente o fim da fase de configuração
        } else if (playerFlagHidden && !enemyFlagHidden && isPlayerTurn) {
            System.out.println("⚠️ Jogador escondeu a bandeira. Agora é a vez da IA.");
            switchTurn(); // Troca para o turno da IA
        }
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
            playerCommands = 0; // Reseta comandos do jogador
            playerTurns++;
        } else {
            enemyCommands = 0; // Reseta comandos da IA
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
        System.out.println("Turno do jogador iniciado.");
    }

    public void startEnemyTurn() {
        System.out.println("Turno da IA iniciado.");
    
        // Reinicia o contador de comandos da IA
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
    
        // Executa múltiplos comandos da IA em sequência
        executeAICommands();
    }
    
    
    public void updateTime(int deltaTime) {
        if (setupPhase) return;
    
        playerTimeRemaining -= deltaTime; // Decrementa o tempo, em vez de sobrescrevê-lo
    
        if (playerTimeRemaining <= 0) {
            System.out.println("Tempo do jogador esgotado.");
            switchTurn();
        }
    }

    public void incrementAITime() {
        if (isPlayerTurn || setupPhase) return; // IA só age no turno dela e fora da fase de setup
    
        if (enemyCommands < MAX_COMMANDS) {
            executeAICommands(); // Executa comandos enquanto a IA ainda tem ações disponíveis
        }
    
        if (enemyCommands >= MAX_COMMANDS) {
            mainGame.addLog("IA completou seu turno.");
            switchTurn(); // Finaliza o turno da IA
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
    
        if (playerCommands >= MAX_COMMANDS) {
            mainGame.addLog("Limite de comandos do jogador atingido neste turno!");
            switchTurn(); // Troca o turno
            return false;
        }
    
        playerCommands++;
        mainGame.addLog("Comando do jogador: " + command);
    
        if (playerCommands >= MAX_COMMANDS) {
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
    
        if (enemyCommands >= MAX_COMMANDS) {
            mainGame.addLog("Limite de comandos da IA atingido neste turno.");
            return false;
        }
    
        enemyCommands++;
        mainGame.addLog("IA executou o comando: " + command);
        System.out.println("Comando atual da IA: " + enemyCommands + "/" + MAX_COMMANDS);
    
        if (enemyCommands >= MAX_COMMANDS) {
            mainGame.addLog("IA atingiu o limite de comandos neste turno. Turno encerrado.");
            return false; // Indica que o turno acabou
        }
    
        return true;
    }
    
    
    private void executeAICommands() {
        System.out.println("🤖 IA começando a executar comandos...");
    
        while (enemyCommands < MAX_COMMANDS) {
            // Solicita à IA que execute um comando
            boolean success = enemyAI.performAction();
    
            if (!success) {
                mainGame.addLog("IA falhou ao executar o comando.");
            } else {
                mainGame.addLog("IA completou o comando com sucesso.");
            }
    
            // Incrementa o contador após cada comando bem-sucedido
            enemyCommands++;
            System.out.println("🤖 Comando da IA executado. Total: " + enemyCommands + "/" + MAX_COMMANDS);
    
            // Verifica se atingiu o limite de comandos
            if (enemyCommands >= MAX_COMMANDS) {
                break;
            }
        }
    
        // Após executar os comandos, encerra o turno
        System.out.println("🤖 IA atingiu o limite de comandos para este turno.");
        switchTurn(); // Alterna para o próximo turno
    }
    
    
    public boolean isInEnemySystem() {
        return isInEnemySystem;
    }

    public FileSystem getCurrentFileSystem() {
        if (isInEnemySystem) {
            return enemyFileSystem; // Garante que o FileSystem inimigo será retornado
        } else {
            return playerFileSystem; // Garante que o FileSystem do jogador será retornado
        }
    }
    

    public void connectToEnemySystem() {
        if (isInEnemySystem) {
            System.out.println("⚠️ Você já está no sistema de arquivos inimigo.");
            return;
        }
        isInEnemySystem = true;
        currentFileSystem = enemyFileSystem; // Troca para o sistema inimigo
        System.out.println("🛠 Conectado ao sistema inimigo.");
    }

    public void exitEnemySystem() {
        if (!isInEnemySystem) {
            System.out.println("⚠️ Você já está no seu sistema de arquivos.");
            return;
        }
        isInEnemySystem = false;
        currentFileSystem = playerFileSystem; // Troca de volta para o sistema do jogador
        System.out.println("🛠 Desconectado do sistema inimigo.");
    }

    public void declareVictory(String winner) {
        mainGame.addLog("🎉 " + winner + " venceu o jogo!");
        endGame(); // Finaliza o jogo
    }
    

    public boolean allFlagsHidden() {
        return playerFileSystem.hasHiddenFlag() && enemyFileSystem.hasHiddenFlag();
    }

    public FileSystem getPlayerFileSystem() {
        return playerFileSystem;
    }

    public FileSystem getEnemyFileSystem() {
        return enemyFileSystem;
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
