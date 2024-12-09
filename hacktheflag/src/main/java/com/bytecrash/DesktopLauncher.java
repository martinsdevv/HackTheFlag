package com.bytecrash;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bytecrash.game.MainGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setTitle("HackTheFlag");
        config.setWindowedMode(1280, 720);
        config.setResizable(true);
        config.useVsync(true);

        new Lwjgl3Application(new MainGame(), config);
    }
}
