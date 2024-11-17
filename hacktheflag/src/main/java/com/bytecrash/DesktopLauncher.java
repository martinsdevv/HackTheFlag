package com.bytecrash;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bytecrash.game.MainGame;;;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("HackTheFlag");
        config.setWindowedMode(800, 600); // Ajuste o tamanho conforme necessário
        config.setResizable(true); // Ou false se não quiser redimensionar
        new Lwjgl3Application(new MainGame(), config);
    }
}
