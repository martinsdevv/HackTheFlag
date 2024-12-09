package com.bytecrash.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bytecrash.terminal.CommandHandler;
import com.bytecrash.terminal.TerminalSimulator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bytecrash.filesystem.FileSystem;


public class MainGame extends ApplicationAdapter {

    private Texture backgroundTexture;
    private Texture terminalTexture;
    private Texture hackButtonTexture;
    private Texture menuButtonTexture;
    private Texture logoutButtonTexture;

    private SpriteBatch batch;

    private Viewport viewport;

    private Stage stage;

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

    private ScrollPane logScrollPane;
    private Label playerTimerLabel;
    private Label logLabel;
    private float timeAccumulator = 0;
    private List<String> logs = new ArrayList<>();

    FileSystem playerFileSystem = new FileSystem("player");
    //FileSystem machineFileSystem = new FileSystem("machine");

    private boolean gameEnded = false;

    private CTFManager ctfManager;

    @Override
    public void create() {
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

        ctfManager = new CTFManager(playerFileSystem, stage, this);

        
        CommandHandler commandHandler = new CommandHandler(ctfManager);

        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        // Criar botões
        // ImageButton hackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(hackButtonTexture)));
        // ImageButton menuButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(menuButtonTexture)));
        // ImageButton logoutButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(logoutButtonTexture)));

        // hackButton.setSize(buttonWidth, buttonHeight);
        // menuButton.setSize(buttonWidth, buttonHeight);
        // logoutButton.setSize(buttonWidth, buttonHeight);

        // Posicionar botões
        //hackButton.setPosition(startX, startY);
        // menuButton.setPosition(startX, startY - buttonHeight - fixedSpacing);
        // logoutButton.setPosition(startX, startY - 2 * (buttonHeight + fixedSpacing));

        // Adicionar botões ao Stage
        // stage.addActor(hackButton);
        // stage.addActor(menuButton);
        // stage.addActor(logoutButton);

        // Configura o Label (terminal esquerdo)
        float padding = 10;
        float terminalX = BLACK_AREA_X + padding;
        float terminalY = BLACK_AREA_Y + padding;
        float terminalWidth = (BLACK_AREA_WIDTH - 3 * padding) / 2;
        float terminalHeight = BLACK_AREA_HEIGHT - 2 * padding;

        Label.LabelStyle terminalStyle = new Label.LabelStyle();
        terminalStyle.font = skin.getFont("default-font");
        terminalStyle.fontColor = new Color(0, 1, 0, 1);


        Label terminalLabel = new Label("", terminalStyle);
        terminalLabel.setWrap(true);

        Table terminalTable = new Table();
        terminalTable.top().left(); 
        terminalTable.add(terminalLabel).width(terminalWidth).expandX().fillX().pad(10);

        terminalScrollPane = new ScrollPane(terminalTable, skin);
        terminalScrollPane.setSize(terminalWidth, terminalHeight);
        terminalScrollPane.setPosition(terminalX, terminalY);
        terminalScrollPane.setScrollingDisabled(true, false);
        terminalScrollPane.setFlickScroll(false);
        terminalScrollPane.setSmoothScrolling(false);

        stage.addActor(terminalScrollPane);

        terminalSimulator = new TerminalSimulator(commandHandler, terminalScrollPane);
        terminalSimulator.setTerminalDisplay(terminalLabel);
        Gdx.input.setInputProcessor(terminalSimulator);

        Label.LabelStyle timerStyle = new Label.LabelStyle();
        timerStyle.font = skin.getFont("default-font");
        timerStyle.fontColor = new Color(1, 0, 0, 1);

        playerTimerLabel = new Label("10:00", timerStyle);
        float timerX = BLACK_AREA_X / 2.3f - playerTimerLabel.getWidth() / 2f;
        float timerY = BLACK_AREA_HEIGHT - 215f;
        playerTimerLabel.setPosition(timerX, timerY);
        playerTimerLabel.setFontScale(1.8f);
        stage.addActor(playerTimerLabel);

        // LOGS

        Label.LabelStyle logStyle = new Label.LabelStyle();
        logStyle.font = skin.getFont("default-font");
        logStyle.fontColor = new Color(0, 1, 0, 1);

        logLabel = new Label("LOGS\n", logStyle);
        logLabel.setWrap(true);

        Table logTable = new Table();
        logTable.top().left();
        logTable.add(logLabel).expandX().fillX();

        logScrollPane = new ScrollPane(logTable, skin);
        logScrollPane.setSize(400, 500);
        logScrollPane.setPosition(VIRTUAL_WIDTH - 815, VIRTUAL_HEIGHT - 530);
        stage.addActor(logScrollPane);

        addLog("Jogo iniciado!");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void showEndScreen(String message) {
        gameEnded = true;
    
        stage.clear();
        Label.LabelStyle endStyle = new Label.LabelStyle();
        endStyle.font = skin.getFont("default-font");
        endStyle.fontColor = Color.WHITE;
    
        Label endLabel = new Label(message, endStyle);
        endLabel.setFontScale(2);
        endLabel.setPosition(
            (VIRTUAL_WIDTH - endLabel.getPrefWidth() * endLabel.getFontScaleX()) / 2f, 
            (VIRTUAL_HEIGHT - endLabel.getPrefHeight() * endLabel.getFontScaleY()) / 2f
        );
    
        stage.addActor(endLabel);
    }
    

    @Override
    public void render() {

        if (gameEnded) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            stage.act();
            stage.draw();
            return;
        }

        float deltaTime = Gdx.graphics.getDeltaTime();
        timeAccumulator += deltaTime;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (timeAccumulator >= 1) {
            if (ctfManager.isPlayerTurn()) {
                ctfManager.decrementPlayerTime();
                updateTimerLabel();
                if (ctfManager.getPlayerTimeRemaining() <= 0) {
                    addLog("Tempo do jogador esgotado!");
                    ctfManager.switchTurn();
                }
            } else {
                ctfManager.incrementAITime();
            }
    
            timeAccumulator = 0;
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        

        batch.draw(backgroundTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        int padding = 10;
        float terminalX = BLACK_AREA_X + padding;
        float terminalY = BLACK_AREA_Y + padding;
        float terminalWidth = (BLACK_AREA_WIDTH - 3 * padding) / 2;
        float terminalHeight = BLACK_AREA_HEIGHT - 2 * padding;

        float rightTerminalX = terminalX + terminalWidth + padding;
        float rightTerminalHeight = (terminalHeight - padding) / 2;

        batch.draw(terminalTexture, rightTerminalX, terminalY + rightTerminalHeight + padding, terminalWidth, rightTerminalHeight);
        batch.draw(terminalTexture, rightTerminalX, terminalY, terminalWidth, rightTerminalHeight);

        logScrollPane.layout();
        logScrollPane.setScrollY(logScrollPane.getMaxY());

        batch.end();

        stage.act(deltaTime);
        stage.draw();

        if (shouldScroll && terminalScrollPane != null) {
            terminalScrollPane.layout();
            terminalScrollPane.setScrollY(terminalScrollPane.getMaxY());
            shouldScroll = false;
        }
    }

    private void updateTimerLabel() {
        playerTimerLabel.setText(formatTime(ctfManager.getPlayerTimeRemaining()));
    }
    
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void addLog(String message) {
        logs.add("[" + formatTime(ctfManager.getPlayerTimeRemaining()) + "] - " + message);
        StringBuilder logText = new StringBuilder("LOGS\n");
        for (String log : logs) {
            logText.append(log).append("\n");
        }
        logLabel.setText(logText.toString());

        if (message.contains("venceu o jogo")) {
            Gdx.app.exit();
        }
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