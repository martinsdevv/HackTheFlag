package com.bytecrash.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainGame extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

    @Override
    public void create() {
        // Configurar o stage e viewport
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Configurar o fundo
        backgroundTexture = new Texture(Gdx.files.internal("assets/background.png"));
        Image background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);

        // Configurar o skin
        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        // Criar o layout principal
        Table mainTable = new Table();
        mainTable.setFillParent(true); // Garante que o Table ocupe toda a tela
        mainTable.pad(0); // Remove padding extra
        stage.addActor(mainTable);

        // === Painel esquerdo ===
        Table leftPanel = new Table();
        leftPanel.setBackground(skin.getDrawable("default-round"));
        leftPanel.pad(10);
        leftPanel.add(new Label("10:00", skin)).padBottom(15).row();
        leftPanel.add(new Label("Exploits", skin)).padBottom(10).row();
        leftPanel.add(new Label("Defesas", skin));

        // === Painel central ===
        Table centerPanel = new Table();
        centerPanel.setBackground(skin.getDrawable("default-rect"));
        centerPanel.pad(10);
        TextArea terminal = new TextArea("[martins@hacktheflag ~] $ ", skin);
        terminal.setDisabled(true);
        TextField commandInput = new TextField("", skin);
        centerPanel.add(terminal).expand().fill().padBottom(10).row();
        centerPanel.add(commandInput).expandX().fillX().height(40);

        // === Painel direito ===
        Table rightPanel = new Table();
        rightPanel.pad(10);

        // Subpainel de logs (superior)
        Table logsPanel = new Table();
        logsPanel.setBackground(skin.getDrawable("default-round"));
        TextArea logs = new TextArea("LOGS\nInimigo acessou a pasta etc/shadows\nInimigo encontrou a primeira bandeira", skin);
        logs.setDisabled(true);
        logsPanel.add(logs).expand().fill();

        // Subpainel de controle (inferior)
        Table controlPanel = new Table();
        controlPanel.setBackground(skin.getDrawable("default-round"));
        Label controlLabel = new Label("Painel de Controle", skin);
        TextArea controlArea = new TextArea("Gráficos e controle", skin);
        controlArea.setDisabled(true);
        controlPanel.add(controlLabel).padBottom(10).row();
        controlPanel.add(controlArea).expand().fill();

        // Adicionar subpainéis ao painel direito
        rightPanel.add(logsPanel).expand().fill().height(Gdx.graphics.getHeight() * 0.5f).padBottom(10).row();
        rightPanel.add(controlPanel).expand().fill().height(Gdx.graphics.getHeight() * 0.5f);

        // Adicionar painéis ao layout principal
        mainTable.add(leftPanel).width(Gdx.graphics.getWidth() * 0.2f).fillY();
        mainTable.add(centerPanel).width(Gdx.graphics.getWidth() * 0.6f).fillY();
        mainTable.add(rightPanel).width(Gdx.graphics.getWidth() * 0.2f).fillY();
    }

    @Override
    public void render() {
        // Atualizar viewport para ajustar ao tamanho da janela atual
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // Limpa a tela e desenha a stage
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Atualizar o viewport e garantir que os componentes se ajustem ao novo tamanho
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        // Liberar recursos
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
    }
}
