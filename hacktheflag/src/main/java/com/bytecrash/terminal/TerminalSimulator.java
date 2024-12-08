package com.bytecrash.terminal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.bytecrash.game.CTFManager;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class TerminalSimulator implements InputProcessor {
    private final CommandHandler commandHandler;
    private final StringBuilder terminalOutput;
    private Label terminalDisplay;
    private String currentInput;
    private final ScrollPane terminalScrollPane;
    private CTFManager ctfManager = new CTFManager(null, null, null, null);

    public TerminalSimulator(CommandHandler commandHandler, ScrollPane terminalScrollPane) {
        this.commandHandler = commandHandler;
        this.terminalOutput = new StringBuilder("[martins@hacktheflag ~] $ ");
        this.currentInput = "";
        this.terminalScrollPane = terminalScrollPane; // Armazena o ScrollPane
    }

    public void setTerminalDisplay(Label terminalDisplay) {
        this.terminalDisplay = terminalDisplay;
        this.terminalDisplay.setText(terminalOutput.toString());
    }
    

    public void appendToTerminal(String result) {
        String hostname = ctfManager.isInEnemySystem() ? "enemy" : "player";
        terminalOutput.append(currentInput).append("\n")
                      .append(result).append("\n")
                      .append("[")
                      .append(hostname)
                      .append("@hacktheflag ")
                      .append(commandHandler.getCurrentDirectoryPath())
                      .append("] $ ");
        currentInput = ""; // Limpa o comando atual
    
        // Atualiza o texto no Label
        if (terminalDisplay != null) {
            terminalDisplay.setText(terminalOutput.toString());
        }
    
        // Força o scroll para o final
        Gdx.app.postRunnable(() -> {
            if (terminalScrollPane != null) {
                terminalScrollPane.layout();
                terminalScrollPane.scrollTo(0, 0, 0, terminalScrollPane.getScrollHeight());
            }
        });
    }    

    @Override
    public boolean keyTyped(char character) {
        if (character == '\r' || character == '\n') {
            // Processa o comando atual
            String result = commandHandler.executeCommand(currentInput);
            appendToTerminal(result);
        } else if (character == '\b') {
            // Backspace
            if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            }
        } else {
            // Qualquer outro caractere
            currentInput += character;
        }

        // Atualiza a exibição com o comando sendo digitado
        terminalDisplay.setText(terminalOutput.toString() + currentInput);

        if (terminalScrollPane != null) {
            terminalScrollPane.layout(); // Atualiza o layout do ScrollPane
            Gdx.app.postRunnable(() -> terminalScrollPane.scrollTo(0, 0, 0, terminalScrollPane.getMaxY()));
        }

        return true;
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        // Este método é exigido pela interface InputProcessor, mas não será usado.
        return false;
    }
}
