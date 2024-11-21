package com.bytecrash.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainGame extends ApplicationAdapter {
    private Texture backgroundTexture;
    private Texture terminalTexture;
    private Texture hackButtonTexture;
    private Texture menuButtonTexture;
    private Texture logoutButtonTexture;
    private SpriteBatch batch;
    private Viewport viewport;
    private Stage stage;

    private static final int VIRTUAL_WIDTH = 1920;
    private static final int VIRTUAL_HEIGHT = 1080;

    // Coordenadas da área preta
    private static final int BLACK_AREA_X = 260;
    private static final int BLACK_AREA_Y = 5;
    private static final int BLACK_AREA_WIDTH = 1658;
    private static final int BLACK_AREA_HEIGHT = 1062;

    @Override
    public void create() {
        // Inicializa os recursos
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("assets/interface/prompt.png"));
        terminalTexture = new Texture(Gdx.files.internal("assets/terminal.png"));
        hackButtonTexture = new Texture(Gdx.files.internal("assets/interface/hack_cut.png"));
        menuButtonTexture = new Texture(Gdx.files.internal("assets/interface/hack_cut.png"));
        logoutButtonTexture = new Texture(Gdx.files.internal("assets/interface/hack_cut.png"));

        // Configura o viewport
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // Configura o Stage para os botões
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        // Criar botões
        ImageButton hackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(hackButtonTexture)));
        ImageButton menuButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(menuButtonTexture)));
        ImageButton logoutButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(logoutButtonTexture)));

        // Escala e tamanho dos botões
        float scale = 4.5f; // Ajuste de escala (proporção dos botões)
        float baseButtonWidth = 96f; // Largura base dos botões
        float baseButtonHeight = 32f; // Altura base dos botões
        float buttonWidth = baseButtonWidth * scale; // Largura escalada
        float buttonHeight = baseButtonHeight * scale; // Altura escalada

        hackButton.setSize(buttonWidth, buttonHeight);
        menuButton.setSize(buttonWidth, buttonHeight);
        logoutButton.setSize(buttonWidth, buttonHeight);

        // Posicionamento fixo na área lateral
        float startX = BLACK_AREA_X / 2f - buttonWidth / 2f; // Centralizado na área preta lateral
        float startY = BLACK_AREA_HEIGHT - 300f; // Posição inicial (abaixo de FLAGS)
        float fixedSpacing = 20f; // Espaçamento fixo entre botões

        // Posicionar botões
        hackButton.setPosition(startX, startY);
        menuButton.setPosition(startX, startY - buttonHeight - fixedSpacing);
        logoutButton.setPosition(startX, startY - 2 * (buttonHeight + fixedSpacing));

        // Adicionar botões ao Stage
        stage.addActor(hackButton);
        stage.addActor(menuButton);
        stage.addActor(logoutButton);
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Renderiza o background
        batch.draw(backgroundTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        // Calcula as dimensões do terminal esquerdo e direito
        int padding = 10;
        float terminalX = BLACK_AREA_X + padding;
        float terminalY = BLACK_AREA_Y + padding;
        float terminalWidth = (BLACK_AREA_WIDTH - 3 * padding) / 2;
        float terminalHeight = BLACK_AREA_HEIGHT - 2 * padding;

        batch.draw(terminalTexture, terminalX, terminalY, terminalWidth, terminalHeight);

        float rightTerminalX = terminalX + terminalWidth + padding;

        float rightTerminalHeight = (terminalHeight - padding) / 2;

        batch.draw(terminalTexture, rightTerminalX, terminalY + rightTerminalHeight + padding, terminalWidth, rightTerminalHeight);

        batch.draw(terminalTexture, rightTerminalX, terminalY, terminalWidth, rightTerminalHeight);

        batch.end();

        // Renderiza os botões no Stage
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        terminalTexture.dispose();
        hackButtonTexture.dispose();
        menuButtonTexture.dispose();
        logoutButtonTexture.dispose();
        stage.dispose();
    }
}
