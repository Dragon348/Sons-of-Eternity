package com.rpg.game.ui;

import com.badlogic.gdx.Screen;
import com.rpg.game.RPGGame;

/**
 * Базовый класс для всех экранов игры
 */
public abstract class BaseScreen implements Screen {
    protected RPGGame game;
    
    public BaseScreen(RPGGame game) {
        this.game = game;
    }
    
    @Override
    public void resize(int width, int height) {
        // По умолчанию ничего не делаем
    }
    
    @Override
    public void pause() {
        // По умолчанию ничего не делаем
    }
    
    @Override
    public void resume() {
        // По умолчанию ничего не делаем
    }
}
