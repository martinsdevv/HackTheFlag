package com.bytecrash;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bytecrash.game.MainGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // Define a resolução inicial
        config.setTitle("HackTheFlag");
        config.setWindowedMode(1280, 720); // Largura e altura (ajuste conforme necessário)
        config.setResizable(true); // Define se a janela pode ser redimensionada
        config.useVsync(true); // Habilita VSync para evitar tearing gráfico

        new Lwjgl3Application(new MainGame(), config);
    }
}
