package com.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rpg.game.RPGGame;

/**
 * Главное меню игры
 */
public class MainMenuScreen extends BaseScreen {
    private Stage stage;
    private BitmapFont font;
    
    public MainMenuScreen(RPGGame game) {
        super(game);
        this.stage = new Stage(new ScreenViewport());
        this.font = new BitmapFont();
        
        initializeUI();
    }
    
    private void initializeUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        // Заголовок
        Label titleLabel = new Label("HEX RPG", new Label.LabelStyle(font, null));
        table.add(titleLabel).colspan(2).padBottom(50).row();
        
        // Кнопка новой игры
        TextButton newGameButton = new TextButton("Новая игра", new TextButton.TextButtonStyle());
        newGameButton.addListener(click -> {
            game.returnToGlobalMap();
            return true;
        });
        table.add(newGameButton).width(200).height(50).pad(10).row();
        
        // Кнопка выхода
        TextButton exitButton = new TextButton("Выход", new TextButton.TextButtonStyle());
        exitButton.addListener(click -> {
            Gdx.app.exit();
            return true;
        });
        table.add(exitButton).width(200).height(50).pad(10);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    
    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }
}
