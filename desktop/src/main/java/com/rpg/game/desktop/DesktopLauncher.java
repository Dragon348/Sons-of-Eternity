package com.rpg.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.rpg.game.RPGGame;

/**
 * Лаунчер для Desktop версии игры
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        // Настройки окна
        config.setTitle("Hex RPG");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        
        // Ограничение FPS
        config.setForegroundFPS(60);
        
        // Иконка окна (опционально)
        // config.setWindowIcon("icon.png");
        
        // Запуск игры
        new Lwjgl3Application(new RPGGame(), config);
    }
}
