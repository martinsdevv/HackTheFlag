package com.bytecrash.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bytecrash.terminal.CommandHandler;
import com.bytecrash.terminal.TerminalSimulator;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bytecrash.filesystem.FileSystem;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;


public class MainGame extends ApplicationAdapter {

    private Texture backgroundTexture;
    private Texture terminalTexture;
    private Texture hackButtonTexture;
    private Texture menuButtonTexture;
    private Texture logoutButtonTexture;

    private SpriteBatch batch;

    private Viewport viewport;

    private Stage stage;

    private TextArea terminalTextArea;
    private TerminalSimulator terminalSimulator;
    private Skin skin;

    private ScrollPane terminalScrollPane;
    private boolean shouldScroll = true;

    private static final int VIRTUAL_WIDTH = 1920;
    private static final int VIRTUAL_HEIGHT = 1080;

    // Coordenadas da área preta
    private static final int BLACK_AREA_X = 260;
    private static final int BLACK_AREA_Y = 5;
    private static final int BLACK_AREA_WIDTH = 1658;
    private static final int BLACK_AREA_HEIGHT = 1062;

    private int playerTimeRemaining = 600; // Tempo total do jogador (10 minutos)
    private int machineTimeRemaining = 600; // Tempo total da máquina (10 minutos)
    private Label playerTimerLabel; // Label para o timer do jogador
    private Label machineTimerLabel; // Label para o timer da máquina
    private boolean isPlayerTurn = true; // Controle de turno
    private float timeAccumulator = 0; // Acumula o tempo decorrido para reduzir o cronômetro

    FileSystem playerFileSystem = new FileSystem("player");
    FileSystem machineFileSystem = new FileSystem("machine");

    CTFManager ctfManager = new CTFManager(playerFileSystem, machineFileSystem);

    @Override
    public void create() {
        // Inicializa os recursos
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("assets/interface/prompt.png"));
        terminalTexture = new Texture(Gdx.files.internal("assets/interface/terminal.png"));
        hackButtonTexture = new Texture(Gdx.files.internal("assets/interface/hack_cut.png"));
        menuButtonTexture = new Texture(Gdx.files.internal("assets/interface/hack_cut.png"));
        logoutButtonTexture = new Texture(Gdx.files.internal("assets/interface/hack_cut.png"));
        skin = new Skin(
            Gdx.files.internal("assets/skins/uiskin.json"),
            new TextureAtlas(Gdx.files.internal("assets/skins/uiskin.atlas"))
        );

        
        CommandHandler commandHandler = new CommandHandler(ctfManager);

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

        // Configura o Label (terminal esquerdo)
        float padding = 10;
        float terminalX = BLACK_AREA_X + padding;
        float terminalY = BLACK_AREA_Y + padding;
        float terminalWidth = (BLACK_AREA_WIDTH - 3 * padding) / 2;
        float terminalHeight = BLACK_AREA_HEIGHT - 2 * padding;

        Label terminalLabel = new Label("", skin);
        terminalLabel.setWrap(true); // Permite quebra de linha no texto

        // Configura a tabela para ajustar dinamicamente o tamanho
        Table terminalTable = new Table();
        terminalTable.top().left(); 
        terminalTable.add(terminalLabel).width(terminalWidth).expandX().fillX().pad(10);

        // Configuração do ScrollPane
        terminalScrollPane = new ScrollPane(terminalTable, skin);
        terminalScrollPane.setSize(terminalWidth, terminalHeight);
        terminalScrollPane.setPosition(terminalX, terminalY);
        terminalScrollPane.setScrollingDisabled(true, false); // Apenas rolagem vertical
        terminalScrollPane.setFlickScroll(false); // Evita comportamento inesperado
        terminalScrollPane.setSmoothScrolling(false);

        // Adicionar o ScrollPane ao Stage
        stage.addActor(terminalScrollPane);

        // Configurar o TerminalSimulator com o Label
        terminalSimulator = new TerminalSimulator(commandHandler, terminalScrollPane);
        terminalSimulator.setTerminalDisplay(terminalLabel); // Passar o Label como display do terminal
        Gdx.input.setInputProcessor(terminalSimulator);

        playerTimerLabel = new Label("Jogador: 10:00", skin);
        machineTimerLabel = new Label("Máquina: 10:00", skin);
        playerTimerLabel.setPosition(10, BLACK_AREA_HEIGHT - 50); // Posição do timer do jogador
        machineTimerLabel.setPosition(10, BLACK_AREA_HEIGHT - 100);
        stage.addActor(playerTimerLabel);
        stage.addActor(machineTimerLabel);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {

        float deltaTime = Gdx.graphics.getDeltaTime();
        timeAccumulator += deltaTime;

        // Limpa a tela com a cor preta
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (timeAccumulator >= 1) {
            ctfManager.updateTime(1); // Atualiza o tempo restante no CTFManager
    
            if (!ctfManager.isPlayerTurn()) {
                ctfManager.switchTurn(); // Força a máquina a jogar no turno dela
            }
    
            updateTimerLabels(); // Atualiza os rótulos do tempo na interface
            timeAccumulator = 0;
        }

        // Configura o SpriteBatch para desenhar os elementos do jogo
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

        // Renderiza o terminal direito (dividido em duas partes)
        float rightTerminalX = terminalX + terminalWidth + padding;
        float rightTerminalHeight = (terminalHeight - padding) / 2;

        batch.draw(terminalTexture, rightTerminalX, terminalY + rightTerminalHeight + padding, terminalWidth, rightTerminalHeight);
        batch.draw(terminalTexture, rightTerminalX, terminalY, terminalWidth, rightTerminalHeight);

        batch.end(); // Finaliza o SpriteBatch antes de desenhar o Stage

        // Atualiza e desenha os elementos da interface (Stage)
        stage.act(deltaTime);
        stage.draw();

        // Gerencia o scroll do terminal
        if (shouldScroll && terminalScrollPane != null) {
            terminalScrollPane.layout(); // Recalcula o layout
            terminalScrollPane.setScrollY(terminalScrollPane.getMaxY()); // Move para o final
            shouldScroll = false; // Evita scroll desnecessário
        }
    }

    private void updateTimerLabels() {
        // Atualiza os labels do cronômetro com o tempo restante formatado
        playerTimerLabel.setText("Jogador: " + formatTime(ctfManager.getPlayerTimeRemaining()));
        machineTimerLabel.setText("Máquina: " + formatTime(ctfManager.getMachineTimeRemaining()));
    }
    
    // Formata o tempo restante em minutos e segundos
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    public void setShouldScroll(boolean shouldScroll) {
        this.shouldScroll = shouldScroll;
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
