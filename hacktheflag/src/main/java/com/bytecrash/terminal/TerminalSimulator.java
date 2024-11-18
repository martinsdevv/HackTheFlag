package com.bytecrash.terminal;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

public class TerminalSimulator implements InputProcessor {
    private final CommandHandler commandHandler;
    private final StringBuilder terminalOutput;
    private TextArea terminalDisplay;
    private String currentInput;

    public TerminalSimulator(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        this.terminalOutput = new StringBuilder("[martins@hacktheflag ~] $ ");
        this.currentInput = "";
    }

    public void setTerminalDisplay(TextArea terminalDisplay) {
        this.terminalDisplay = terminalDisplay;
        this.terminalDisplay.setText(terminalOutput.toString());
    }

    private void appendToTerminal(String result) {
        // Adiciona o resultado do comando sem sobrescrever o prompt
        terminalOutput.append(currentInput).append("\n").append(result).append("\n[martins@hacktheflag ~] $ ");
        currentInput = ""; // Limpa o comando atual
        terminalDisplay.setText(terminalOutput.toString()); // Atualiza a exibição
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
        return true;
    }


    // Implementação dos métodos obrigatórios da interface InputProcessor

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
