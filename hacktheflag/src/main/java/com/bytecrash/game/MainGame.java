package com.bytecrash.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bytecrash.terminal.CommandHandler;
import com.bytecrash.filesystem.FileSystem;

public class MainGame extends ApplicationAdapter implements InputProcessor {
    private SpriteBatch batch;
    private BitmapFont font;
    private FileSystem fileSystem;
    private CommandHandler commandHandler;
    private StringBuilder terminalOutput;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        // Inicializa o sistema de arquivos e comandos
        fileSystem = new FileSystem();
        commandHandler = new CommandHandler(fileSystem);

        terminalOutput = new StringBuilder("[martins@hacktheflag ~] $ ");
        Gdx.input.setInputProcessor(this); // Configura o InputProcessor
    }

    @Override
    public void render() {
        // Limpa a tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Desenha o terminal
        batch.begin();
        font.draw(batch, terminalOutput.toString(), 50, Gdx.graphics.getHeight() - 50);
        batch.end();
    }

    public void processCommand(String command) {
        // Executa o comando e atualiza o terminal
        String result = commandHandler.executeCommand(command);
        terminalOutput.append(command).append("\n").append(result).append("\n");
        terminalOutput.append("[martins@hacktheflag ~] $ ");
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    // Métodos obrigatórios da interface InputProcessor
    @Override
    public boolean keyDown(int keycode) {
        return false; // Personalize o comportamento conforme necessário
    }

    @Override
    public boolean keyUp(int keycode) {
        return false; // Personalize o comportamento conforme necessário
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '\r' || character == '\n') { // Quando Enter for pressionado
            String command = terminalOutput.toString().split("\n")[terminalOutput.toString().split("\n").length - 1].replace("[martins@hacktheflag ~] $ ", "");
            processCommand(command);
        } else {
            terminalOutput.append(character); // Adiciona caracteres ao terminal
        }
        return true;
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
        return false; // Implementação padrão
    }
}
